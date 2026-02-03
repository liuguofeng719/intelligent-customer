package com.intelligent.customer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "milvus")
public record MilvusProperties(
        String host,
        Integer port,
        String collectionName,
        Integer dimension
) {
}
