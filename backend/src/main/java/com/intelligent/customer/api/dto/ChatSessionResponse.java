package com.intelligent.customer.api.dto;

/**
 * 会话响应数据。
 *
 * @param sessionId 会话标识
 * @param intent    当前意图
 */
public record ChatSessionResponse(
        String sessionId,
        String intent
) {
}
