package com.intelligent.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智能客服应用启动入口。
 * 用于加载 Spring Boot 自动配置并启动 Web 服务。
 */
@SpringBootApplication
public class IntelligentCustomerApplication {

	public static void main(String[] args) {
		// 启动应用，参数由运行环境传入
		SpringApplication.run(IntelligentCustomerApplication.class, args);
	}

}
