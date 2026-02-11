package com.intelligent.customer.chat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ChatMemoryServiceTest {

    @Autowired
    private ChatMemoryService chatMemoryService;

    @Test
    void persist_and_load_history() {
        String sessionId = chatMemoryService.ensureSession(null);
        chatMemoryService.appendMessage(sessionId, ChatRole.USER, "你好");
        chatMemoryService.appendMessage(sessionId, ChatRole.ASSISTANT, "您好，有什么可以帮您？");

        List<ChatHistoryItem> history = chatMemoryService.loadHistory(sessionId);

        assertThat(history).hasSize(2);
        assertThat(history.get(0).role()).isEqualTo(ChatRole.USER);
        assertThat(history.get(1).role()).isEqualTo(ChatRole.ASSISTANT);
    }

    @Test
    void reset_session_clears_history() {
        String sessionId = chatMemoryService.ensureSession(null);
        chatMemoryService.appendMessage(sessionId, ChatRole.USER, "测试");

        String newSessionId = chatMemoryService.resetSession(sessionId);
        List<ChatHistoryItem> history = chatMemoryService.loadHistory(sessionId);

        assertThat(newSessionId).isNotBlank();
        assertThat(history).isEmpty();
    }
}
