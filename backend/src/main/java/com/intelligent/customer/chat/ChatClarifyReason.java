package com.intelligent.customer.chat;

/**
 * 澄清触发原因枚举。
 */
public enum ChatClarifyReason {
    EMPTY("空问题"),
    SHORT("过短"),
    PRONOUN("代词不明确"),
    ORDER("订单缺少关键信息"),
    PRODUCT("商品缺少关键信息"),
    CUSTOMER("客户缺少关键信息"),
    GENERAL("通用问题不完整");

    private final String label;

    ChatClarifyReason(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
