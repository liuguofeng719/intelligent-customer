package com.intelligent.customer.api.dto;

import java.time.LocalDateTime;

/**
 * 澄清配置响应。
 */
public record ChatClarifyConfigResponse(
        int shortQuestionLength,
        int minQuestionLength,
        boolean enablePronounCheck,
        String genericTemplate,
        String pronounTemplate,
        String orderTemplate,
        String productTemplate,
        String customerTemplate,
        LocalDateTime updatedAt
) {
}
