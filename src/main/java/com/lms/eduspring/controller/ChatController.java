package com.lms.eduspring.controller;

import com.lms.eduspring.model.ChatMessage;
import com.lms.eduspring.model.ChatSession;
import com.lms.eduspring.service.ChatService;
import com.lms.eduspring.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${OPENAI_API_KEY:}")
    private String openAiApiKey;

    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    // Extract current user from JWT
    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userService.findByUsername(username).getId();
    }

    // 1️⃣ List conversations (supports test override)
    @GetMapping("/conversations")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ChatSession>> getAllConversations(
            @RequestParam(required = false) Long userId
    ) {
        Long resolvedUser = (userId != null) ? userId : getCurrentUserId();
        return ResponseEntity.ok(chatService.getSessionsForUser(resolvedUser));
    }

    // 2️⃣ Get messages for conversation (supports test override)
    @GetMapping("/conversations/{conversationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getConversationMessages(
            @PathVariable Long conversationId,
            @RequestParam(required = false) Long userId
    ) {
        Long resolvedUser = (userId != null) ? userId : getCurrentUserId();

        List<ChatMessage> messages =
                chatService.getMessagesForUserSession(resolvedUser, conversationId);

        Map<String, Object> response = new HashMap<>();
        response.put("id", conversationId);
        response.put("messages", messages);

        return ResponseEntity.ok(response);
    }

    // 3️⃣ Send message → save user → AI → save AI
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestBody Map<String, Object> payload,
            @RequestParam(required = false) Long userId
    ) {

        Long resolvedUser = (userId != null) ? userId : getCurrentUserId();

        Long conversationId = payload.get("conversationId") == null ? null :
                Long.valueOf(payload.get("conversationId").toString());

        String userMessage = payload.get("message").toString();

        ChatSession session =
                chatService.processUserMessage(resolvedUser, conversationId, userMessage);

        String aiReply = callOpenAI(userMessage);

        chatService.processAiMessage(session.getId(), aiReply);

        Map<String, Object> response = new HashMap<>();
        response.put("conversationId", session.getId());
        response.put("reply", aiReply);

        return ResponseEntity.ok(response);
    }

    // OpenAI helper
    private String callOpenAI(String message) {
        try {
            String url = "https://api.openai.com/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAiApiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", "You are EduSpring AI assistant."));
            messages.add(Map.of("role", "user", "content", message));
            requestBody.put("messages", messages);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, entity, Map.class);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) response.getBody().get("choices");

            @SuppressWarnings("unchecked")
            Map<String, Object> msgObj =
                    (Map<String, Object>) choices.get(0).get("message");

            return msgObj.get("content").toString();

        } catch (Exception e) {
            return "AI did not respond.";
        }
    }
}
