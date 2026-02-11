package com.intelligent.customer.chat;

/**
 * 对话意图枚举，用于跨轮对话保持业务目标。
 */
public enum ChatIntent {
    ORDER("订单"),
    PRODUCT("产品"),
    CUSTOMER("客户"),
    GENERAL("通用");

    private final String label;

    ChatIntent(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static ChatIntent fromValue(String value) {
        if (value == null || value.isBlank()) {
            return GENERAL;
        }
        for (ChatIntent intent : values()) {
            if (intent.name().equalsIgnoreCase(value)) {
                return intent;
            }
        }
        return GENERAL;
    }
}
