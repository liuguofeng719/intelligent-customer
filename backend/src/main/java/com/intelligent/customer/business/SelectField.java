package com.intelligent.customer.business;

/**
 * 查询字段定义。
 */
public record SelectField(
        String entity,
        String field,
        String alias
) {
}
