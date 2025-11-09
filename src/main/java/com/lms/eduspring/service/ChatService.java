package com.lms.eduspring.service;

import com.lms.eduspring.model.*;
import com.lms.eduspring.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatService(ChatSessionRepository chatSessionRepository,
                       ChatMessageRepository chatMessageRepository,
                       UserRepository userRepository) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    public ChatSession processUserMessage(Long userId, Long chatSessionId, String userMessage) {
        ChatSession session;
        if (chatSessionId == null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            session = new ChatSession(generateTitle(userMessage), user);
            chatSessionRepository.save(session);
        } else {
            session = chatSessionRepository.findById(chatSessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Chat session not found"));
        }

        ChatMessage userMsg = new ChatMessage("user", userMessage);
        session.addMessage(userMsg);

        chatSessionRepository.save(session);
        return session;
    }

    private String generateTitle(String message) {
        if (message == null || message.isBlank()) return "New Chat";
        return message.length() > 30 ? message.substring(0, 30) + "..." : message;
    }

    public List<ChatMessage> getMessages(Long sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Chat session not found"));
        return session.getMessages();
    }
}
