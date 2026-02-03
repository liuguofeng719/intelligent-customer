package com.intelligent.customer.faq;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaqIngestService {
    private final FaqParser parser;
    private final EmbeddingStoreIngestor ingestor;

    public FaqIngestService(FaqParser parser, EmbeddingStoreIngestor ingestor) {
        this.parser = parser;
        this.ingestor = ingestor;
    }

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
