package com.intelligent.customer.chat;

/**
 * 澄清触发原因统计。
 */
public record ChatClarifyReasonCount(
        String reason,
        String label,
        long count
) {
}
