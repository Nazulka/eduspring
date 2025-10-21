package com.lms.eduspring.controller;

import com.lms.eduspring.dto.ChatRequest;
import com.lms.eduspring.dto.ChatResponse;
import com.lms.eduspring.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String reply = chatService.chat(request.getMessage());
        return new ChatResponse(reply);
    }
}
