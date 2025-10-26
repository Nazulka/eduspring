package com.lms.eduspring.controller;

import com.lms.eduspring.service.ChatService;
import com.lms.eduspring.service.JwtService;
import com.lms.eduspring.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final JwtService jwtService;
    private final UserService userService;

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
            Map<String, String> error = new HashMap<>();
            error.put("error", "Message cannot be empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        chatService.saveMessage(payload.get("message"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Message posted successfully"));
    }
}
