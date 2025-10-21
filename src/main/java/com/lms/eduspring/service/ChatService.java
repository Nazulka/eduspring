package com.lms.eduspring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;

@Service
public class ChatService {

    private final WebClient webClient;

    public ChatService(@Value("${openai.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public String chat(String message) {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", new Object[]{
                        Map.of("role", "user", "content", message)
                }
        );

        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(res -> {
                    var choices = (java.util.List<Map<String, Object>>) res.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        var msg = (Map<String, Object>) choices.get(0).get("message");
                        return (String) msg.get("content");
                    }
                    return "No response from AI.";
                })
                .onErrorResume(e -> Mono.just("Error: " + e.getMessage()))
                .block();
    }
}
