package com.lms.eduspring.controller;

import com.lms.eduspring.config.TestSecurityConfig;
import com.lms.eduspring.model.ChatMessage;
import com.lms.eduspring.model.ChatSession;
import com.lms.eduspring.service.ChatService;
import com.lms.eduspring.service.UserService;
import com.lms.eduspring.security.JwtAuthFilter;
import com.lms.eduspring.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ChatControllerTest.TestSecurityExceptionAdvice.class, TestSecurityConfig.class})
//@ImportAutoConfiguration(exclude = {
//        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
//        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class,
//        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
//        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
//        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
//        org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class
//})
class ChatControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean private ChatService chatService;
    @MockBean private UserService userService;
    @MockBean private JwtAuthFilter jwtAuthFilter;
    @MockBean private JwtService jwtService;

    @RestControllerAdvice
    static class TestSecurityExceptionAdvice {
        @ExceptionHandler(SecurityException.class)
        public ResponseEntity<Map<String, String>> handle(SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @Test
    void unauthorizedUser_ShouldGet403() throws Exception {
        when(chatService.getSessionsForUser(0L))
                .thenThrow(new SecurityException("Unauthorized"));

        mockMvc.perform(get("/api/chat/conversations")
                        .param("userId", "0"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void forbiddenAccess_ShouldReturn403() throws Exception {
        when(chatService.getMessagesForUserSession(1L, 5L))
                .thenThrow(new SecurityException("Access denied"));

        mockMvc.perform(get("/api/chat/conversations/5")
                        .param("userId", "1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access denied"));
    }

    @Test
    void getUserSessions_ShouldReturnSimplifiedSessions() throws Exception {
        ChatSession s1 = new ChatSession("Chat about Java", null);
        ChatSession s2 = new ChatSession("AI discussion", null);

        when(chatService.getSessionsForUser(1L)).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/chat/conversations")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Chat about Java"))
                .andExpect(jsonPath("$[1].title").value("AI discussion"));
    }

    @Test
    void getMessagesForSession_ShouldReturnMessages() throws Exception {
        ChatMessage m1 = new ChatMessage("user", "Hi!");
        ChatMessage m2 = new ChatMessage("ai", "Hello!");

        when(chatService.getMessagesForUserSession(1L, 10L))
                .thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/api/chat/conversations/10")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0].content").value("Hi!"))
                .andExpect(jsonPath("$.messages[1].content").value("Hello!"));
    }
}
