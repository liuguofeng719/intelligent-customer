package com.intelligent.customer.chat;

/**
 * 澄清追问配置快照。
 */
public record ChatClarifySettings(
        int shortQuestionLength,
        int minQuestionLength,
        boolean enablePronounCheck,
        String genericTemplate,
        String pronounTemplate,
        String orderTemplate,
        String productTemplate,
        String customerTemplate
) {
}
