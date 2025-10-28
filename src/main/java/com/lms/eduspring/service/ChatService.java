package com.lms.eduspring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final List<String> messages = new ArrayList<>();

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public void saveMessage(String message) {
        messages.add(message);
    }

    private final WebClient webClient;

    public ChatService(@Value("${OPENAI_API_KEY}") String apiKey) {
        System.out.println("OpenAI Key (first 10 chars): " +
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

                .onStatus(status -> status.equals(HttpStatus.TOO_MANY_REQUESTS),
                        response -> Mono.error(new RuntimeException("Rate limit reached. Please wait a moment and try again.")))
                .onStatus(status -> status.equals(HttpStatus.UNAUTHORIZED),
                        response -> Mono.error(new RuntimeException("Invalid API key or unauthorized request.")))
                .onStatus(status -> status.equals(HttpStatus.BAD_REQUEST),
                        response -> Mono.error(new RuntimeException("Bad request. Please check your input.")))

                // ✅ Convert to Map once
                .bodyToMono(Map.class)
                .map(res -> {
                    var choices = (List<Map<String, Object>>) res.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        var msg = (Map<String, Object>) choices.get(0).get("message");
                        return (String) msg.get("content");
                    }
                    return "No response from AI.";
                })

                // ✅ Handle general errors
                .onErrorResume(WebClientResponseException.class,
                        e -> Mono.just("Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString()))
                .onErrorResume(e -> Mono.just("Error: " + e.getMessage()))
                .block();
    }
}
