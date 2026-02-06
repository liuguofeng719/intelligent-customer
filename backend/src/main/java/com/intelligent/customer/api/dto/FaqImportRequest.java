package com.intelligent.customer.api.dto;

import com.intelligent.customer.faq.FaqSourceType;

/**
 * FAQ 导入请求。
 *
 * @param sourceType 内容格式
 * @param content    FAQ 原文
 */
public record FaqImportRequest(
        FaqSourceType sourceType,
        String content
) {
}
