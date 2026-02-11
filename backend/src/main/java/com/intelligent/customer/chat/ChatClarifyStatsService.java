package com.intelligent.customer.chat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.intelligent.customer.domain.ChatClarifyEvent;
import com.intelligent.customer.repo.ChatClarifyEventMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 澄清触发统计服务。
 */
@Service
public class ChatClarifyStatsService {

    private final ChatClarifyEventMapper eventMapper;

    public ChatClarifyStatsService(ChatClarifyEventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    public void recordEvent(String sessionId, ChatClarifyReason reason, String question) {
        if (reason == null) {
            return;
        }
        // 记录澄清触发事件，用于后续统计
        ChatClarifyEvent event = new ChatClarifyEvent();
        event.setSessionId(sessionId);
        event.setReason(reason.name());
        event.setQuestion(question);
        event.setCreatedAt(LocalDateTime.now());
        eventMapper.insert(event);
    }

    /**
     * 汇总澄清触发统计。
     */
    public ChatClarifyStats snapshot() {
        Long total = eventMapper.selectCount(new QueryWrapper<>());
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        QueryWrapper<ChatClarifyEvent> todayWrapper = new QueryWrapper<>();
        todayWrapper.ge("created_at", startOfDay);
        Long today = eventMapper.selectCount(todayWrapper);

        // 按原因统计触发次数
        QueryWrapper<ChatClarifyEvent> groupWrapper = new QueryWrapper<>();
        groupWrapper.select("reason", "count(*) as count")
                .groupBy("reason");
        List<Map<String, Object>> rows = eventMapper.selectMaps(groupWrapper);
        List<ChatClarifyReasonCount> counts = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            String reason = String.valueOf(row.get("reason"));
            Object countValue = row.get("count");
            long count = countValue == null ? 0L : Long.parseLong(countValue.toString());
            try {
                ChatClarifyReason reasonEnum = ChatClarifyReason.valueOf(reason);
                counts.add(new ChatClarifyReasonCount(reasonEnum.name(), reasonEnum.label(), count));
            } catch (Exception ex) {
                counts.add(new ChatClarifyReasonCount(reason, reason, count));
            }
        }
        return new ChatClarifyStats(total == null ? 0L : total, today == null ? 0L : today, counts);
    }
}
