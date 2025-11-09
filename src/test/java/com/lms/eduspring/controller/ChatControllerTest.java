package com.lms.eduspring.controller;

import com.lms.eduspring.model.ChatMessage;
import com.lms.eduspring.model.ChatSession;
import com.lms.eduspring.service.ChatService;
import com.lms.eduspring.service.JwtService;
import com.lms.eduspring.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private ChatService chatService;
    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getMessagesBySessionShouldReturnOk() throws Exception {
        ChatMessage msg = new ChatMessage("user", "Hello world");
        when(chatService.getMessages(1L)).thenReturn(List.of(msg));

        mockMvc.perform(get("/api/chat/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0].content").value("Hello world"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void postMessageShouldReturnSessionAndMessages() throws Exception {
        ChatMessage msg = new ChatMessage("user", "Hi there");
        ChatSession session = new ChatSession("Hi there", null);
        session.addMessage(msg);
        when(chatService.processUserMessage(1L, null, "Hi there")).thenReturn(session);

        mockMvc.perform(post("/api/chat/message")
                        .with(csrf())
                        .param("userId", "1")
                        .param("content", "Hi there"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0].content").value("Hi there"));
    }

    @Test
    void unauthorizedUserShouldGet401() throws Exception {
        mockMvc.perform(get("/api/chat/1"))
                .andExpect(status().isUnauthorized());
    }
}
