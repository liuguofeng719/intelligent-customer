package com.intelligent.customer.api;

import com.intelligent.customer.api.dto.ChatRequest;
import com.intelligent.customer.api.dto.ChatResponse;
import com.intelligent.customer.api.dto.ChatSessionResetRequest;
import com.intelligent.customer.api.dto.ChatSessionResponse;
import com.intelligent.customer.business.BusinessQueryResult;
import com.intelligent.customer.business.BusinessQueryService;
import com.intelligent.customer.chat.ChatClarifyService;
import com.intelligent.customer.chat.ChatClarifyResult;
import com.intelligent.customer.chat.ChatClarifyStatsService;
import com.intelligent.customer.chat.ChatHistoryItem;
import com.intelligent.customer.chat.ChatIntent;
import com.intelligent.customer.chat.ChatIntentService;
import com.intelligent.customer.chat.ChatMemoryService;
import com.intelligent.customer.chat.ChatRole;
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
    private final ChatMemoryService chatMemoryService;
    private final ChatIntentService chatIntentService;
    private final ChatClarifyService chatClarifyService;
    private final ChatClarifyStatsService chatClarifyStatsService;

    public ChatController(ChatService chatService,
                          FaqSearchService faqSearchService,
                          BusinessQueryService businessQueryService,
                          ChatMemoryService chatMemoryService,
                          ChatIntentService chatIntentService,
                          ChatClarifyService chatClarifyService,
                          ChatClarifyStatsService chatClarifyStatsService) {
        this.chatService = chatService;
        this.faqSearchService = faqSearchService;
        this.businessQueryService = businessQueryService;
        this.chatMemoryService = chatMemoryService;
        this.chatIntentService = chatIntentService;
        this.chatClarifyService = chatClarifyService;
        this.chatClarifyStatsService = chatClarifyStatsService;
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
                String sessionId = chatMemoryService.ensureSession(request.sessionId());
                List<ChatHistoryItem> history = chatMemoryService.loadHistory(sessionId);
                ChatIntent currentIntent = chatMemoryService.loadIntent(sessionId);
                ChatIntent intent = chatIntentService.resolveIntent(currentIntent, request.question(), history);
                chatMemoryService.updateIntent(sessionId, intent);
                ChatClarifyResult clarifyResult = chatClarifyService.buildClarifyQuestion(request.question(), intent, history);
                if (clarifyResult != null) {
                    chatMemoryService.appendMessage(sessionId, ChatRole.USER, request.question());
                    chatMemoryService.appendMessage(sessionId, ChatRole.ASSISTANT, clarifyResult.message());
                    chatClarifyStatsService.recordEvent(sessionId, clarifyResult.reason(), request.question());
                    emitter.send(SseEmitter.event().data(new ChatResponse(sessionId, clarifyResult.message(), intent.label())));
                    emitter.complete();
                    log.info("对话链路澄清触发 sessionId={}", sessionId);
                    return;
                }
                List<String> faqSnippets = faqSearchService.search(request.question(), 3);
                BusinessQueryResult queryResult = businessQueryService.tryBuildSummary(request.question());
                boolean summaryHit = queryResult != null && queryResult.hasSummary();
                String summary = summaryHit ? queryResult.summary() : "";
                log.info("对话链路检索完成 faqCount={} summaryHit={}", faqSnippets.size(), summaryHit);
                String answer = chatService.answer(request.question(), faqSnippets, summary, history, intent);
                chatMemoryService.appendMessage(sessionId, ChatRole.USER, request.question());
                chatMemoryService.appendMessage(sessionId, ChatRole.ASSISTANT, answer);
                emitter.send(SseEmitter.event().data(new ChatResponse(sessionId, answer, intent.label())));
                emitter.complete();
                log.info("对话链路完成 questionLength={} answerLength={}", request.question().length(), answer.length());
            } catch (Exception ex) {
                log.warn("对话链路处理失败 question={}", request.question(), ex);
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    /**
     * 重置会话，清空历史并返回新的会话标识。
     */
    @PostMapping("/chat/session/reset")
    public ChatSessionResponse resetSession(@RequestBody ChatSessionResetRequest request) {
        String sessionId = chatMemoryService.resetSession(request.sessionId());
        return new ChatSessionResponse(sessionId, ChatIntent.GENERAL.label());
    }
}
