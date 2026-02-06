package com.intelligent.customer.config;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 向量库与 FAQ 分段相关配置。
 * 约束：Milvus 必须可连接；默认维度与分段参数在配置缺省时生效。
 */
@Configuration
@EnableConfigurationProperties({MilvusProperties.class, FaqProperties.class})
public class VectorStoreConfig {

    /**
     * 构建 Milvus 向量存储实例。
     */
    @Bean
    public MilvusEmbeddingStore milvusEmbeddingStore(MilvusProperties properties) {
        int dimension = properties.dimension() == null ? 768 : properties.dimension();
        return MilvusEmbeddingStore.builder()
                .host(properties.host())
                .port(properties.port())
                .collectionName(properties.collectionName())
                .dimension(dimension)
                .indexType(IndexType.HNSW)
                .metricType(MetricType.COSINE)
                .autoFlushOnInsert(true)
                .build();
    }

    /**
     * 构建 FAQ 入库组件，负责分段与向量写入。
     */
    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor(EmbeddingModel embeddingModel,
                                                         MilvusEmbeddingStore embeddingStore,
                                                         FaqProperties faqProperties) {
        int chunkSize = faqProperties.chunkSize() == null ? 300 : faqProperties.chunkSize();
        int overlap = faqProperties.overlap() == null ? 30 : faqProperties.overlap();
        DocumentSplitter splitter = DocumentSplitters.recursive(chunkSize, overlap);
        return EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
    }
}
