package com.intelligent.customer.business;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 业务查询服务，负责调用 Vanna 生成 SQL 并输出摘要。
 */
@Service
public class BusinessQueryService {

    private final VannaGateway vannaClient;
    private final BusinessQueryExecutor executor;
    private final BusinessQuerySummaryBuilder summaryBuilder;

    public BusinessQueryService(VannaGateway vannaClient,
                                BusinessQueryExecutor executor,
                                BusinessQuerySummaryBuilder summaryBuilder) {
        this.vannaClient = vannaClient;
        this.executor = executor;
        this.summaryBuilder = summaryBuilder;
    }

    public BusinessQueryResult tryBuildSummary(String question) {
        // 参数校验：空问题直接返回空结果，避免无意义查询
        if (question == null || question.isBlank()) {
            return emptyResult();
        }
        try {
            // 第一步：调用 Vanna 生成 SQL
            VannaSqlResponse response = vannaClient.generateSql(question, buildSchema());
            if (response == null || response.sql() == null || response.sql().isBlank()) {
                return emptyResult();
            }
            // 第二步：执行 SQL 查询
            String sql = response.sql().trim();
            List<Map<String, Object>> rows = executor.query(sql);
            // 第三步：将查询结果汇总为业务摘要
            String summary = summaryBuilder.buildSummary(rows);
            if (summary.isBlank()) {
                return emptyResult();
            }
            return new BusinessQueryResult(summary, sql, rows);
        } catch (Exception ex) {
            // 任意异常都回退为空结果，保证对话链路稳定
            return emptyResult();
        }
    }

    private BusinessQueryResult emptyResult() {
        return new BusinessQueryResult("", "", List.of());
    }

    private String buildSchema() {
        return """
                tables:
                  - orders(id, customer_id, product_id, status, amount, created_at)
                  - customer(id, name, phone)
                  - product(id, name, price, stock)
                """;
    }
}
