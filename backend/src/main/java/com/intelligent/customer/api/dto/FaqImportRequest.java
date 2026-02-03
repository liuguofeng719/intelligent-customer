package com.intelligent.customer.api.dto;

import com.intelligent.customer.faq.FaqSourceType;

public record FaqImportRequest(
        FaqSourceType sourceType,
        String content
) {
}
