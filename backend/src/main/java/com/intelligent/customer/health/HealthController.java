package com.intelligent.customer.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口，用于冒烟验证。
 */
@RestController
@RequestMapping("/api")
public class HealthController {
    @GetMapping("/health")
    public String health() {
        // 基础健康检查，便于冒烟验证
        return "OK";
    }
}
