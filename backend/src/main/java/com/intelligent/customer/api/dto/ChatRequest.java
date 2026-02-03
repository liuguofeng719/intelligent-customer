package com.intelligent.customer.api.dto;

import java.util.List;

public record ChatRequest(
        String question,
        String sessionId,
        List<String> history
) {
}
