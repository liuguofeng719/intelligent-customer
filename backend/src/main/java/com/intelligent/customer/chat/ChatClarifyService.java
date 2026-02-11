package com.intelligent.customer.chat;

import com.intelligent.customer.config.ChatClarifyProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 澄清追问服务，用于识别歧义问题并生成反问。
 */
@Service
public class ChatClarifyService {

    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d{3,}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{11}");

    private final ChatClarifyConfigService configService;

    public ChatClarifyService(ChatClarifyConfigService configService) {
        this.configService = configService;
    }

    /**
     * 根据问题与意图生成澄清追问。
     */
    public ChatClarifyResult buildClarifyQuestion(String question,
                                                  ChatIntent intent,
                                                  List<ChatHistoryItem> history) {
        ChatClarifySettings settings = configService.getSettings();
        if (question == null || question.isBlank()) {
            return new ChatClarifyResult(settings.genericTemplate(), ChatClarifyReason.EMPTY);
        }
        if (question.trim().length() <= settings.shortQuestionLength()) {
            return new ChatClarifyResult(settings.genericTemplate(), ChatClarifyReason.SHORT);
        }
        if (settings.enablePronounCheck() && containsPronoun(question) && !hasIdentifier(question, history)) {
            return new ChatClarifyResult(settings.pronounTemplate(), ChatClarifyReason.PRONOUN);
        }
        if (intent == ChatIntent.ORDER && !hasIdentifier(question, history)) {
            return new ChatClarifyResult(settings.orderTemplate(), ChatClarifyReason.ORDER);
        }
        if (intent == ChatIntent.PRODUCT && !hasIdentifier(question, history)) {
            return new ChatClarifyResult(settings.productTemplate(), ChatClarifyReason.PRODUCT);
        }
        if (intent == ChatIntent.CUSTOMER && !hasIdentifier(question, history)) {
            return new ChatClarifyResult(settings.customerTemplate(), ChatClarifyReason.CUSTOMER);
        }
        if (intent == ChatIntent.GENERAL && question.length() < settings.minQuestionLength()) {
            return new ChatClarifyResult(settings.genericTemplate(), ChatClarifyReason.GENERAL);
        }
        return null;
    }

    private boolean hasIdentifier(String question, List<ChatHistoryItem> history) {
        if (question != null) {
            if (DIGIT_PATTERN.matcher(question).find() || PHONE_PATTERN.matcher(question).find()) {
                return true;
            }
        }
        if (history != null) {
            for (ChatHistoryItem item : history) {
                if (item == null || item.content() == null) {
                    continue;
                }
                if (DIGIT_PATTERN.matcher(item.content()).find()
                        || PHONE_PATTERN.matcher(item.content()).find()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsPronoun(String question) {
        return question.contains("这个")
                || question.contains("那个")
                || question.contains("它")
                || question.contains("这单")
                || question.contains("那单")
                || question.contains("上次");
    }
}
