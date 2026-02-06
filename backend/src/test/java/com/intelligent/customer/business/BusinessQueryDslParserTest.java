package com.intelligent.customer.business;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DSL 解析器测试，验证模型输出能够稳定转为结构化对象。
 */
class BusinessQueryDslParserTest {

    @Test
    void parse_json_dsl_to_object() {
        String json = """
                {
                  "from": "orders",
                  "joins": [
                    {"entity": "customer"},
                    {"entity": "product"}
                  ],
                  "select": [
                    {"entity": "orders", "field": "id"},
                    {"entity": "customer", "field": "name", "alias": "customerName"}
                  ],
                  "where": [
                    {"entity": "orders", "field": "id", "op": "=", "value": 1}
                  ],
                  "orderBy": [
                    {"entity": "orders", "field": "createdAt", "direction": "desc"}
                  ],
                  "limit": 5,
                  "offset": 0
                }
                """;

        BusinessQueryDslParser parser = new BusinessQueryDslParser();
        BusinessQueryDsl dsl = parser.parse(json);

        assertThat(dsl.from()).isEqualTo("orders");
        assertThat(dsl.select()).hasSize(2);
        assertThat(dsl.where()).hasSize(1);
    }

    @Test
    void parse_wrapped_json_payload() {
        String json = """
                ```json
                {
                  "from": "customer",
                  "select": [
                    {"entity": "customer", "field": "name"}
                  ],
                  "limit": 1
                }
                ```
                """;

        BusinessQueryDslParser parser = new BusinessQueryDslParser();
        BusinessQueryDsl dsl = parser.parse(json);

        assertThat(dsl.from()).isEqualTo("customer");
        assertThat(dsl.select()).hasSize(1);
        assertThat(dsl.limit()).isEqualTo(1);
    }
}
