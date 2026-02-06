package com.intelligent.customer.business;

/**
 * 过滤条件定义。
 */
public record FilterCondition(
        String entity,
        String field,
        String op,
        Object value
) {
}
