package com.intelligent.customer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "faq")
public record FaqProperties(
        Integer chunkSize,
        Integer overlap
) {
}
