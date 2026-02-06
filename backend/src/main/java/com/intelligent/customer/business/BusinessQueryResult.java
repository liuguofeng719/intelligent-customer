package com.intelligent.customer.business;

import java.util.List;
import java.util.Map;

/**
 * 业务查询结果，提供摘要与原始记录。
 */
public record BusinessQueryResult(
        String summary,
        BusinessQueryDsl dsl,
        BusinessQuerySql sql,
        List<Map<String, Object>> rows
) {
    public boolean hasSummary() {
        return summary != null && !summary.isBlank();
    }
}
