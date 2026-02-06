package com.intelligent.customer.business;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 业务查询字段白名单与表结构映射。
 */
@Component
public class BusinessQuerySchema {

    private final Map<String, Map<String, String>> fieldMappings = new LinkedHashMap<>();
    private final Map<String, String> tableAliases = new LinkedHashMap<>();

    public BusinessQuerySchema() {
        tableAliases.put("orders", "o");
        tableAliases.put("customer", "c");
        tableAliases.put("product", "p");

        Map<String, String> orders = new LinkedHashMap<>();
        orders.put("id", "id");
        orders.put("customerId", "customer_id");
        orders.put("productId", "product_id");
        orders.put("status", "status");
        orders.put("amount", "amount");
        orders.put("createdAt", "created_at");
        fieldMappings.put("orders", orders);

        Map<String, String> customer = new LinkedHashMap<>();
        customer.put("id", "id");
        customer.put("name", "name");
        customer.put("phone", "phone");
        fieldMappings.put("customer", customer);

        Map<String, String> product = new LinkedHashMap<>();
        product.put("id", "id");
        product.put("name", "name");
        product.put("price", "price");
        product.put("stock", "stock");
        fieldMappings.put("product", product);
    }

    public boolean supportsEntity(String entity) {
        return fieldMappings.containsKey(entity);
    }

    public boolean supportsField(String entity, String field) {
        Map<String, String> mapping = fieldMappings.get(entity);
        return mapping != null && mapping.containsKey(field);
    }

    public String resolveColumn(String entity, String field) {
        Map<String, String> mapping = fieldMappings.get(entity);
        if (mapping == null) {
            return null;
        }
        return mapping.get(field);
    }

    public String aliasOf(String entity) {
        return tableAliases.get(entity);
    }

    public Set<String> supportedEntities() {
        return new LinkedHashSet<>(fieldMappings.keySet());
    }
}
