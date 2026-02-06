package com.intelligent.customer.business;

import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * DSL 校验器，保证字段与操作符符合白名单。
 */
@Component
public class BusinessQueryDslValidator {

    private static final Set<String> SUPPORTED_OPS = Set.of(
            "=", "!=", ">", ">=", "<", "<=", "like", "in", "between"
    );

    private final BusinessQuerySchema schema;

    public BusinessQueryDslValidator(BusinessQuerySchema schema) {
        this.schema = schema;
    }

    public String validate(BusinessQueryDsl dsl) {
        if (dsl == null) {
            return "DSL 为空";
        }
        if (dsl.from() == null || !schema.supportsEntity(dsl.from())) {
            return "主表不受支持";
        }
        if ((dsl.select() == null || dsl.select().isEmpty())
                && (dsl.aggregates() == null || dsl.aggregates().isEmpty())) {
            return "未指定查询字段";
        }
        if (dsl.joins() != null) {
            for (JoinSpec join : dsl.joins()) {
                if (join == null || !schema.supportsEntity(join.entity())) {
                    return "关联表不受支持";
                }
            }
        }
        if (dsl.select() != null) {
            for (SelectField field : dsl.select()) {
                if (!isValidField(field.entity(), field.field())) {
                    return "查询字段不受支持";
                }
            }
        }
        if (dsl.where() != null) {
            for (FilterCondition condition : dsl.where()) {
                if (!isValidField(condition.entity(), condition.field())) {
                    return "过滤字段不受支持";
                }
                String op = normalize(condition.op());
                if (!SUPPORTED_OPS.contains(op)) {
                    return "过滤操作符不受支持";
                }
            }
        }
        if (dsl.aggregates() != null) {
            for (AggregateSpec aggregate : dsl.aggregates()) {
                if (!isValidField(aggregate.entity(), aggregate.field())) {
                    return "聚合字段不受支持";
                }
            }
        }
        if (dsl.orderBy() != null) {
            for (OrderSpec order : dsl.orderBy()) {
                if (!isValidField(order.entity(), order.field())) {
                    return "排序字段不受支持";
                }
            }
        }
        if (dsl.groupBy() != null) {
            for (String fieldRef : dsl.groupBy()) {
                FieldRef ref = FieldRef.parse(fieldRef);
                if (ref == null || !isValidField(ref.entity(), ref.field())) {
                    return "分组字段不受支持";
                }
            }
        }
        return null;
    }

    private boolean isValidField(String entity, String field) {
        if (entity == null || field == null) {
            return false;
        }
        return schema.supportsField(entity, field);
    }

    private String normalize(String op) {
        if (op == null) {
            return "";
        }
        return op.trim().toLowerCase(Locale.ROOT);
    }

    record FieldRef(String entity, String field) {
        static FieldRef parse(String raw) {
            if (raw == null || raw.isBlank()) {
                return null;
            }
            String trimmed = raw.trim();
            int dot = trimmed.indexOf('.');
            if (dot > 0) {
                return new FieldRef(trimmed.substring(0, dot), trimmed.substring(dot + 1));
            }
            return null;
        }
    }
}
