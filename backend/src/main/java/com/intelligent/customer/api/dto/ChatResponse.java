package com.intelligent.customer.api.dto;

/**
 * 对话响应数据。
 *
 * @param sessionId 会话标识
 * @param answer    客服回答
 * @param intent    当前意图
 */
public record ChatResponse(
        String sessionId,
        String answer,
        String intent
) {
}
