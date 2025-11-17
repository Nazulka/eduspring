package com.lms.eduspring.service;

import com.lms.eduspring.model.ChatMessage;
import com.lms.eduspring.model.ChatSession;
import com.lms.eduspring.model.User;
import com.lms.eduspring.repository.ChatMessageRepository;
import com.lms.eduspring.repository.ChatSessionRepository;
import com.lms.eduspring.repository.UserRepository;
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

    // ----------------------------
    // Save USER messages
    // ----------------------------
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

    // ----------------------------
    // Save AI messages
    // ----------------------------
    public ChatSession processAiMessage(Long sessionId, String aiReply) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Chat session not found"));

        ChatMessage aiMsg = new ChatMessage("ai", aiReply);
        session.addMessage(aiMsg);

        chatSessionRepository.save(session);

        return session;
    }

    // ----------------------------
    // FIXED TITLE GENERATOR
    // ----------------------------
    private String generateTitle(String message) {
        if (message == null || message.isBlank()) {
            return "New Chat";
        }

        // Trim and clean whitespace
        String cleaned = message.trim().replaceAll("\\s+", " ");

        // Take first 40 chars for nicer preview titles
        return cleaned.length() > 40
                ? cleaned.substring(0, 40) + "..."
                : cleaned;
    }

    public List<ChatMessage> getMessages(Long sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Chat session not found"));
        return session.getMessages();
    }

    public List<ChatSession> getSessionsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return chatSessionRepository.findByUser(user);
    }

    public List<ChatMessage> getMessagesForUserSession(Long userId, Long sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Chat session not found"));

        if (!session.getUser().getId().equals(userId)) {
            throw new SecurityException("Access denied: session does not belong to this user");
        }

        return session.getMessages();
    }
}
