package com.lms.eduspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.eduspring.dto.UserRegistrationDto;
import com.lms.eduspring.service.UserService;
import com.lms.eduspring.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // disable Spring Security filters for simplicity
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ Mock dependencies injected into AuthController
    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @Test
    void testRegisterUser_Success() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("student1");
        dto.setPassword("password123");
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setEmail("alice@example.com");

        doNothing().when(userService).registerUser(any());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registration successful!"));

        verify(userService, times(1)).registerUser(any());
    }

    @Test
    void testRegisterUser_UsernameTaken() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("existingUser");
        dto.setPassword("password123");
        dto.setFirstName("Bob");
        dto.setLastName("Jones");
        dto.setEmail("bob@example.com");

        doThrow(new IllegalArgumentException("Username already taken"))
                .when(userService).registerUser(any());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username already taken"));

        verify(userService, times(1)).registerUser(any());
    }

    @Test
    void testLogin_Success() throws Exception {
        String username = "student1";
        String password = "password123";

        when(userService.verifyLogin(eq(username), eq(password))).thenReturn(true);
        // ✅ Updated mock for JwtService (2 parameters)
        when(jwtService.generateToken(eq(username), anyMap())).thenReturn("mocked-jwt-token");

        String requestBody = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));

        verify(userService, times(1)).verifyLogin(username, password);
        // ✅ Updated verification for JwtService (2 parameters)
        verify(jwtService, times(1)).generateToken(eq(username), anyMap());
    }


    @Test
    void testLogin_Failure() throws Exception {
        String username = "student1";
        String password = "wrongPassword";

        when(userService.verifyLogin(eq(username), eq(password))).thenReturn(false);

        String requestBody = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));

        verify(userService, times(1)).verifyLogin(username, password);
        // ✅ Updated to match new JwtService signature
        verify(jwtService, never()).generateToken(anyString(), anyMap());
    }

}
