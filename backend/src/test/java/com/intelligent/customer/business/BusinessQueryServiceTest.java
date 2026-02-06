package com.intelligent.customer.business;

import dev.langchain4j.model.chat.ChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 业务查询链路测试，验证 NL2DSL + SQL 查询 + 摘要输出。
 */
@SpringBootTest
@ActiveProfiles("test")
class BusinessQueryServiceTest {

    @Autowired
    private BusinessQueryService service;

    @Test
    void build_summary_from_query() {
        BusinessQueryResult result = service.tryBuildSummary("查询订单1");

        assertThat(result.summary()).contains("id=1");
        assertThat(result.summary()).contains("status=PAID");
    }

    @Test
    void sql_runner_can_query_orders() {
        List<Map<String, Object>> rows = com.baomidou.mybatisplus.extension.toolkit.SqlRunner
                .db(com.intelligent.customer.domain.Order.class)
                .selectList("select id, status, amount from orders where id = {0}", 1);
        assertThat(rows).isNotEmpty();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        ChatModel chatModel() {
            return new FakeChatModel();
        }
    }

    static class FakeChatModel implements ChatModel {
        @Override
        public String chat(String prompt) {
            return """
                    {
                      "from": "orders",
                      "select": [
                        {"entity": "orders", "field": "id"},
                        {"entity": "orders", "field": "status"},
                        {"entity": "orders", "field": "amount"}
                      ],
                      "where": [
                        {"entity": "orders", "field": "id", "op": "=", "value": 1}
                      ],
                      "limit": 1
                    }
                    """;
        }
    }
}
