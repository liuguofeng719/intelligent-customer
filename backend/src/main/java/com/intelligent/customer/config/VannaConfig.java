package com.intelligent.customer.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Vanna 客户端配置。
 */
@Configuration
@EnableConfigurationProperties(VannaProperties.class)
public class VannaConfig {

    @Bean
    public RestTemplate vannaRestTemplate(RestTemplateBuilder builder, VannaProperties properties) {
        return builder
                .rootUri(properties.baseUrl())
                .setConnectTimeout(properties.timeout())
                .setReadTimeout(properties.timeout())
                .build();
    }
}
