package com.intelligent.customer.business;

/**
 * 聚合字段定义。
 */
public record AggregateSpec(
        String entity,
        String field,
        String function,
        String alias
) {
}
