package com.intelligent.customer.api;

import com.intelligent.customer.api.dto.ChatRequest;
import com.intelligent.customer.business.BusinessQueryResult;
import com.intelligent.customer.business.BusinessQueryService;
import com.intelligent.customer.chat.ChatService;
import com.intelligent.customer.faq.FaqSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 对话接口，负责整合 FAQ 检索与大模型回复。
 */
@RestController
@RequestMapping("/api")
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;
    private final FaqSearchService faqSearchService;
    private final BusinessQueryService businessQueryService;

    public ChatController(ChatService chatService,
                          FaqSearchService faqSearchService,
                          BusinessQueryService businessQueryService) {
        this.chatService = chatService;
        this.faqSearchService = faqSearchService;
        this.businessQueryService = businessQueryService;
    }

    /**
     * 基于问题返回 SSE 流式回复。
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestBody ChatRequest request) {
        SseEmitter emitter = new SseEmitter();
        CompletableFuture.runAsync(() -> {
            try {
                log.info("对话请求接收 question={}", request.question());
                List<String> faqSnippets = faqSearchService.search(request.question(), 3);
                BusinessQueryResult queryResult = businessQueryService.tryBuildSummary(request.question());
                String summary = queryResult.hasSummary() ? queryResult.summary() : "";
                log.info("对话链路检索完成 faqCount={} summaryHit={}", faqSnippets.size(), queryResult.hasSummary());
                String answer = chatService.answer(request.question(), faqSnippets, summary);
                emitter.send(SseEmitter.event().data(answer));
                emitter.complete();
                log.info("对话链路完成 questionLength={} answerLength={}", request.question().length(), answer.length());
            } catch (Exception ex) {
                log.warn("对话链路处理失败 question={}", request.question(), ex);
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }
}
