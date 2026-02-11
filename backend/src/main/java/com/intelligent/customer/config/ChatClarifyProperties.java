package com.intelligent.customer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 澄清追问配置，支持模板与阈值控制。
 */
@ConfigurationProperties(prefix = "chat.clarify")
public record ChatClarifyProperties(
        int shortQuestionLength,
        int minQuestionLength,
        boolean enablePronounCheck,
        String genericTemplate,
        String pronounTemplate,
        String orderTemplate,
        String productTemplate,
        String customerTemplate
) {
    public ChatClarifyProperties {
        if (shortQuestionLength <= 0) {
            shortQuestionLength = 3;
        }
        if (minQuestionLength <= 0) {
            minQuestionLength = 6;
        }
        if (genericTemplate == null || genericTemplate.isBlank()) {
            genericTemplate = "请补充您要咨询的具体问题。";
        }
        if (pronounTemplate == null || pronounTemplate.isBlank()) {
            pronounTemplate = "请说明具体指的是哪一单或哪件商品？";
        }
        if (orderTemplate == null || orderTemplate.isBlank()) {
            orderTemplate = "请提供订单号或下单手机号。";
        }
        if (productTemplate == null || productTemplate.isBlank()) {
            productTemplate = "请提供商品名称或编号。";
        }
        if (customerTemplate == null || customerTemplate.isBlank()) {
            customerTemplate = "请提供客户编号或手机号。";
        }
    }
}
