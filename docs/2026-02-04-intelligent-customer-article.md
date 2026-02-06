日期：2026-02-04  
作者：浓睡不消残酒  

“纸上得来终觉浅，绝知此事要躬行。”——陆游

# 智能客服实战：Spring Boot + LangChain4j + Ollama 全链路落地与避坑指南

## 封面文案
一套可本地复现的智能客服落地方案，JDK 17、Spring Boot 3.5、MyBatis-Plus、LangChain4j、Ollama、Milvus 组合，强调工程可维护与验证闭环。

## 摘要
- 可复现的智能客服方案，对话、FAQ、业务查询一体化  
- 组件组合：Spring Boot、MyBatis-Plus、LangChain4j、Ollama、Milvus、MySQL、Vue  
- 给出完整命令、关键代码与正误对照，降低首次落地风险  
- 文末列出企业化升级方向：并发、观测、成本控制  

## 环境与前置
- JDK 17  
- Spring Boot 3.5  
- MyBatis-Plus  
- LangChain4j  
- Ollama 本地模型服务  
- Milvus 与 MySQL，Docker Compose 运行  
- Node 18 及以上  

**关键依赖**
- Ollama 模型 `qwen2.5:7b` 用于对话  
- Ollama 模型 `nomic-embed-text` 用于向量  

## 流程总览图
```
flowchart TD
  A[前端 Vue] -->|REST/SSE| B[Spring Boot API]
  B --> C[FAQ 入库与检索]
  C -->|向量| D[Milvus]
  B --> E[业务查询]
  E --> F[MySQL]
  B --> G[Prompt 组装]
  G --> H[Ollama 对话模型]
  C --> I[Ollama 向量模型]
```

## 实战主体

### 1) 结论：最小闭环方案
**选择：** Spring Boot + LangChain4j + Ollama + Milvus + MySQL + Vue  
**原因：**  
- 本地可复现，依赖清晰，便于快速验证  
- LangChain4j 统一嵌入、检索与模型调用  
- Milvus 适合向量检索扩展  
- 业务事实与 FAQ 分离，便于治理  

### 2) 原理：FAQ 与业务事实驱动的检索增强
- FAQ 文档分段并向量化，写入 Milvus  
- 用户问题转向量后检索 FAQ 片段  
- 业务数据通过 NL2DSL + SQL 查询补充事实  
- Prompt 统一组装，交给 Ollama 生成回复  

### 3) 场景：企业落地三类问题
1) 标准政策类：退款到账、发票开具  
2) 业务查询类：订单状态、库存、客户信息  
3) 混合咨询类：订单异常与政策解释  

### 4) 边界：落地阶段需关注的限制
- 本地模型推理速度受硬件影响  
- FAQ 数据质量决定命中率  
- Milvus 维度需与嵌入模型一致  
- 生产环境需补充并发、审计、可观测性  
- 本机 MySQL 占用端口可能导致连接误用  

### 5) 优化与权衡
- 性能与质量：模型越大越准但更慢  
- 召回与精度：阈值过高会漏召回，过低会引入噪声  
- 成本与稳定性：本地模型稳定但耗资源，云模型可控但依赖网络  
- 复杂度：全容器化一致性强，运维复杂度也更高  

### 6) 正确示例

#### 6.1 启动依赖
```bash
docker compose up -d mysql etcd minio milvus
ollama pull qwen2.5:7b
ollama pull nomic-embed-text
```

#### 6.2 启动后端
```bash
MYSQL_URL="jdbc:mysql://<本机IP>:3306/intelligent_customer?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
MYSQL_USER=root MYSQL_PASSWORD=root \
./mvnw spring-boot:run
```

#### 6.3 FAQ 导入与检索
```bash
python3 - <<'PY' | curl -s -X POST http://localhost:8080/api/faq/import \
  -H "Content-Type: application/json" --data-binary @-
import json
content = """# 退款说明
Q: 退款多久到账？
A: 一般1-3个工作日到账，原路退回。"""
print(json.dumps({"sourceType":"MARKDOWN","content":content}, ensure_ascii=False))
PY

curl -s --get --data-urlencode "query=退款" \
  "http://localhost:8080/api/faq/search"
```

#### 6.4 对话接口 SSE
```bash
curl -s -N -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  --data-binary '{"question":"我想退款多久到账？"}'
```

### 6.5 核心依赖（pom 片段）
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
  <groupId>com.baomidou</groupId>
  <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
  <version>3.5.7</version>
</dependency>
<dependency>
  <groupId>dev.langchain4j</groupId>
  <artifactId>langchain4j</artifactId>
</dependency>
<dependency>
  <groupId>dev.langchain4j</groupId>
  <artifactId>langchain4j-ollama</artifactId>
</dependency>
<dependency>
  <groupId>dev.langchain4j</groupId>
  <artifactId>langchain4j-milvus</artifactId>
</dependency>
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <scope>runtime</scope>
</dependency>
```

### 6.6 关键代码

**对话链路：在 FAQ 之外注入业务摘要**
```java
@PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public SseEmitter chat(@RequestBody ChatRequest request) {
    SseEmitter emitter = new SseEmitter();
    CompletableFuture.runAsync(() -> {
        try {
            List<String> faqSnippets = faqSearchService.search(request.question(), 3);
            BusinessQueryResult queryResult = businessQueryService.tryBuildSummary(request.question());
            String summary = queryResult.hasSummary() ? queryResult.summary() : "";
            String answer = chatService.answer(request.question(), faqSnippets, summary);
            emitter.send(SseEmitter.event().data(answer));
            emitter.complete();
        } catch (Exception ex) {
            emitter.completeWithError(ex);
        }
    });
    return emitter;
}
```

**NL2DSL → SQL → 摘要**
```java
public BusinessQueryResult tryBuildSummary(String question) {
    if (question == null || question.isBlank()) {
        return emptyResult();
    }
    String prompt = promptBuilder.buildPrompt(question);
    String rawDsl = chatModel.chat(prompt);
    BusinessQueryDsl dsl = parser.parse(rawDsl);
    String error = validator.validate(dsl);
    if (error != null) {
        return emptyResult();
    }
    BusinessQuerySql sql = sqlBuilder.build(dsl);
    List<Map<String, Object>> rows = executor.query(dsl, sql);
    String summary = summaryBuilder.buildSummary(rows);
    return summary.isBlank() ? emptyResult() : new BusinessQueryResult(summary, dsl, sql, rows);
}
```

**SQL 构建占位符（与 MyBatis-Plus SqlRunner 对齐）**
```java
String placeholder = "{" + index++ + "}";
clauses.add(alias + "." + column + " " + op + " " + placeholder);
```

**SqlRunner 映射初始化**
```java
@PostConstruct
public void initSqlRunner() {
    SqlRunnerInjector injector = new SqlRunnerInjector();
    for (SqlSessionFactory factory : sqlSessionFactories) {
        injector.inject(factory.getConfiguration());
    }
}
```

**NL2DSL 提示词（关键字段白名单）**
```java
joiner.add("可用实体：orders, customer, product。");
joiner.add("字段白名单：");
joiner.add("- orders: id, customerId, productId, status, amount, createdAt");
joiner.add("- customer: id, name, phone");
joiner.add("- product: id, name, price, stock");
```

### 7) 错误示例

**错误 1：FAQ 导入使用 text/plain**
```bash
curl -X POST http://localhost:8080/api/faq/import -H "Content-Type: text/plain" ...
```
**原因：** 接口要求 JSON  
**修正：** `Content-Type: application/json`

**错误 2：MySQL 连接到本机服务**
```bash
MYSQL_URL=jdbc:mysql://localhost:3306/...
```
**原因：** 本机 mysqld 占用 3306  
**修正：** 使用本机 IP 或调整容器端口映射  

### 8) 基础验证命令
```bash
curl -s http://localhost:8080/api/health
```
**期望输出：**
```
OK
```

## 常见问答
**Q1：如何提升 FAQ 检索准确率？**  
A：清洗文本、调整分段策略、增加高质量语料。  

**Q2：可以替换成在线模型吗？**  
A：可以，LangChain4j 支持替换为云端模型，但需评估成本与延迟。  

**Q3：业务字段如何注入？**  
A：通过业务查询接口聚合，再拼接进入 Prompt。  

## 检查要点
- 结构完整：标题、摘要、前置、主体、示例、验证  
- 命令齐全：启动、导入、检索、对话  
- 版本明确：JDK、Spring Boot、模型与依赖  

## 参考
- Spring Boot 官方文档：https://spring.io/projects/spring-boot  
- LangChain4j 官方文档：https://docs.langchain4j.dev/  
- Ollama 官方文档：https://ollama.com/  
- Milvus 官方文档：https://milvus.io/docs  
