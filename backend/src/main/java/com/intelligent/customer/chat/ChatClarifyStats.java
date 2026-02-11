package com.intelligent.customer.chat;

import java.util.List;

/**
 * 澄清触发统计结果。
 */
public record ChatClarifyStats(
        long total,
        long today,
        List<ChatClarifyReasonCount> reasons
) {
}
