package com.intelligent.customer.business;

import java.util.List;

/**
 * 业务查询 DSL，描述查询意图与结构。
 */
public record BusinessQueryDsl(
        String from,
        List<JoinSpec> joins,
        List<SelectField> select,
        List<FilterCondition> where,
        List<AggregateSpec> aggregates,
        List<String> groupBy,
        List<OrderSpec> orderBy,
        Integer limit,
        Integer offset
) {
}
