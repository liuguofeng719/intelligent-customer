package com.intelligent.customer.chat;

/**
 * 澄清结果，包含追问内容与触发原因。
 */
public record ChatClarifyResult(
        String message,
        ChatClarifyReason reason
) {
}
