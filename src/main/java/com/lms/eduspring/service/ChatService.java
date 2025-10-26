package com.lms.eduspring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final WebClient webClient;

    public ChatService(@Value("${openai.api.key}") String apiKey) {
        System.out.println("🔑 OpenAI Key (first 10 chars): " +
                (apiKey != null ? apiKey.substring(0, Math.min(apiKey.length(), 10)) : "NULL"));

        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public String chat(String message) {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(Map.of("role", "user", "content", message))
        );

        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()

                // Handle specific HTTP statuses
                .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals,
                        response -> Mono.error(new RuntimeException("Rate limit reached. Please wait a moment and try again.")))
                .onStatus(HttpStatus.UNAUTHORIZED::equals,
                        response -> Mono.error(new RuntimeException("Invalid API key or unauthorized request.")))
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                        response -> Mono.error(new RuntimeException("Bad request. Please check your input.")))

                .bodyToMono(Map.class)
                .map(res -> {
                    var choices = (List<Map<String, Object>>) res.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        var msg = (Map<String, Object>) choices.get(0).get("message");
                        return (String) msg.get("content");
                    }
                    return "No response from AI.";
                })

                // Catch-all for unexpected errors
                .onErrorResume(WebClientResponseException.class,
                        e -> Mono.just("Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString()))
                .onErrorResume(e -> Mono.just("Error: " + e.getMessage()))

                .block();
    }
}
