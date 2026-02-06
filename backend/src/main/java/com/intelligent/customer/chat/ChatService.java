package com.intelligent.customer.chat;

import dev.langchain4j.model.chat.ChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 对话服务，统一封装大模型调用。
 */
@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private final ChatModel chatModel;
    private final PromptComposer promptComposer;

    public ChatService(ChatModel chatModel, PromptComposer promptComposer) {
        this.chatModel = chatModel;
        this.promptComposer = promptComposer;
    }

    /**
     * 生成客服回答。
     *
     * @param question        用户问题
     * @param faqSnippets     FAQ 片段
     * @param businessSummary 业务摘要信息
     * @return 客服回复
     */
    public String answer(String question, List<String> faqSnippets, String businessSummary) {
        log.info("对话服务开始 questionLength={} faqCount={} summaryPresent={}",
                question == null ? 0 : question.length(),
                faqSnippets == null ? 0 : faqSnippets.size(),
                businessSummary != null && !businessSummary.isBlank());
        String prompt = promptComposer.compose(question, faqSnippets, businessSummary);
        log.info("对话服务提示词已构建 promt =\n {} promptLength={}", prompt, prompt.length());
        return chatModel.chat(prompt);
    }
}
