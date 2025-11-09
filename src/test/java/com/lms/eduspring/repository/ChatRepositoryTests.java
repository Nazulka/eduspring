package com.lms.eduspring.repository;



import com.lms.eduspring.model.ChatMessage;
import com.lms.eduspring.model.ChatSession;
import com.lms.eduspring.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ChatRepositoryTests {

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveSessionAndMessages() {
        User user = new User("tester", "password", "Test", "User", "test@example.com", "STUDENT");
        userRepository.save(user);

        ChatSession session = new ChatSession("First Chat", user);
        session.addMessage(new ChatMessage("user", "Hello there!"));
        session.addMessage(new ChatMessage("ai", "Hi! How can I help?"));

        chatSessionRepository.save(session);

        List<ChatSession> sessions = chatSessionRepository.findAll();
        assertThat(sessions).hasSize(1);
        assertThat(sessions.get(0).getMessages()).hasSize(2);
    }

    @Test
    void testCascadeDeleteSessionRemovesMessages() {
        User user = new User("tester2", "password", "Test", "User", "test2@example.com", "STUDENT");
        userRepository.save(user);

        ChatSession session = new ChatSession("Cascade Test", user);
        session.addMessage(new ChatMessage("user", "Hi"));
        session.addMessage(new ChatMessage("ai", "Hello"));
        chatSessionRepository.save(session);

        assertThat(chatMessageRepository.count()).isEqualTo(2);

        chatSessionRepository.delete(session);
        assertThat(chatMessageRepository.count()).isZero();
    }
}
