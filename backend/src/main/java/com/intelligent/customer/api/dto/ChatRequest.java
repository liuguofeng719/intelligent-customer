package com.intelligent.customer.api.dto;

import java.util.List;

/**
 * 对话请求数据。
 *
 * @param question  用户问题
 * @param sessionId 会话标识（预留）
 * @param history   历史对话（预留）
 */
public record ChatRequest(
        String question,
        String sessionId,
        List<String> history
) {
}
