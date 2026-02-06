package com.intelligent.customer.config;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 相关配置，集中声明 Ollama 模型 Bean。
 * 约束：依赖配置文件中的 base-url 与模型名称。
 */
@Configuration
@EnableConfigurationProperties(OllamaProperties.class)
public class AiConfig {

    /**
     * 构建对话模型，供问答链路调用。
     */
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

    /**
     * 构建向量模型，供 FAQ 嵌入与检索使用。
     */
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
