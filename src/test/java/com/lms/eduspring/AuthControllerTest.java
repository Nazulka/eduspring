package com.lms.eduspring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.eduspring.controller.AuthController;
import com.lms.eduspring.dto.UserRegistrationDto;
import com.lms.eduspring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

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

        when(userService.verifyLogin(username, password)).thenReturn(true);

        String requestBody = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"));

        verify(userService, times(1)).verifyLogin(username, password);
    }

    @Test
    void testLogin_Failure() throws Exception {
        String username = "student1";
        String password = "wrongPassword";

        when(userService.verifyLogin(username, password)).thenReturn(false);

        String requestBody = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));

        verify(userService, times(1)).verifyLogin(username, password);
    }
}
