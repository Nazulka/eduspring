package com.lms.eduspring.controller;

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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ✅ Mock all dependencies from ChatController constructor
    @MockBean private ChatService chatService;
    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void authorizedUserShouldGetResponse() throws Exception {
        when(chatService.getMessages()).thenReturn(List.of("Hello world"));

        mockMvc.perform(get("/api/chat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0]").value("Hello world"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void invalidInputShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .with(csrf())  // ✅ add this line
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Message cannot be empty"));
    }

    @Test
    void unauthorizedUserShouldGet401() throws Exception {
        mockMvc.perform(get("/api/chat"))
                .andExpect(status().isUnauthorized());
    }
}
