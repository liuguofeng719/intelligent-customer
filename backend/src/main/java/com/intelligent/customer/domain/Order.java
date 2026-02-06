package com.intelligent.customer.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体，存储交易信息。
 */
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    // 主键 ID
    private Long id;

    @TableField("customer_id")
    // 客户 ID（必填）
    private Long customerId;

    @TableField("product_id")
    // 产品 ID（必填）
    private Long productId;

    // 订单状态（必填）
    private String status;

    // 订单金额（必填）
    private BigDecimal amount;

    @TableField("created_at")
    // 创建时间（必填）
    private LocalDateTime createdAt;

    public Order() {
    }

    public Order(Long customerId, Long productId, String status, BigDecimal amount, LocalDateTime createdAt) {
        this.customerId = customerId;
        this.productId = productId;
        this.status = status;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
