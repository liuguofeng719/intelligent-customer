package com.intelligent.customer.config;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OllamaProperties.class)
public class AiConfig {

    @Bean
    public OllamaChatModel ollamaChatModel(OllamaProperties properties) {
        // 基于配置初始化 Ollama 对话模型
        return OllamaChatModel.builder()
                .baseUrl(properties.baseUrl())
                .modelName(properties.chatModel())
                .temperature(properties.temperature())
                .timeout(properties.timeout())
                .build();
    }

    @Bean
    public OllamaEmbeddingModel ollamaEmbeddingModel(OllamaProperties properties) {
        // 基于配置初始化 Ollama 向量模型
        return OllamaEmbeddingModel.builder()
                .baseUrl(properties.baseUrl())
                .modelName(properties.embeddingModel())
                .timeout(properties.timeout())
                .build();
    }
}
