package com.intelligent.customer.business;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SQL 生成器测试，确保 DSL 到 SQL 的映射稳定。
 */
class BusinessQuerySqlBuilderTest {

    @Test
    void build_sql_with_joins_and_conditions() {
        BusinessQueryDsl dsl = new BusinessQueryDsl(
                "orders",
                List.of(new JoinSpec("customer"), new JoinSpec("product")),
                List.of(
                        new SelectField("orders", "id", null),
                        new SelectField("customer", "name", "customerName")
                ),
                List.of(new FilterCondition("orders", "id", "=", 1)),
                List.of(new AggregateSpec("orders", "amount", "sum", "totalAmount")),
                List.of("orders.id"),
                List.of(new OrderSpec("orders", "createdAt", "desc")),
                5,
                0
        );

        BusinessQuerySqlBuilder builder = new BusinessQuerySqlBuilder(new BusinessQuerySchema());
        BusinessQuerySql sql = builder.build(dsl);

        assertThat(sql.sql()).contains("from orders o");
        assertThat(sql.sql()).contains("join customer c");
        assertThat(sql.sql()).contains("join product p");
        assertThat(sql.sql()).contains("where o.id = {0}");
        assertThat(sql.sql()).contains("order by o.created_at desc");
        assertThat(sql.sql()).contains("limit 5");
        assertThat(sql.sql()).contains("offset 0");
        assertThat(sql.params()).containsExactly(1);
    }
}
