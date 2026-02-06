package com.intelligent.customer.business;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * DSL 解析器，将模型输出转为 DSL 对象。
 */
@Component
public class BusinessQueryDslParser {

    private final ObjectMapper objectMapper;

    public BusinessQueryDslParser() {
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public BusinessQueryDsl parse(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("DSL 输出为空");
        }
        String json = extractJson(raw);
        try {
            return objectMapper.readValue(json, BusinessQueryDsl.class);
        } catch (Exception ex) {
            throw new IllegalArgumentException("DSL 解析失败", ex);
        }
    }

    private String extractJson(String raw) {
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return raw.substring(start, end + 1);
        }
        return raw;
    }
}
