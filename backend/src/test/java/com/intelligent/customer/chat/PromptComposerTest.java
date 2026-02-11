package com.intelligent.customer.chat;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PromptComposerTest {

    private final PromptComposer composer = new PromptComposer();

    @Test
    void compose_prompt_contains_facts_and_faq() {
        String prompt = composer.compose(
                "订单什么时候发货？",
                List.of("发货通常在48小时内完成。"),
                "订单#1001：已付款，待发货。",
                List.of(new ChatHistoryItem(ChatRole.USER, "我的订单呢？")),
                ChatIntent.ORDER
        );

        assertThat(prompt).contains("历史对话");
        assertThat(prompt).contains("FAQ片段");
        assertThat(prompt).contains("订单#1001");
        assertThat(prompt).contains("订单什么时候发货");
    }
}
