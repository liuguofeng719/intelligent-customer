package com.intelligent.customer.faq;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FaqParserTest {

    private final FaqParser parser = new FaqParser();

    @Test
    void parse_markdown_to_chunks() {
        String md = """
                # 退款
                退款需要提供订单号与支付凭证。

                # 发货
                一般 48 小时内发货。
                """;

        List<FaqDocument> docs = parser.parseMarkdown(md);

        assertThat(docs).hasSize(2);
        assertThat(docs.get(0).title()).contains("退款");
        assertThat(docs.get(0).content()).contains("订单号");
    }
}
