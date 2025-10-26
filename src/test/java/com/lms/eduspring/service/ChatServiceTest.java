package com.lms.eduspring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    @Mock
    private WebClient mockWebClient;
    @Mock
    private WebClient.RequestBodyUriSpec mockRequestBodyUriSpec;
    @Mock
    private WebClient.RequestBodySpec mockRequestBodySpec;
    @Mock
    private WebClient.RequestHeadersSpec mockRequestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec mockResponseSpec;

    private ChatService chatService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        chatService = new ChatService("dummy-key");

        // Replace ChatService.webClient with our mock
        Field webClientField = ChatService.class.getDeclaredField("webClient");
        webClientField.setAccessible(true);
        webClientField.set(chatService, mockWebClient);

        // ✅ Ensure the whole WebClient chain is connected
        when(mockWebClient.post()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.contentType(any())).thenReturn(mockRequestBodySpec);
        when(mockRequestBodySpec.bodyValue(any())).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        // ✅ Make onStatus() calls return the same mockResponseSpec (so chain continues)
        when(mockResponseSpec.onStatus(any(), any())).thenReturn(mockResponseSpec);

    }

    @Test
    void testChat_SuccessResponse() {
        Map<String, Object> responseBody = Map.of(
                "choices", List.of(
                        Map.of("message", Map.of("content", "Hello user!"))
                )
        );

        when(mockResponseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(responseBody));

        String result = chatService.chat("Hi AI!");
        assertEquals("Hello user!", result);
    }

    @Test
    void testChat_NoChoicesResponse() {
        when(mockResponseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(Map.of()));

        String result = chatService.chat("No response?");
        assertEquals("No response from AI.", result);
    }

    @Test
    void testChat_ErrorResponse() {
        when(mockResponseSpec.bodyToMono(Map.class))
                .thenReturn(Mono.error(new WebClientResponseException(
                        "Unauthorized", HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized", null, null, null)));

        String result = chatService.chat("Bad key?");
        assertTrue(result.contains("Error"));
    }
}
