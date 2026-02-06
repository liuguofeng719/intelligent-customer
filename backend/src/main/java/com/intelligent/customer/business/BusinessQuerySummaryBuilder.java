package com.intelligent.customer.business;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 业务摘要构建器，将查询结果转为自然语言摘要。
 */
@Component
public class BusinessQuerySummaryBuilder {

    public String buildSummary(List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return "";
        }
        List<String> lines = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            List<String> parts = new ArrayList<>();
            row.keySet().stream()
                    .sorted(Comparator.naturalOrder())
                    .forEach(key -> {
                        String normalizedKey = key == null ? "" : key.toLowerCase(Locale.ROOT);
                        parts.add(normalizedKey + "=" + row.get(key));
                    });
            lines.add(String.join(", ", parts));
        }
        StringBuilder summary = new StringBuilder();
        summary.append("业务查询结果：\n");
        for (String line : lines) {
            summary.append("- ").append(line).append('\n');
        }
        return summary.toString().trim();
    }
}
