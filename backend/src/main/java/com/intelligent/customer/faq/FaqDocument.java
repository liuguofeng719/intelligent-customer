package com.intelligent.customer.faq;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;

import java.util.List;

public record FaqDocument(String title, String content, List<String> tags) {

    public Document toDocument() {
        Metadata metadata = new Metadata();
        if (title != null && !title.isBlank()) {
            metadata.put("title", title);
        }
        if (tags != null && !tags.isEmpty()) {
            metadata.put("tags", String.join(",", tags));
        }
        return Document.from(content == null ? "" : content, metadata);
    }
}
