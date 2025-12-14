package com.lms.eduspring.service;

import com.lms.eduspring.dto.ConversationDetailDto;
import com.lms.eduspring.dto.ConversationDto;
import com.lms.eduspring.dto.MessageDto;
import com.lms.eduspring.model.ChatMessage;
import com.lms.eduspring.model.ChatSession;
import com.lms.eduspring.model.User;
import com.lms.eduspring.repository.ChatMessageRepository;
import com.lms.eduspring.repository.ChatSessionRepository;
import com.lms.eduspring.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatSessionRepository sessionRepo;
    private final ChatMessageRepository messageRepo;
    private final UserRepository userRepo;

    public ChatServiceImpl(ChatSessionRepository sessionRepo,
                           ChatMessageRepository messageRepo,
                           UserRepository userRepo) {
        this.sessionRepo = sessionRepo;
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
    }

    @Override
    public ChatSession processUserMessage(Long userId, Long conversationId, String content) {

        ChatSession session;

        if (conversationId == null) {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            String title = content.length() > 30 ? content.substring(0, 30) : content;
            session = new ChatSession(title, user);
            sessionRepo.save(session);
        } else {
            session = sessionRepo.findById(conversationId)
                    .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
        }

        ChatMessage msg = new ChatMessage("user", content);
        session.addMessage(msg);
        sessionRepo.save(session);

        return session;
    }

    @Override
    public void processAiMessage(Long conversationId, String content) {

        ChatSession session = sessionRepo.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        ChatMessage msg = new ChatMessage("ai", content);
        session.addMessage(msg);
        sessionRepo.save(session);
    }

    @Override
    public List<ChatSession> getSessionsForUser(Long userId) {
        return sessionRepo.findByUser_Id(userId);
    }

    @Override
    public List<ChatMessage> getMessagesForUserSession(Long userId, Long conversationId) {

        ChatSession session = sessionRepo.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!session.getUser().getId().equals(userId)) {
            throw new SecurityException("Access denied: session does not belong to this user");
        }

        return session.getMessages();
    }

    @Override
    public List<ConversationDto> getConversationDtosForUser(Long userId) {
        return getSessionsForUser(userId).stream()
                .map(session -> new ConversationDto(
                        session.getId(),
                        session.getTitle(),
                        session.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public ConversationDetailDto getConversationDetail(Long userId, Long conversationId) {

        ChatSession session = sessionRepo
                .findByIdAndUser_Id(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        return new ConversationDetailDto(
                session.getId(),
                session.getTitle(),
                session.getCreatedAt(),
                session.getMessages().stream()
                        .map(m -> new MessageDto(
                                m.getRole(),
                                m.getContent(),
                                m.getCreatedAt()
                        ))
                        .toList()
        );
    }
}
