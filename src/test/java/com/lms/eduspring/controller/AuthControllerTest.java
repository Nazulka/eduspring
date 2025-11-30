package com.lms.eduspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.eduspring.config.TestSecurityConfig;
import com.lms.eduspring.dto.LoginRequestDto;
import com.lms.eduspring.dto.UserRegistrationDto;
import com.lms.eduspring.model.User;
import com.lms.eduspring.service.JwtService;
import com.lms.eduspring.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({TestSecurityConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    // ===============================================
    // REGISTER SUCCESS
    // ===============================================
    @Test
    void testRegisterUser_Success() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("student1");
        dto.setPassword("password123");
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setEmail("alice@example.com");

        doNothing().when(userService).registerUser(any(User.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registration successful!"));

        verify(userService, times(1)).registerUser(any(User.class));
    }

    // ===============================================
    // REGISTER FAILURE
    // ===============================================
    @Test
    void testRegisterUser_UsernameTaken() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("existingUser");
        dto.setPassword("password123");
        dto.setFirstName("Bob");
        dto.setLastName("Jones");
        dto.setEmail("bob@example.com");

        doThrow(new IllegalArgumentException("Username already taken"))
                .when(userService).registerUser(any(User.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username already taken"));
    }

    // ===============================================
    // LOGIN SUCCESS
    // ===============================================
    @Test
    void testLogin_Success() throws Exception {
        String username = "student1";
        String password = "password123";

        when(userService.verifyLogin(username, password)).thenReturn(true);

        User user = new User(username, password, "Alice", "Smith",
                "alice@example.com", "STUDENT", Set.of());
        user.setId(1L);

        when(userService.findByUsername(username)).thenReturn(user);
        when(jwtService.generateToken(eq(username), any(Map.class)))
                .thenReturn("mocked-token");

        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.token").value("mocked-token"))
                .andExpect(jsonPath("$.user.username").value("student1"))
                .andExpect(jsonPath("$.user.email").value("alice@example.com"));
    }

    // ===============================================
    // LOGIN FAILURE
    // ===============================================
    @Test
    void testLogin_Failure() throws Exception {
        when(userService.verifyLogin("student1", "wrongPassword"))
                .thenReturn(false);

        LoginRequestDto dto = new LoginRequestDto();
        dto.setUsername("student1");
        dto.setPassword("wrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }
}
