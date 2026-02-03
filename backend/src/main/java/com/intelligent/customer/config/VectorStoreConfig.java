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

@Configuration
@EnableConfigurationProperties({MilvusProperties.class, FaqProperties.class})
public class VectorStoreConfig {

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
