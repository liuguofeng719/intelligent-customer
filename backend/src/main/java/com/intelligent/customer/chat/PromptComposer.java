package com.intelligent.customer.chat;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 提示词拼装器，统一构造客服上下文。
 */
@Component
public class PromptComposer {

    /**
     * 组装提示词，包含业务事实、FAQ 与用户问题。
     */
    public String compose(String question,
                          List<String> faqSnippets,
                          String businessSummary,
                          List<ChatHistoryItem> history,
                          ChatIntent intent) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是智能客服，请基于业务事实与FAQ回答用户问题。\n");
        if (intent != null && intent != ChatIntent.GENERAL) {
            prompt.append("当前意图：").append(intent.label()).append('\n');
        }
        if (history != null && !history.isEmpty()) {
            prompt.append("历史对话：\n");
            for (ChatHistoryItem item : history) {
                if (item == null || item.content() == null || item.content().isBlank()) {
                    continue;
                }
                String roleLabel = item.role() == null ? ChatRole.USER.label() : item.role().label();
                prompt.append(roleLabel).append("：").append(item.content().trim()).append('\n');
            }
        }
        if (businessSummary != null && !businessSummary.isBlank()) {
            prompt.append("业务事实：\n");
            prompt.append(businessSummary).append('\n');
        }
        if (faqSnippets != null && !faqSnippets.isEmpty()) {
            prompt.append("FAQ片段：\n");
            for (String snippet : faqSnippets) {
                if (snippet == null || snippet.isBlank()) {
                    continue;
                }
                prompt.append("- ").append(snippet.trim()).append('\n');
            }
        }
        prompt.append("用户问题：").append(question).append('\n');
        prompt.append("回复要求：语气自然、简洁、每句不超过20字，最多3句。\n");
        prompt.append("如果需要澄清，只问1个最关键的问题。");
        return prompt.toString();
    }
}
