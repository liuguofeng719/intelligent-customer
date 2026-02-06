package com.intelligent.customer.faq;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;

import java.util.List;

/**
 * FAQ 文档对象，用于转换为向量库文档。
 *
 * @param title   标题
 * @param content 内容
 * @param tags    标签
 */
public record FaqDocument(String title, String content, List<String> tags) {

    /**
     * 转为 LangChain4j Document，并附带元数据。
     */
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
