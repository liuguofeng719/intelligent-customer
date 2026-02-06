package com.intelligent.customer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Ollama 接入配置。
 *
 * @param baseUrl        Ollama 服务地址
 * @param chatModel      对话模型名称
 * @param embeddingModel 向量模型名称
 * @param temperature    采样温度
 * @param timeout        请求超时时间
 */
@ConfigurationProperties(prefix = "ollama")
public record OllamaProperties(
        String baseUrl,
        String chatModel,
        String embeddingModel,
        Double temperature,
        Duration timeout
) {
}
