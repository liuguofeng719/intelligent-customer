package com.intelligent.customer.chat;

import com.intelligent.customer.domain.ChatClarifyEvent;
import com.intelligent.customer.repo.ChatClarifyEventMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ChatClarifyStatsServiceTest {

    @Autowired
    private ChatClarifyStatsService statsService;

    @Autowired
    private ChatClarifyEventMapper eventMapper;

    @Test
    void record_and_snapshot_stats() {
        ChatClarifyEvent event = new ChatClarifyEvent();
        event.setSessionId("session-1");
        event.setReason(ChatClarifyReason.ORDER.name());
        event.setQuestion("订单状态");
        event.setCreatedAt(LocalDateTime.now());
        eventMapper.insert(event);

        ChatClarifyStats stats = statsService.snapshot();

        assertThat(stats.total()).isGreaterThanOrEqualTo(1);
        assertThat(stats.reasons()).isNotEmpty();
    }
}
