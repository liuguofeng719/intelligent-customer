package com.intelligent.customer.business;

import java.util.List;

/**
 * Vanna SQL 响应体。
 */
public record VannaSqlResponse(
        String sql,
        List<String> columns,
        Double confidence
) {
}
