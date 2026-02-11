package com.intelligent.customer.api.dto;

import com.intelligent.customer.chat.ChatClarifyReasonCount;

import java.util.List;

/**
 * 澄清统计响应。
 */
public record ChatClarifyStatsResponse(
        long total,
        long today,
        List<ChatClarifyReasonCount> reasons
) {
}
