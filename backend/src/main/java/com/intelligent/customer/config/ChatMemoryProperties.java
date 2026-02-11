package com.intelligent.customer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 对话记忆配置，控制历史消息窗口大小。
 */
@ConfigurationProperties(prefix = "chat.memory")
public record ChatMemoryProperties(
        int maxMessages
) {
    public ChatMemoryProperties {
        if (maxMessages <= 0) {
            maxMessages = 6;
        }
    }
}
