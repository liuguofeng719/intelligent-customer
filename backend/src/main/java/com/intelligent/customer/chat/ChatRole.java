package com.intelligent.customer.chat;

/**
 * 对话角色枚举，统一用户与客服角色标识。
 */
public enum ChatRole {
    USER("用户"),
    ASSISTANT("客服");

    private final String label;

    ChatRole(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static ChatRole fromValue(String value) {
        if (value == null) {
            return USER;
        }
        for (ChatRole role : values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        return USER;
    }
}
