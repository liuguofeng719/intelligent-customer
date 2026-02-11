package com.intelligent.customer.chat;

import com.intelligent.customer.config.ChatClarifyProperties;
import com.intelligent.customer.domain.ChatClarifyConfig;
import com.intelligent.customer.repo.ChatClarifyConfigMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 澄清追问配置服务，负责读取与更新配置。
 */
@Service
public class ChatClarifyConfigService {

    private static final long CONFIG_ID = 1L;

    private final ChatClarifyProperties defaults;
    private final ChatClarifyConfigMapper configMapper;

    public ChatClarifyConfigService(ChatClarifyProperties defaults,
                                    ChatClarifyConfigMapper configMapper) {
        this.defaults = defaults;
        this.configMapper = configMapper;
    }

    public ChatClarifySettings getSettings() {
        ChatClarifyConfig config = configMapper.selectById(CONFIG_ID);
        if (config == null) {
            return toSettings(defaults);
        }
        return mergeSettings(defaults, config);
    }

    /**
     * 更新澄清配置并持久化。
     */
    public ChatClarifySettings updateSettings(ChatClarifySettings settings) {
        ChatClarifySettings normalized = normalize(settings);
        ChatClarifyConfig config = toEntity(normalized);
        ChatClarifyConfig exist = configMapper.selectById(CONFIG_ID);
        if (exist == null) {
            configMapper.insert(config);
        } else {
            configMapper.updateById(config);
        }
        return normalized;
    }

    /**
     * 归一化配置，确保阈值与模板有默认值。
     */
    private ChatClarifySettings normalize(ChatClarifySettings settings) {
        if (settings == null) {
            return toSettings(defaults);
        }
        return new ChatClarifySettings(
                settings.shortQuestionLength() > 0 ? settings.shortQuestionLength() : defaults.shortQuestionLength(),
                settings.minQuestionLength() > 0 ? settings.minQuestionLength() : defaults.minQuestionLength(),
                settings.enablePronounCheck(),
                isBlank(settings.genericTemplate()) ? defaults.genericTemplate() : settings.genericTemplate(),
                isBlank(settings.pronounTemplate()) ? defaults.pronounTemplate() : settings.pronounTemplate(),
                isBlank(settings.orderTemplate()) ? defaults.orderTemplate() : settings.orderTemplate(),
                isBlank(settings.productTemplate()) ? defaults.productTemplate() : settings.productTemplate(),
                isBlank(settings.customerTemplate()) ? defaults.customerTemplate() : settings.customerTemplate()
        );
    }

    /**
     * 合并数据库配置与默认配置。
     */
    private ChatClarifySettings mergeSettings(ChatClarifyProperties defaults, ChatClarifyConfig config) {
        return new ChatClarifySettings(
                config.getShortQuestionLength() == null ? defaults.shortQuestionLength() : config.getShortQuestionLength(),
                config.getMinQuestionLength() == null ? defaults.minQuestionLength() : config.getMinQuestionLength(),
                config.getEnablePronounCheck() == null ? defaults.enablePronounCheck() : config.getEnablePronounCheck(),
                isBlank(config.getGenericTemplate()) ? defaults.genericTemplate() : config.getGenericTemplate(),
                isBlank(config.getPronounTemplate()) ? defaults.pronounTemplate() : config.getPronounTemplate(),
                isBlank(config.getOrderTemplate()) ? defaults.orderTemplate() : config.getOrderTemplate(),
                isBlank(config.getProductTemplate()) ? defaults.productTemplate() : config.getProductTemplate(),
                isBlank(config.getCustomerTemplate()) ? defaults.customerTemplate() : config.getCustomerTemplate()
        );
    }

    /**
     * 基于默认配置生成配置快照。
     */
    private ChatClarifySettings toSettings(ChatClarifyProperties defaults) {
        return new ChatClarifySettings(
                defaults.shortQuestionLength(),
                defaults.minQuestionLength(),
                defaults.enablePronounCheck(),
                defaults.genericTemplate(),
                defaults.pronounTemplate(),
                defaults.orderTemplate(),
                defaults.productTemplate(),
                defaults.customerTemplate()
        );
    }

    /**
     * 配置快照转换为实体。
     */
    private ChatClarifyConfig toEntity(ChatClarifySettings settings) {
        ChatClarifyConfig config = new ChatClarifyConfig();
        config.setId(CONFIG_ID);
        config.setShortQuestionLength(settings.shortQuestionLength());
        config.setMinQuestionLength(settings.minQuestionLength());
        config.setEnablePronounCheck(settings.enablePronounCheck());
        config.setGenericTemplate(settings.genericTemplate());
        config.setPronounTemplate(settings.pronounTemplate());
        config.setOrderTemplate(settings.orderTemplate());
        config.setProductTemplate(settings.productTemplate());
        config.setCustomerTemplate(settings.customerTemplate());
        config.setUpdatedAt(LocalDateTime.now());
        return config;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
