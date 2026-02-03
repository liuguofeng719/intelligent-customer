# Intelligent Customer Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 在本地以 Spring Boot + LangChain4j + Ollama + MySQL + Milvus + Vue 落地“智能客服核心闭环”，并可通过 docker-compose 一键启动依赖服务。  

**Architecture:** 后端单体服务负责对话编排、FAQ 向量检索与业务数据查询，前端 Vue 独立工程提供聊天与管理界面。对话流为：意图识别 → 并行检索（FAQ + 业务）→ 提示词拼装 → Ollama 生成 → SSE 流式返回。  

**Tech Stack:** Java 17, Spring Boot, LangChain4j, Ollama, MySQL, Milvus, Vue 3, Vite, Docker Compose  

---

> 说明：当前未创建专用 worktree，执行阶段如需隔离，请先创建 worktree 后再开始。

### Task 1: 初始化工程骨架（后端 + 前端目录）

**Files:**
- Create: `backend/`（Spring Boot 项目）
- Create: `frontend/`（Vue 项目）

**Step 1: 创建后端工程**

Run（推荐 Spring Initializr）：  
`curl -o /tmp/intelligent-customer.zip "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=3.2.4&groupId=com.intelligent.customer&artifactId=intelligent-customer&name=intelligent-customer&packageName=com.intelligent.customer&javaVersion=17&dependencies=web,validation,data-jpa,mysql"`  
Expected: 下载成功，输出 zip 文件。

**Step 2: 解压并放入 backend/**

Run:  
`mkdir -p backend && unzip /tmp/intelligent-customer.zip -d backend`  
Expected: 生成 `backend/pom.xml` 与 `backend/src/`。

**Step 3: 创建前端工程**

Run:  
`npm create vue@latest frontend -- --default`  
Expected: 生成 `frontend/` 目录结构。

**Step 4: 提交**

Run:  
`git add backend frontend && git commit -m "chore: initialize backend and frontend skeleton"`  
Expected: 提交成功。

---

### Task 2: 后端基础配置与健康检查

**Files:**
- Modify: `backend/pom.xml`
- Create: `backend/src/main/resources/application.yml`
- Create: `backend/src/main/java/com/intelligent/customer/health/HealthController.java`
- Test: `backend/src/test/java/com/intelligent/customer/health/HealthControllerTest.java`

**Step 1: 写失败测试（健康检查）**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthControllerTest {
  @Autowired TestRestTemplate rest;
  @Test void health_ok() {
    ResponseEntity<String> resp = rest.getForEntity("/api/health", String.class);
    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
```

**Step 2: 运行测试（预期失败）**  
Run: `mvn -q -f backend/pom.xml -Dtest=HealthControllerTest test`  
Expected: FAIL（/api/health 未实现）。

**Step 3: 实现最小接口 + 基础配置**

- 新增 `HealthController`，返回 `OK` 文本  
- `application.yml` 设置 `server.port: 8080`  
- 注释需为中文

**Step 4: 运行测试（预期通过）**  
Run: `mvn -q -f backend/pom.xml -Dtest=HealthControllerTest test`  
Expected: PASS

**Step 5: 提交**  
Run: `git add backend && git commit -m "feat: add health endpoint"`  
Expected: 提交成功。

---

### Task 3: LangChain4j + Ollama + Milvus 基础接入

**Files:**
- Modify: `backend/pom.xml`
- Create: `backend/src/main/java/com/intelligent/customer/config/AiConfig.java`
- Create: `backend/src/test/java/com/intelligent/customer/config/AiConfigTest.java`

**Step 1: 写失败测试（Bean 可用）**

```java
@SpringBootTest
class AiConfigTest {
  @Autowired OllamaChatModel chatModel;
  @Autowired EmbeddingModel embeddingModel;
  @Test void beans_loaded() {
    assertThat(chatModel).isNotNull();
    assertThat(embeddingModel).isNotNull();
  }
}
```

**Step 2: 运行测试（预期失败）**  
Run: `mvn -q -f backend/pom.xml -Dtest=AiConfigTest test`  
Expected: FAIL（Bean 未配置）。

**Step 3: 实现最小配置**
- 增加 LangChain4j 相关依赖  
- `AiConfig` 中配置 Ollama Chat/Embedding Bean  
- 从 `application.yml` 读取 `ollama.base-url` 与 `ollama.model`  
- 注释使用中文

**Step 4: 运行测试（预期通过）**  
Run: `mvn -q -f backend/pom.xml -Dtest=AiConfigTest test`  
Expected: PASS

**Step 5: 提交**  
Run: `git add backend && git commit -m "feat: configure langchain4j ollama beans"`  
Expected: 提交成功。

---

### Task 4: 业务数据模型与样例数据

**Files:**
- Create: `backend/src/main/java/com/intelligent/customer/domain/Customer.java`
- Create: `backend/src/main/java/com/intelligent/customer/domain/Product.java`
- Create: `backend/src/main/java/com/intelligent/customer/domain/Order.java`
- Create: `backend/src/main/java/com/intelligent/customer/repo/*.java`
- Create: `backend/src/main/resources/schema.sql`
- Create: `backend/src/main/resources/data.sql`
- Test: `backend/src/test/java/com/intelligent/customer/repo/RepositorySmokeTest.java`

**Step 1: 写失败测试（仓储读取样例数据）**

```java
@SpringBootTest
class RepositorySmokeTest {
  @Autowired CustomerRepository customerRepo;
  @Test void load_seed_customer() {
    assertThat(customerRepo.findById(1L)).isPresent();
  }
}
```

**Step 2: 运行测试（预期失败）**  
Run: `mvn -q -f backend/pom.xml -Dtest=RepositorySmokeTest test`  
Expected: FAIL（数据表与样例数据未创建）。

**Step 3: 实现最小模型 + 初始化数据**
- JPA 实体与仓储接口  
- schema.sql / data.sql 写入最小样例  
- 注释使用中文

**Step 4: 运行测试（预期通过）**  
Run: `mvn -q -f backend/pom.xml -Dtest=RepositorySmokeTest test`  
Expected: PASS

**Step 5: 提交**  
Run: `git add backend && git commit -m "feat: add domain entities and seed data"`  
Expected: 提交成功。

---

### Task 5: FAQ 导入与向量化（Milvus）

**Files:**
- Create: `backend/src/main/java/com/intelligent/customer/faq/FaqDocument.java`
- Create: `backend/src/main/java/com/intelligent/customer/faq/FaqIngestService.java`
- Create: `backend/src/main/java/com/intelligent/customer/faq/FaqParser.java`
- Test: `backend/src/test/java/com/intelligent/customer/faq/FaqParserTest.java`

**Step 1: 写失败测试（解析与切分）**

```java
@Test
void parse_markdown_to_chunks() {
  String md = "# 退款\n退款流程说明...";
  List<FaqDocument> docs = parser.parseMarkdown(md);
  assertThat(docs).isNotEmpty();
}
```

**Step 2: 运行测试（预期失败）**  
Run: `mvn -q -f backend/pom.xml -Dtest=FaqParserTest test`  
Expected: FAIL

**Step 3: 实现最小解析 + 切分 + 向量化流程**
- Markdown/CSV 解析  
- 文本切分与 Embedding  
- 通过 Milvus 写入向量  
- 注释使用中文

**Step 4: 运行测试（预期通过）**  
Run: `mvn -q -f backend/pom.xml -Dtest=FaqParserTest test`  
Expected: PASS

**Step 5: 提交**  
Run: `git add backend && git commit -m "feat: add faq ingest and embeddings"`  
Expected: 提交成功。

---

### Task 6: RAG 检索与对话编排

**Files:**
- Create: `backend/src/main/java/com/intelligent/customer/chat/PromptComposer.java`
- Create: `backend/src/main/java/com/intelligent/customer/chat/ChatService.java`
- Test: `backend/src/test/java/com/intelligent/customer/chat/PromptComposerTest.java`

**Step 1: 写失败测试（提示词拼装）**

```java
@Test
void compose_prompt_contains_facts_and_faq() {
  String prompt = composer.compose("问题", List.of("FAQ"), "订单摘要");
  assertThat(prompt).contains("FAQ").contains("订单摘要");
}
```

**Step 2: 运行测试（预期失败）**  
Run: `mvn -q -f backend/pom.xml -Dtest=PromptComposerTest test`  
Expected: FAIL

**Step 3: 实现提示词拼装与 ChatService**
- PromptComposer 生成结构化提示词  
- ChatService 聚合 FAQ + 业务摘要 → 调用 Ollama  
- 注释使用中文

**Step 4: 运行测试（预期通过）**  
Run: `mvn -q -f backend/pom.xml -Dtest=PromptComposerTest test`  
Expected: PASS

**Step 5: 提交**  
Run: `git add backend && git commit -m "feat: add rag retrieval and chat orchestration"`  
Expected: 提交成功。

---

### Task 7: API 接口（SSE + FAQ + 业务查询）

**Files:**
- Create: `backend/src/main/java/com/intelligent/customer/api/ChatController.java`
- Create: `backend/src/main/java/com/intelligent/customer/api/FaqController.java`
- Create: `backend/src/main/java/com/intelligent/customer/api/BusinessController.java`
- Test: `backend/src/test/java/com/intelligent/customer/api/ChatControllerTest.java`

**Step 1: 写失败测试（聊天接口可访问）**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatControllerTest {
  @Autowired TestRestTemplate rest;
  @Test void chat_endpoint_exists() {
    ResponseEntity<String> resp = rest.postForEntity("/api/chat", Map.of("question", "hi"), String.class);
    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
```

**Step 2: 运行测试（预期失败）**  
Run: `mvn -q -f backend/pom.xml -Dtest=ChatControllerTest test`  
Expected: FAIL

**Step 3: 实现接口**
- `/api/chat` SSE 输出  
- `/api/faq/import` + `/api/faq/search`  
- `/api/orders/{id}`、`/api/products/{id}`、`/api/customers/{id}`  
- 注释使用中文

**Step 4: 运行测试（预期通过）**  
Run: `mvn -q -f backend/pom.xml -Dtest=ChatControllerTest test`  
Expected: PASS

**Step 5: 提交**  
Run: `git add backend && git commit -m "feat: add chat/faq/business apis"`  
Expected: 提交成功。

---

### Task 8: 前端 Vue 功能页（聊天 / FAQ / 数据）

**Files:**
- Modify: `frontend/src/router/index.js`
- Create: `frontend/src/views/ChatView.vue`
- Create: `frontend/src/views/FaqView.vue`
- Create: `frontend/src/views/BusinessView.vue`
- Create: `frontend/src/api/client.ts`

**Step 1: 写失败测试（前端路由）**

Run: `npm -C frontend run lint`  
Expected: FAIL（未配置路由与页面）。

**Step 2: 实现页面与 API 客户端**
- SSE 聊天流式展示  
- FAQ 导入与检索  
- 业务数据查询表格  
- 注释使用中文

**Step 3: 运行测试（预期通过）**  
Run: `npm -C frontend run lint`  
Expected: PASS

**Step 4: 提交**  
Run: `git add frontend && git commit -m "feat: add vue pages for chat and faq"`  
Expected: 提交成功。

---

### Task 9: docker-compose 与运行说明

**Files:**
- Create: `docker-compose.yml`
- Modify: `backend/src/main/resources/application.yml`
- Create: `README.md`

**Step 1: 写失败验证（compose 语法检查）**

Run: `docker compose config`  
Expected: FAIL（文件不存在）。

**Step 2: 实现 docker-compose**
- Ollama、MySQL、Milvus（含依赖）  
- 端口与环境变量统一  
- 注释使用中文

**Step 3: 运行验证（预期通过）**  
Run: `docker compose config`  
Expected: PASS

**Step 4: 提交**  
Run: `git add docker-compose.yml README.md backend/src/main/resources/application.yml && git commit -m "feat: add docker compose and runbook"`  
Expected: 提交成功。

---

### Task 10: 冒烟/功能测试脚本与记录

**Files:**
- Create: `.codex/testing.md`
- Create: `verification.md`

**Step 1: 编写本地测试步骤记录**
- 记录 3 条固定问答链路  
- 记录 FAQ 导入与检索  
- 记录订单/产品查询

**Step 2: 执行并记录输出**
- 运行 `curl` 调用接口并记录响应  

**Step 3: 提交**  
Run: `git add .codex/testing.md verification.md && git commit -m "test: add local verification records"`  
Expected: 提交成功。
