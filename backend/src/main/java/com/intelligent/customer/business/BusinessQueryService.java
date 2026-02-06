package com.intelligent.customer.business;

import dev.langchain4j.model.chat.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 业务查询服务，负责 NL2DSL、SQL 构建与摘要输出。
 */
@Service
public class BusinessQueryService {

    private final ChatModel chatModel;
    private final BusinessQueryPromptBuilder promptBuilder;
    private final BusinessQueryDslParser parser;
    private final BusinessQueryDslValidator validator;
    private final BusinessQuerySqlBuilder sqlBuilder;
    private final BusinessQueryExecutor executor;
    private final BusinessQuerySummaryBuilder summaryBuilder;

    public BusinessQueryService(ChatModel chatModel,
                                BusinessQueryPromptBuilder promptBuilder,
                                BusinessQueryDslParser parser,
                                BusinessQueryDslValidator validator,
                                BusinessQuerySqlBuilder sqlBuilder,
                                BusinessQueryExecutor executor,
                                BusinessQuerySummaryBuilder summaryBuilder) {
        this.chatModel = chatModel;
        this.promptBuilder = promptBuilder;
        this.parser = parser;
        this.validator = validator;
        this.sqlBuilder = sqlBuilder;
        this.executor = executor;
        this.summaryBuilder = summaryBuilder;
    }

    public BusinessQueryResult tryBuildSummary(String question) {
        // 参数校验：空问题直接返回空结果，避免无意义查询
        if (question == null || question.isBlank()) {
            return emptyResult();
        }
        try {
            // 第一步：构建 NL2DSL 提示词并调用模型生成 DSL
            String prompt = promptBuilder.buildPrompt(question);
            String rawDsl = chatModel.chat(prompt);
            // 第二步：解析 DSL 并进行字段与操作符白名单校验
            BusinessQueryDsl dsl = parser.parse(rawDsl);
            String error = validator.validate(dsl);
            if (error != null) {
                return emptyResult();
            }
            // 第三步：构建 SQL 并执行查询
            BusinessQuerySql sql = sqlBuilder.build(dsl);
            List<Map<String, Object>> rows = executor.query(dsl, sql);
            // 第四步：将查询结果汇总为业务摘要
            String summary = summaryBuilder.buildSummary(rows);
            if (summary.isBlank()) {
                return emptyResult();
            }
            return new BusinessQueryResult(summary, dsl, sql, rows);
        } catch (Exception ex) {
            // 任意异常都回退为空结果，保证对话链路稳定
            return emptyResult();
        }
    }

    private BusinessQueryResult emptyResult() {
        return new BusinessQueryResult("", null, null, List.of());
    }
}
