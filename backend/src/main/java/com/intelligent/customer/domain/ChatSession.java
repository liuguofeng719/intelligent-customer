package com.intelligent.customer.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 会话实体，持久化记录对话上下文。
 */
@TableName("chat_session")
public class ChatSession {

    @TableId(type = IdType.INPUT)
    private String sessionId;
    private LocalDateTime createdAt;
    private LocalDateTime lastActiveAt;
    private String intent;
    private LocalDateTime intentUpdatedAt;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(LocalDateTime lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public LocalDateTime getIntentUpdatedAt() {
        return intentUpdatedAt;
    }

    public void setIntentUpdatedAt(LocalDateTime intentUpdatedAt) {
        this.intentUpdatedAt = intentUpdatedAt;
    }
}
