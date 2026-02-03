package com.intelligent.customer.api;

import com.intelligent.customer.api.dto.ChatRequest;
import com.intelligent.customer.chat.ChatService;
import com.intelligent.customer.faq.FaqSearchService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class ChatController {
    private final ChatService chatService;
    private final FaqSearchService faqSearchService;

    public ChatController(ChatService chatService, FaqSearchService faqSearchService) {
        this.chatService = chatService;
        this.faqSearchService = faqSearchService;
    }

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestBody ChatRequest request) {
        SseEmitter emitter = new SseEmitter();
        CompletableFuture.runAsync(() -> {
            try {
                List<String> faqSnippets = faqSearchService.search(request.question(), 3);
                String answer = chatService.answer(request.question(), faqSnippets, "");
                emitter.send(SseEmitter.event().data(answer));
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }
}
