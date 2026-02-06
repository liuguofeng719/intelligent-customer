package com.intelligent.customer.business;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * SQL 生成器，将 DSL 转为可执行 SQL。
 */
@Component
public class BusinessQuerySqlBuilder {

    private final BusinessQuerySchema schema;

    public BusinessQuerySqlBuilder(BusinessQuerySchema schema) {
        this.schema = schema;
    }

    public BusinessQuerySql build(BusinessQueryDsl dsl) {
        String from = dsl.from();
        String fromAlias = schema.aliasOf(from);
        List<Object> params = new ArrayList<>();
        ParamIndex index = new ParamIndex();

        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append(buildSelect(dsl));
        sql.append(" from ").append(from).append(' ').append(fromAlias);

        List<String> joins = buildJoinClauses(from, collectEntities(dsl));
        for (String joinClause : joins) {
            sql.append(' ').append(joinClause);
        }

        String whereClause = buildWhere(dsl, params, index);
        if (!whereClause.isBlank()) {
            sql.append(" where ").append(whereClause);
        }

        String groupByClause = buildGroupBy(dsl);
        if (!groupByClause.isBlank()) {
            sql.append(" group by ").append(groupByClause);
        }

        String orderByClause = buildOrderBy(dsl);
        if (!orderByClause.isBlank()) {
            sql.append(" order by ").append(orderByClause);
        }

        if (dsl.limit() != null) {
            sql.append(" limit ").append(dsl.limit());
        }
        if (dsl.offset() != null) {
            sql.append(" offset ").append(dsl.offset());
        }

        return new BusinessQuerySql(sql.toString(), params);
    }

    private String buildSelect(BusinessQueryDsl dsl) {
        List<String> columns = new ArrayList<>();
        if (dsl.select() != null) {
            for (SelectField field : dsl.select()) {
                String column = toColumn(field.entity(), field.field());
                String alias = schema.aliasOf(field.entity());
                String select = alias + "." + column;
                if (field.alias() != null && !field.alias().isBlank()) {
                    select += " as " + field.alias();
                }
                columns.add(select);
            }
        }
        if (dsl.aggregates() != null) {
            for (AggregateSpec aggregate : dsl.aggregates()) {
                String column = toColumn(aggregate.entity(), aggregate.field());
                String alias = schema.aliasOf(aggregate.entity());
                String function = aggregate.function().toLowerCase(Locale.ROOT);
                String select = function + "(" + alias + "." + column + ")";
                if (aggregate.alias() != null && !aggregate.alias().isBlank()) {
                    select += " as " + aggregate.alias();
                }
                columns.add(select);
            }
        }
        if (columns.isEmpty()) {
            columns.add("*");
        }
        return String.join(", ", columns);
    }

    private String buildWhere(BusinessQueryDsl dsl, List<Object> params, ParamIndex index) {
        if (dsl.where() == null || dsl.where().isEmpty()) {
            return "";
        }
        List<String> clauses = new ArrayList<>();
        for (FilterCondition condition : dsl.where()) {
            String alias = schema.aliasOf(condition.entity());
            String column = toColumn(condition.entity(), condition.field());
            String op = condition.op().toLowerCase(Locale.ROOT);
            if ("in".equals(op) && condition.value() instanceof List<?> values) {
                List<String> tokens = new ArrayList<>();
                for (Object value : values) {
                    tokens.add(index.nextPlaceholder());
                    params.add(value);
                }
                String placeholders = String.join(", ", tokens);
                clauses.add(alias + "." + column + " in (" + placeholders + ")");
                continue;
            }
            if ("between".equals(op) && condition.value() instanceof List<?> values && values.size() >= 2) {
                String first = index.nextPlaceholder();
                String second = index.nextPlaceholder();
                clauses.add(alias + "." + column + " between " + first + " and " + second);
                params.add(values.get(0));
                params.add(values.get(1));
                continue;
            }
            String placeholder = index.nextPlaceholder();
            clauses.add(alias + "." + column + " " + op + " " + placeholder);
            params.add(condition.value());
        }
        return String.join(" and ", clauses);
    }

    private String buildGroupBy(BusinessQueryDsl dsl) {
        if (dsl.groupBy() == null || dsl.groupBy().isEmpty()) {
            return "";
        }
        List<String> columns = new ArrayList<>();
        for (String fieldRef : dsl.groupBy()) {
            String value = buildFieldReference(fieldRef, dsl.from());
            if (!value.isBlank()) {
                columns.add(value);
            }
        }
        return String.join(", ", columns);
    }

    private String buildOrderBy(BusinessQueryDsl dsl) {
        if (dsl.orderBy() == null || dsl.orderBy().isEmpty()) {
            return "";
        }
        List<String> columns = new ArrayList<>();
        for (OrderSpec order : dsl.orderBy()) {
            String alias = schema.aliasOf(order.entity());
            String column = toColumn(order.entity(), order.field());
            String direction = order.direction() == null ? "asc" : order.direction().toLowerCase(Locale.ROOT);
            columns.add(alias + "." + column + " " + direction);
        }
        return String.join(", ", columns);
    }

    private String buildFieldReference(String fieldRef, String defaultEntity) {
        if (fieldRef == null || fieldRef.isBlank()) {
            return "";
        }
        String trimmed = fieldRef.trim();
        int dot = trimmed.indexOf('.');
        if (dot > 0) {
            String entity = trimmed.substring(0, dot);
            String field = trimmed.substring(dot + 1);
            return schema.aliasOf(entity) + "." + toColumn(entity, field);
        }
        return schema.aliasOf(defaultEntity) + "." + toColumn(defaultEntity, trimmed);
    }

    private String toColumn(String entity, String field) {
        String column = schema.resolveColumn(entity, field);
        if (column == null) {
            return field;
        }
        return column;
    }

    private Set<String> collectEntities(BusinessQueryDsl dsl) {
        Set<String> entities = new LinkedHashSet<>();
        entities.add(dsl.from());
        if (dsl.joins() != null) {
            for (JoinSpec join : dsl.joins()) {
                if (join != null && join.entity() != null) {
                    entities.add(join.entity());
                }
            }
        }
        if (dsl.select() != null) {
            for (SelectField field : dsl.select()) {
                if (field != null && field.entity() != null) {
                    entities.add(field.entity());
                }
            }
        }
        if (dsl.where() != null) {
            for (FilterCondition condition : dsl.where()) {
                if (condition != null && condition.entity() != null) {
                    entities.add(condition.entity());
                }
            }
        }
        if (dsl.orderBy() != null) {
            for (OrderSpec order : dsl.orderBy()) {
                if (order != null && order.entity() != null) {
                    entities.add(order.entity());
                }
            }
        }
        if (dsl.groupBy() != null) {
            for (String group : dsl.groupBy()) {
                if (group != null && group.contains(".")) {
                    entities.add(group.substring(0, group.indexOf('.')));
                }
            }
        }
        if (dsl.aggregates() != null) {
            for (AggregateSpec aggregate : dsl.aggregates()) {
                if (aggregate != null && aggregate.entity() != null) {
                    entities.add(aggregate.entity());
                }
            }
        }
        return entities;
    }

    private List<String> buildJoinClauses(String from, Set<String> entities) {
        List<String> joins = new ArrayList<>();
        if ("orders".equals(from)) {
            if (entities.contains("customer")) {
                joins.add("join customer c on o.customer_id = c.id");
            }
            if (entities.contains("product")) {
                joins.add("join product p on o.product_id = p.id");
            }
            return joins;
        }
        if ("customer".equals(from)) {
            if (entities.contains("orders") || entities.contains("product")) {
                joins.add("join orders o on o.customer_id = c.id");
            }
            if (entities.contains("product")) {
                joins.add("join product p on o.product_id = p.id");
            }
            return joins;
        }
        if ("product".equals(from)) {
            if (entities.contains("orders") || entities.contains("customer")) {
                joins.add("join orders o on o.product_id = p.id");
            }
            if (entities.contains("customer")) {
                joins.add("join customer c on o.customer_id = c.id");
            }
        }
        return joins;
    }

    private static class ParamIndex {
        private int current = 0;

        String nextPlaceholder() {
            return "{" + current++ + "}";
        }
    }
}
