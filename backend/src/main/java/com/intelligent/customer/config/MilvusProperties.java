package com.intelligent.customer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Milvus 连接与集合配置。
 *
 * @param host           服务地址
 * @param port           服务端口
 * @param collectionName 集合名称
 * @param dimension      向量维度
 */
@ConfigurationProperties(prefix = "milvus")
public record MilvusProperties(
        String host,
        Integer port,
        String collectionName,
        Integer dimension
) {
}
