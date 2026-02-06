package com.intelligent.customer.faq;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FAQ 入库服务，负责解析并写入向量库。
 */
@Service
public class FaqIngestService {
    private final FaqParser parser;
    private final EmbeddingStoreIngestor ingestor;

    public FaqIngestService(FaqParser parser, EmbeddingStoreIngestor ingestor) {
        this.parser = parser;
        this.ingestor = ingestor;
    }

    /**
     * 解析内容并写入向量库。
     *
     * @param sourceType 内容格式
     * @param content    FAQ 原文
     * @return 入库文档数量
     */
    public int ingest(FaqSourceType sourceType, String content) {
        List<FaqDocument> faqDocuments = parser.parse(sourceType, content);
        List<Document> documents = faqDocuments.stream()
                .map(FaqDocument::toDocument)
                .toList();
        if (documents.isEmpty()) {
            return 0;
        }
        ingestor.ingest(documents);
        return documents.size();
    }
}
