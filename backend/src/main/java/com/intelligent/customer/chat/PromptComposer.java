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
    public String compose(String question, List<String> faqSnippets, String businessSummary) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是智能客服，请基于业务事实与FAQ回答用户问题。\n");
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
        prompt.append("请给出简洁、准确的回复。");
        return prompt.toString();
    }
}
