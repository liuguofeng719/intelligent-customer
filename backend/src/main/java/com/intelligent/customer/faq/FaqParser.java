package com.intelligent.customer.faq;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FAQ 解析器，支持 Markdown/CSV/文本。
 */
@Component
public class FaqParser {

    /**
     * 根据格式分发解析逻辑。
     */
    public List<FaqDocument> parse(FaqSourceType sourceType, String content) {
        if (sourceType == null) {
            return parseText(content);
        }
        return switch (sourceType) {
            case MARKDOWN -> parseMarkdown(content);
            case CSV -> parseCsv(content);
            case TEXT -> parseText(content);
        };
    }

    /**
     * 解析 Markdown 标题分段的 FAQ。
     */
    public List<FaqDocument> parseMarkdown(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return Collections.emptyList();
        }
        List<FaqDocument> docs = new ArrayList<>();
        String currentTitle = null;
        StringBuilder current = new StringBuilder();
        String[] lines = markdown.split("\\R");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("#")) {
                appendDocIfPresent(docs, currentTitle, current);
                currentTitle = trimmed.replaceFirst("^#+\\s*", "").trim();
                current.setLength(0);
                continue;
            }
            if (!trimmed.isEmpty()) {
                if (current.length() > 0) {
                    current.append('\n');
                }
                current.append(trimmed);
            }
        }
        appendDocIfPresent(docs, currentTitle, current);
        if (docs.isEmpty()) {
            docs.add(new FaqDocument("FAQ", markdown.trim(), Collections.emptyList()));
        }
        return docs;
    }

    /**
     * 解析 CSV（标题,内容）的 FAQ。
     */
    public List<FaqDocument> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return Collections.emptyList();
        }
        List<FaqDocument> docs = new ArrayList<>();
        String[] lines = csv.split("\\R");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            int commaIndex = trimmed.indexOf(',');
            String title;
            String content;
            if (commaIndex < 0) {
                title = trimmed;
                content = "";
            } else {
                title = trimmed.substring(0, commaIndex).trim();
                content = trimmed.substring(commaIndex + 1).trim();
            }
            docs.add(new FaqDocument(title.isEmpty() ? "FAQ" : title, content, Collections.emptyList()));
        }
        return docs;
    }

    /**
     * 解析纯文本 FAQ。
     */
    public List<FaqDocument> parseText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return List.of(new FaqDocument("FAQ", text.trim(), Collections.emptyList()));
    }

    /**
     * 追加有效文档，忽略空标题与空内容。
     */
    private void appendDocIfPresent(List<FaqDocument> docs, String title, StringBuilder content) {
        if ((title == null || title.isBlank()) && content.isEmpty()) {
            return;
        }
        String safeTitle = (title == null || title.isBlank()) ? "FAQ" : title;
        String safeContent = content.toString().trim();
        docs.add(new FaqDocument(safeTitle, safeContent, Collections.emptyList()));
    }
}
