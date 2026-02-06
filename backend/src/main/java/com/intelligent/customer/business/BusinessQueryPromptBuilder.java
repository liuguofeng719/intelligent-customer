package com.intelligent.customer.business;

import org.springframework.stereotype.Component;

import java.util.StringJoiner;

/**
 * NL2DSL 提示词构建器。
 */
@Component
public class BusinessQueryPromptBuilder {

    public String buildPrompt(String question) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("你是业务查询助手，需要将用户问题转为 JSON DSL。");
        joiner.add("只输出 JSON，不要输出多余文字。");
        joiner.add("可用实体：orders, customer, product。");
        joiner.add("字段白名单：");
        joiner.add("- orders: id, customerId, productId, status, amount, createdAt");
        joiner.add("- customer: id, name, phone");
        joiner.add("- product: id, name, price, stock");
        joiner.add("DSL 结构：");
        joiner.add("{");
        joiner.add("  \"from\": \"orders|customer|product\",");
        joiner.add("  \"joins\": [{\"entity\":\"customer\"},{\"entity\":\"product\"}],");
        joiner.add("  \"select\": [{\"entity\":\"orders\",\"field\":\"id\",\"alias\":\"\"}],");
        joiner.add("  \"where\": [{\"entity\":\"orders\",\"field\":\"id\",\"op\":\"=\",\"value\":1}],");
        joiner.add("  \"aggregates\": [{\"entity\":\"orders\",\"field\":\"amount\",\"function\":\"sum\",\"alias\":\"total\"}],");
        joiner.add("  \"groupBy\": [\"orders.id\"],");
        joiner.add("  \"orderBy\": [{\"entity\":\"orders\",\"field\":\"createdAt\",\"direction\":\"desc\"}],");
        joiner.add("  \"limit\": 10,");
        joiner.add("  \"offset\": 0");
        joiner.add("}");
        joiner.add("用户问题：" + question);
        return joiner.toString();
    }
}
