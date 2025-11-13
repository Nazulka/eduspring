package com.lms.eduspring.service;

import com.lms.eduspring.model.ChatMessage;
import com.lms.eduspring.model.ChatSession;
import com.lms.eduspring.model.User;
import com.lms.eduspring.repository.ChatMessageRepository;
import com.lms.eduspring.repository.ChatSessionRepository;
import com.lms.eduspring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatService chatService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User("john", "pass", "John", "Doe", "john@example.com", "STUDENT");
        ReflectionTestUtils.setField(mockUser, "id", 1L);
    }

    @Test
    void testCreateNewChatSession_WhenNoSessionId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        ChatSession savedSession = new ChatSession("First Chat", mockUser);
        ReflectionTestUtils.setField(savedSession, "id", 100L);
        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(savedSession);

        ChatSession result = chatService.processUserMessage(1L, null, "Hello world!");

        assertThat(result.getTitle()).isEqualTo("Hello world!");
        verify(chatSessionRepository, atLeastOnce()).save(any(ChatSession.class));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testAddMessageToExistingSession() {
        ChatSession existingSession = new ChatSession("Existing Chat", mockUser);
        ReflectionTestUtils.setField(existingSession, "id", 10L);

        when(chatSessionRepository.findById(10L)).thenReturn(Optional.of(existingSession));

        ChatSession result = chatService.processUserMessage(1L, 10L, "Follow-up message");

        assertThat(result.getMessages()).hasSize(1);
        assertThat(result.getMessages().get(0).getContent()).isEqualTo("Follow-up message");
        verify(chatSessionRepository, times(1)).findById(10L);
        verify(chatSessionRepository, times(1)).save(existingSession);
    }

    @Test
    void testGetMessagesForUserSession_WhenUserOwnsSession() {
        ChatSession session = new ChatSession("My chat", mockUser);
        ReflectionTestUtils.setField(session, "id", 5L);
        session.addMessage(new ChatMessage("user", "Hi"));
        session.addMessage(new ChatMessage("ai", "Hello"));

        when(chatSessionRepository.findById(5L)).thenReturn(Optional.of(session));

        List<ChatMessage> messages = chatService.getMessagesForUserSession(1L, 5L);

        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getContent()).isEqualTo("Hi");
    }

    @Test
    void testGetMessagesForUserSession_WhenUserNotOwner_ShouldThrow() {
        User anotherUser = new User("other", "pass", "A", "B", "other@example.com", "STUDENT");
        ReflectionTestUtils.setField(anotherUser, "id", 2L);

        ChatSession otherUserSession = new ChatSession("Private chat", anotherUser);
        ReflectionTestUtils.setField(otherUserSession, "id", 9L);

        when(chatSessionRepository.findById(9L)).thenReturn(Optional.of(otherUserSession));

        assertThrows(SecurityException.class,
                () -> chatService.getMessagesForUserSession(1L, 9L));
    }

    @Test
    void testGetMessagesForUserSession_WhenSessionNotFound_ShouldThrow() {
        when(chatSessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> chatService.getMessagesForUserSession(1L, 99L));
    }
}
