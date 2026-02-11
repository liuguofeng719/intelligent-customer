package com.intelligent.customer.chat;

/**
 * 对话历史条目，用于提示词构建。
 */
public record ChatHistoryItem(
        ChatRole role,
        String content
) {
}
