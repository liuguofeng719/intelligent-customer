package com.intelligent.customer.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 澄清追问配置实体。
 */
@TableName("chat_clarify_config")
public class ChatClarifyConfig {

    @TableId(type = IdType.INPUT)
    private Long id;
    private Integer shortQuestionLength;
    private Integer minQuestionLength;
    private Boolean enablePronounCheck;
    private String genericTemplate;
    private String pronounTemplate;
    private String orderTemplate;
    private String productTemplate;
    private String customerTemplate;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getShortQuestionLength() {
        return shortQuestionLength;
    }

    public void setShortQuestionLength(Integer shortQuestionLength) {
        this.shortQuestionLength = shortQuestionLength;
    }

    public Integer getMinQuestionLength() {
        return minQuestionLength;
    }

    public void setMinQuestionLength(Integer minQuestionLength) {
        this.minQuestionLength = minQuestionLength;
    }

    public Boolean getEnablePronounCheck() {
        return enablePronounCheck;
    }

    public void setEnablePronounCheck(Boolean enablePronounCheck) {
        this.enablePronounCheck = enablePronounCheck;
    }

    public String getGenericTemplate() {
        return genericTemplate;
    }

    public void setGenericTemplate(String genericTemplate) {
        this.genericTemplate = genericTemplate;
    }

    public String getPronounTemplate() {
        return pronounTemplate;
    }

    public void setPronounTemplate(String pronounTemplate) {
        this.pronounTemplate = pronounTemplate;
    }

    public String getOrderTemplate() {
        return orderTemplate;
    }

    public void setOrderTemplate(String orderTemplate) {
        this.orderTemplate = orderTemplate;
    }

    public String getProductTemplate() {
        return productTemplate;
    }

    public void setProductTemplate(String productTemplate) {
        this.productTemplate = productTemplate;
    }

    public String getCustomerTemplate() {
        return customerTemplate;
    }

    public void setCustomerTemplate(String customerTemplate) {
        this.customerTemplate = customerTemplate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
