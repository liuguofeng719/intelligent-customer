package com.intelligent.customer.business;

/**
 * Vanna SQL 请求体。
 */
public record VannaSqlRequest(
        String question,
        String schema,
        String dialect
) {
}
