package com.lms.eduspring.controller;

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
                          JwtService jwtService, UserService userService) {
        this.chatService = chatService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getChatMessages() {
        List<String> messages = chatService.getMessages();
        return ResponseEntity.ok(Map.of("messages", messages));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> postMessage(@RequestBody(required = false) Map<String, String> payload) {
        if (payload == null || !payload.containsKey("message") || payload.get("message").isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Message cannot be empty"));
        }

        String userMessage = payload.get("message");

        // ✅ Save user message (your existing behavior)
        chatService.saveMessage(userMessage);

        try {
            // ✅ Prepare OpenAI API request
            String openAiUrl = "https://api.openai.com/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAiApiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", "You are EduSpring AI assistant, a helpful learning mentor."),
                    Map.of("role", "user", "content", userMessage)
            ));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(openAiUrl, entity, Map.class);

            // ✅ Extract assistant’s reply
            String aiReply = (String) ((Map) ((Map) ((List) response.getBody().get("choices")).get(0))
                    .get("message"))
                    .get("content");

            // ✅ Optionally, save AI reply too
            chatService.saveMessage("AI: " + aiReply);

            return ResponseEntity.ok(Map.of(
                    "userMessage", userMessage,
                    "aiReply", aiReply
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to contact AI service"));
        }
    }
}
