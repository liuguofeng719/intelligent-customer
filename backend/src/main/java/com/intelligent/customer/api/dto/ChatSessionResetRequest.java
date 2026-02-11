package com.intelligent.customer.api.dto;

/**
 * 会话重置请求。
 *
 * @param sessionId 会话标识
 */
public record ChatSessionResetRequest(
        String sessionId
) {
}
