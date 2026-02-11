package com.intelligent.customer.chat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.intelligent.customer.config.ChatMemoryProperties;
import com.intelligent.customer.domain.ChatMessage;
import com.intelligent.customer.domain.ChatSession;
import com.intelligent.customer.repo.ChatMessageMapper;
import com.intelligent.customer.repo.ChatSessionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 对话记忆服务，持久化会话与多轮消息。
 */
@Service
public class ChatMemoryService {

    private final ChatSessionMapper sessionMapper;
    private final ChatMessageMapper messageMapper;
    private final ChatMemoryProperties properties;

    public ChatMemoryService(ChatSessionMapper sessionMapper,
                             ChatMessageMapper messageMapper,
                             ChatMemoryProperties properties) {
        this.sessionMapper = sessionMapper;
        this.messageMapper = messageMapper;
        this.properties = properties;
    }

    /**
     * 创建或确认会话，并返回会话标识。
     */
    public String ensureSession(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return createSession();
        }
        ChatSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            createSessionWithId(sessionId);
            return sessionId;
        }
        session.setLastActiveAt(LocalDateTime.now());
        sessionMapper.updateById(session);
        return sessionId;
    }

    /**
     * 获取当前会话意图。
     */
    public ChatIntent loadIntent(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return ChatIntent.GENERAL;
        }
        ChatSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            return ChatIntent.GENERAL;
        }
        return ChatIntent.fromValue(session.getIntent());
    }

    /**
     * 更新会话意图。
     */
    public void updateIntent(String sessionId, ChatIntent intent) {
        if (sessionId == null || sessionId.isBlank() || intent == null) {
            return;
        }
        ChatSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            return;
        }
        session.setIntent(intent.name());
        session.setIntentUpdatedAt(LocalDateTime.now());
        sessionMapper.updateById(session);
    }

    /**
     * 读取最近的对话历史，用于提示词上下文。
     */
    public List<ChatHistoryItem> loadHistory(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return List.of();
        }
        QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("session_id", sessionId)
                .orderByDesc("created_at")
                .last("limit " + properties.maxMessages());
        List<ChatMessage> messages = messageMapper.selectList(wrapper);
        if (messages == null || messages.isEmpty()) {
            return List.of();
        }
        Collections.reverse(messages);
        List<ChatHistoryItem> history = new ArrayList<>();
        for (ChatMessage message : messages) {
            history.add(new ChatHistoryItem(ChatRole.fromValue(message.getRole()), message.getContent()));
        }
        return history;
    }

    /**
     * 追加一条会话消息。
     */
    public void appendMessage(String sessionId, ChatRole role, String content) {
        if (sessionId == null || sessionId.isBlank() || content == null || content.isBlank()) {
            return;
        }
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setRole(role.name());
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);

        ChatSession session = sessionMapper.selectById(sessionId);
        if (session != null) {
            session.setLastActiveAt(LocalDateTime.now());
            sessionMapper.updateById(session);
        }
    }

    /**
     * 重置会话并返回新的会话标识。
     */
    public String resetSession(String sessionId) {
        if (sessionId != null && !sessionId.isBlank()) {
            QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
            wrapper.eq("session_id", sessionId);
            messageMapper.delete(wrapper);
            sessionMapper.deleteById(sessionId);
        }
        return createSession();
    }

    private String createSession() {
        String sessionId = UUID.randomUUID().toString();
        createSessionWithId(sessionId);
        return sessionId;
    }

    private void createSessionWithId(String sessionId) {
        ChatSession session = new ChatSession();
        session.setSessionId(sessionId);
        LocalDateTime now = LocalDateTime.now();
        session.setCreatedAt(now);
        session.setLastActiveAt(now);
        session.setIntent(ChatIntent.GENERAL.name());
        session.setIntentUpdatedAt(now);
        sessionMapper.insert(session);
    }
}
