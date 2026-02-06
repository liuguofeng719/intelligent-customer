package com.intelligent.customer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * FAQ 切分配置。
 *
 * @param chunkSize 单段长度
 * @param overlap   相邻段重叠字符数
 */
@ConfigurationProperties(prefix = "faq")
public record FaqProperties(
        Integer chunkSize,
        Integer overlap
) {
}
