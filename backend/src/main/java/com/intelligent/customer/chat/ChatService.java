package com.intelligent.customer.chat;

import dev.langchain4j.model.chat.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    private final ChatModel chatModel;
    private final PromptComposer promptComposer;

    public ChatService(ChatModel chatModel, PromptComposer promptComposer) {
        this.chatModel = chatModel;
        this.promptComposer = promptComposer;
    }

    public String answer(String question, List<String> faqSnippets, String businessSummary) {
        String prompt = promptComposer.compose(question, faqSnippets, businessSummary);
        return chatModel.chat(prompt);
    }
}
