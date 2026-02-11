package com.intelligent.customer.chat;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChatClarifyServiceTest {

    @Test
    void clarify_when_order_missing_identifier() {
        ChatClarifyConfigService configService = Mockito.mock(ChatClarifyConfigService.class);
        Mockito.when(configService.getSettings())
                .thenReturn(new ChatClarifySettings(
                        3,
                        6,
                        true,
                        "请补充您要咨询的具体问题。",
                        "请说明具体指的是哪一单或哪件商品？",
                        "请提供订单号或下单手机号。",
                        "请提供商品名称或编号。",
                        "请提供客户编号或手机号。"
                ));
        ChatClarifyService service = new ChatClarifyService(configService);

        ChatClarifyResult result = service.buildClarifyQuestion("订单状态", ChatIntent.ORDER, List.of());

        assertThat(result).isNotNull();
        assertThat(result.message()).contains("订单号");
        assertThat(result.reason()).isEqualTo(ChatClarifyReason.ORDER);
    }
}
