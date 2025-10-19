package com.lms.eduspring;

import com.lms.eduspring.model.User;
import com.lms.eduspring.repository.UserRepository;
import com.lms.eduspring.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService; // the class we are testing

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_ShouldSaveUser_WhenUsernameIsUnique() {

        User user = new User("newUser", "plainPass", "John", "Doe", "john@example.com", "STUDENT");
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPass")).thenReturn("hashedPass");

        userService.registerUser(user);

        verify(passwordEncoder).encode("plainPass"); // password was hashed
        verify(userRepository).save(user);// user was saved
//        System.out.println("Test is running!");
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameExists() {
        User user = new User(
                "existingUser",
                "pass",
                "Jane",
                "Smith",
                "jane@example.com",
                "STUDENT");

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any(User.class));
        // should not save
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        User user = new User("alice", "hash", "Alice", "Brown", "alice@example.com", "STUDENT");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        User found = userService.findByUsername("alice");

        assertNotNull(found);
        assertEquals("alice", found.getUsername());
    }

    @Test
    void findByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            userService.findByUsername("unknown");
        });

        verify(userRepository, times(1)).findByUsername("unknown");
    }


    @Test
    void verifyLogin_ShouldReturnTrue_WhenPasswordMatches() {
        User user = new User("bob", "hashed", "Bob", "Lee", "bob@example.com", "STUDENT");
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPass", "hashed")).thenReturn(true);

        boolean result = userService.verifyLogin("bob", "rawPass");

        assertTrue(result);
    }

    @Test
    void verifyLogin_ShouldReturnFalse_WhenPasswordDoesNotMatch() {
        User user = new User("bob", "hashed", "Bob", "Lee", "bob@example.com", "STUDENT");
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "hashed")).thenReturn(false);

        boolean result = userService.verifyLogin("bob", "wrongPass");

        assertFalse(result);
    }

    @Test
    void verifyLogin_ShouldReturnFalse_WhenUserNotFound() {
        when(userRepository.findByUsername("missingUser")).thenReturn(Optional.empty());

        boolean result = userService.verifyLogin("missingUser", "anyPass");

        assertFalse(result);
    }
}
