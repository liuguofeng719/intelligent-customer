package com.intelligent.customer.business;

/**
 * 排序定义。
 */
public record OrderSpec(
        String entity,
        String field,
        String direction
) {
}
