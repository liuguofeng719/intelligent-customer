package com.intelligent.customer.api.dto;

/**
 * 澄清配置更新请求。
 */
public record ChatClarifyConfigRequest(
        Integer shortQuestionLength,
        Integer minQuestionLength,
        Boolean enablePronounCheck,
        String genericTemplate,
        String pronounTemplate,
        String orderTemplate,
        String productTemplate,
        String customerTemplate
) {
}
