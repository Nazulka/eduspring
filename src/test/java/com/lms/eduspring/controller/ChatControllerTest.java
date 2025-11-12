package com.lms.eduspring.controller;

import com.lms.eduspring.config.TestConfig;
import com.lms.eduspring.exception.GlobalExceptionHandler;
import com.lms.eduspring.model.ChatMessage;
import com.lms.eduspring.model.ChatSession;
import com.lms.eduspring.model.User;
import com.lms.eduspring.service.ChatService;
import com.lms.eduspring.service.JwtService;
import com.lms.eduspring.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ✅ FIX SUMMARY:
 * - Uses @WebMvcTest to load only the ChatController (not the full Spring context)
 * - @MockBean mocks all dependencies (ChatService, JwtService, UserService)
 * - @AutoConfigureMockMvc(addFilters = false) disables security filters
 * - @Import(TestConfig, GlobalExceptionHandler) loads minimal beans needed for the test
 * This prevents Spring from trying to start PostgreSQL, SecurityConfig, or other heavy components.
 */
@WebMvcTest(controllers = ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({TestConfig.class, GlobalExceptionHandler.class})
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private ChatService chatService;
    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;

    // 🔹 If your app has a JwtAuthFilter or similar security filter and still fails to start,
    // just uncomment this line:
    // @MockBean private JwtAuthFilter jwtAuthFilter;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getUserSessions_ShouldReturnSessionsForAuthenticatedUser() throws Exception {
        User user = new User("john", "pass", "John", "Doe", "john@example.com", "STUDENT");
        ChatSession session1 = new ChatSession("Chat about Java", user);
        ChatSession session2 = new ChatSession("AI discussion", user);

        when(chatService.getSessionsForUser(1L)).thenReturn(List.of(session1, session2));

        mockMvc.perform(get("/api/chat/sessions")
                        .param("userId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessions.length()").value(2))
                .andExpect(jsonPath("$.sessions[0].title").value("Chat about Java"))
                .andExpect(jsonPath("$.sessions[1].title").value("AI discussion"));

        verify(chatService, times(1)).getSessionsForUser(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getMessagesForSession_ShouldReturnMessagesForUser() throws Exception {
        ChatMessage msg1 = new ChatMessage("user", "Hi!");
        ChatMessage msg2 = new ChatMessage("ai", "Hello, how can I help?");
        when(chatService.getMessagesForUserSession(1L, 10L)).thenReturn(List.of(msg1, msg2));

        mockMvc.perform(get("/api/chat/sessions/10")
                        .param("userId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0].content").value("Hi!"))
                .andExpect(jsonPath("$.messages[1].content").value("Hello, how can I help?"));

        verify(chatService, times(1)).getMessagesForUserSession(1L, 10L);
    }

    @Test
    void unauthorizedUser_ShouldGet403() throws Exception {
        when(chatService.getSessionsForUser(anyLong()))
                .thenThrow(new SecurityException("Unauthorized"));

        mockMvc.perform(get("/api/chat/sessions")
                        .param("userId", "0"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void forbiddenAccess_ShouldReturn403() throws Exception {
        when(chatService.getMessagesForUserSession(1L, 5L))
                .thenThrow(new SecurityException("Access denied: session does not belong to this user"));

        mockMvc.perform(get("/api/chat/sessions/5")
                        .param("userId", "1")
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access denied: session does not belong to this user"));
    }
}
