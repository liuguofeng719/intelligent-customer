package com.intelligent.customer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "ollama")
public record OllamaProperties(
        String baseUrl,
        String chatModel,
        String embeddingModel,
        Double temperature,
        Duration timeout
) {
}
