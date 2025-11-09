package com.lms.eduspring.controller;

import com.lms.eduspring.model.ChatMessage;
import com.lms.eduspring.model.ChatSession;
import com.lms.eduspring.service.ChatService;
import com.lms.eduspring.service.JwtService;
import com.lms.eduspring.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final JwtService jwtService;
    private final UserService userService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${OPENAI_API_KEY:}")
    private String openAiApiKey;

    public ChatController(ChatService chatService,
                          JwtService jwtService,
                          UserService userService) {
        this.chatService = chatService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/message")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> handleChatMessage(
            @RequestParam Long userId,
            @RequestParam(required = false) Long sessionId,
            @RequestParam String content
    ) {
        ChatSession session = chatService.processUserMessage(userId, sessionId, content);
        return ResponseEntity.ok(Map.of(
                "sessionId", session.getId(),
                "messages", session.getMessages()
        ));
    }

    @GetMapping("/{sessionId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getChatMessages(@PathVariable Long sessionId) {
        List<ChatMessage> messages = chatService.getMessages(sessionId);
        return ResponseEntity.ok(Map.of("messages", messages));
    }

    @PostMapping("/ask")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> askOpenAi(
            @RequestParam Long userId,
            @RequestParam(required = false) Long sessionId,
            @RequestParam String message
    ) {
        try {
            // Save user's message and create session if needed
            ChatSession session = chatService.processUserMessage(userId, sessionId, message);

            // Prepare OpenAI request
            String openAiUrl = "https://api.openai.com/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAiApiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-3.5-turbo");
            body.put("messages", List.of(
                    Map.of("role", "system", "content", "You are EduSpring AI assistant, a helpful learning mentor."),
                    Map.of("role", "user", "content", message)
            ));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(openAiUrl, entity, Map.class);

            // Extract AI response
            String aiReply = (String) ((Map) ((Map) ((List) response.getBody().get("choices")).get(0))
                    .get("message"))
                    .get("content");

            // Save AI reply under same session
            chatService.processUserMessage(userId, session.getId(), aiReply);

            return ResponseEntity.ok(Map.of(
                    "sessionId", session.getId(),
                    "userMessage", message,
                    "aiReply", aiReply
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to contact AI service"));
        }
    }

    @GetMapping("/sessions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserSessions(@RequestParam Long userId) {
        List<ChatSession> sessions = chatService.getSessionsForUser(userId);
        return ResponseEntity.ok(Map.of("sessions", sessions));
    }

    @GetMapping("/sessions/{sessionId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getSessionMessages(
            @RequestParam Long userId,
            @PathVariable Long sessionId
    ) {
        try {
            List<ChatMessage> messages = chatService.getMessagesForUserSession(userId, sessionId);
            return ResponseEntity.ok(Map.of("messages", messages));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }


}
