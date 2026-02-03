package com.intelligent.customer.faq;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class FaqParser {

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

    public List<FaqDocument> parseText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return List.of(new FaqDocument("FAQ", text.trim(), Collections.emptyList()));
    }

    private void appendDocIfPresent(List<FaqDocument> docs, String title, StringBuilder content) {
        if ((title == null || title.isBlank()) && content.isEmpty()) {
            return;
        }
        String safeTitle = (title == null || title.isBlank()) ? "FAQ" : title;
        String safeContent = content.toString().trim();
        docs.add(new FaqDocument(safeTitle, safeContent, Collections.emptyList()));
    }
}
