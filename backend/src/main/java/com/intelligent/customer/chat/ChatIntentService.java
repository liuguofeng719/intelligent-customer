package com.intelligent.customer.chat;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 意图识别服务，用于跨轮保持业务目标。
 */
@Service
public class ChatIntentService {

    /**
     * 识别当前问题的意图，并在不明确时回退历史意图。
     */
    public ChatIntent resolveIntent(ChatIntent currentIntent,
                                    String question,
                                    List<ChatHistoryItem> history) {
        if (question == null || question.isBlank()) {
            return fallbackIntent(currentIntent, history);
        }
        String normalized = question.trim();
        if (containsAny(normalized, "订单", "发货", "物流", "退款", "退货", "售后", "签收")) {
            return ChatIntent.ORDER;
        }
        if (containsAny(normalized, "产品", "商品", "价格", "库存", "型号", "规格")) {
            return ChatIntent.PRODUCT;
        }
        if (containsAny(normalized, "客户", "用户", "会员", "账号", "账户", "手机号", "联系方式")) {
            return ChatIntent.CUSTOMER;
        }
        return fallbackIntent(currentIntent, history);
    }

    private ChatIntent fallbackIntent(ChatIntent currentIntent, List<ChatHistoryItem> history) {
        if (currentIntent != null && currentIntent != ChatIntent.GENERAL) {
            return currentIntent;
        }
        if (history != null) {
            for (int i = history.size() - 1; i >= 0; i--) {
                ChatHistoryItem item = history.get(i);
                if (item == null || item.content() == null) {
                    continue;
                }
                String content = item.content();
                if (containsAny(content, "订单", "发货", "物流", "退款", "退货", "售后")) {
                    return ChatIntent.ORDER;
                }
                if (containsAny(content, "产品", "商品", "价格", "库存", "型号", "规格")) {
                    return ChatIntent.PRODUCT;
                }
                if (containsAny(content, "客户", "用户", "会员", "账号", "账户", "手机号", "联系方式")) {
                    return ChatIntent.CUSTOMER;
                }
            }
        }
        return ChatIntent.GENERAL;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
