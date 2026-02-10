package com.intelligent.customer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Vanna 服务配置。
 */
@ConfigurationProperties(prefix = "vanna")
public record VannaProperties(
        String baseUrl,
        String sqlPath,
        Duration timeout,
        String dialect
) {
    public VannaProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "http://localhost:8081";
        }
        if (sqlPath == null || sqlPath.isBlank()) {
            sqlPath = "/api/v1/sql";
        }
        if (timeout == null) {
            timeout = Duration.ofSeconds(30);
        }
        if (dialect == null || dialect.isBlank()) {
            dialect = "mysql";
        }
    }
}
