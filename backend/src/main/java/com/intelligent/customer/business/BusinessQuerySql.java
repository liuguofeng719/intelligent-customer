package com.intelligent.customer.business;

import java.util.List;

/**
 * SQL 构建结果，包含语句与参数。
 */
public record BusinessQuerySql(
        String sql,
        List<Object> params
) {
}
