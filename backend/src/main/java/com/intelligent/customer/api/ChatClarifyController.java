package com.intelligent.customer.api;

import com.intelligent.customer.api.dto.ChatClarifyConfigRequest;
import com.intelligent.customer.api.dto.ChatClarifyConfigResponse;
import com.intelligent.customer.api.dto.ChatClarifyStatsResponse;
import com.intelligent.customer.chat.ChatClarifyConfigService;
import com.intelligent.customer.chat.ChatClarifySettings;
import com.intelligent.customer.chat.ChatClarifyStats;
import com.intelligent.customer.chat.ChatClarifyStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 澄清追问配置与统计接口。
 */
@RestController
@RequestMapping("/api/chat/clarify")
public class ChatClarifyController {

    private final ChatClarifyConfigService configService;
    private final ChatClarifyStatsService statsService;

    public ChatClarifyController(ChatClarifyConfigService configService,
                                 ChatClarifyStatsService statsService) {
        this.configService = configService;
        this.statsService = statsService;
    }

    @GetMapping("/config")
    public ChatClarifyConfigResponse getConfig() {
        ChatClarifySettings settings = configService.getSettings();
        // 返回当前配置
        return new ChatClarifyConfigResponse(
                settings.shortQuestionLength(),
                settings.minQuestionLength(),
                settings.enablePronounCheck(),
                settings.genericTemplate(),
                settings.pronounTemplate(),
                settings.orderTemplate(),
                settings.productTemplate(),
                settings.customerTemplate(),
                null
        );
    }

    @PutMapping("/config")
    public ChatClarifyConfigResponse updateConfig(@RequestBody ChatClarifyConfigRequest request) {
        // 读取当前配置并按请求字段覆盖
        ChatClarifySettings current = configService.getSettings();
        ChatClarifySettings updated = new ChatClarifySettings(
                request.shortQuestionLength() == null ? current.shortQuestionLength() : request.shortQuestionLength(),
                request.minQuestionLength() == null ? current.minQuestionLength() : request.minQuestionLength(),
                request.enablePronounCheck() == null ? current.enablePronounCheck() : request.enablePronounCheck(),
                request.genericTemplate() == null ? current.genericTemplate() : request.genericTemplate(),
                request.pronounTemplate() == null ? current.pronounTemplate() : request.pronounTemplate(),
                request.orderTemplate() == null ? current.orderTemplate() : request.orderTemplate(),
                request.productTemplate() == null ? current.productTemplate() : request.productTemplate(),
                request.customerTemplate() == null ? current.customerTemplate() : request.customerTemplate()
        );
        ChatClarifySettings saved = configService.updateSettings(updated);
        return new ChatClarifyConfigResponse(
                saved.shortQuestionLength(),
                saved.minQuestionLength(),
                saved.enablePronounCheck(),
                saved.genericTemplate(),
                saved.pronounTemplate(),
                saved.orderTemplate(),
                saved.productTemplate(),
                saved.customerTemplate(),
                null
        );
    }

    @GetMapping("/stats")
    public ChatClarifyStatsResponse getStats() {
        ChatClarifyStats stats = statsService.snapshot();
        // 返回当前统计快照
        return new ChatClarifyStatsResponse(stats.total(), stats.today(), stats.reasons());
    }
}
