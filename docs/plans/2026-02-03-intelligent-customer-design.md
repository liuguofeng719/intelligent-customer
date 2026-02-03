# 智能客服快速落地设计（Spring Boot + LangChain4j + Ollama）

日期：2026-02-03  
作者：Codex  

## 目标与范围
- 目标：在本地环境快速落地“智能客服核心闭环”，实现对话 + FAQ 检索 + 业务数据查询 + 前后端分离演示。
- 范围内：Spring Boot 后端、Vue 前端、Ollama（qwen2.5:7b）、MySQL、Milvus、docker-compose 一键启动、最小样例数据与测试。
- 非范围：多渠道接入、工单系统、人工坐席协同、运营报表、质检、多语言、策略灰度。按要求不新增登录/权限等安全模块。

## 架构与数据流
前端 Vue 通过 REST + SSE 调用后端。后端编排流程为：意图识别 → 并行检索（FAQ 向量检索 + 业务数据查询）→ 组合上下文提示词 → Ollama 生成 → SSE 流式返回。  
FAQ 文档通过导入接口切分与向量化后写入 Milvus。业务事实来自 MySQL 查询结果摘要。

## 组件拆分
- 对话编排模块：负责意图识别、上下文拼装、LLM 调用。
- RAG 模块：FAQ 导入、切分、嵌入、检索。
- 业务数据模块：订单/产品/客户查询与摘要生成。
- 模型接入模块：Ollama Chat 与 Embedding 客户端。
- API 控制层：对外 REST + SSE 接口。

## 接口契约（核心）
- `POST /api/chat`：输入 `question`, `sessionId`, `history`（可选），SSE 输出流式回复。
- `POST /api/faq/import`：输入 `sourceType`（markdown/csv/text）与内容，返回导入数量。
- `GET /api/faq/search?query=`：检索验证接口（演示用）。
- 业务演示接口：`GET /api/orders/{id}`、`GET /api/products/{id}`、`GET /api/customers/{id}`。

## 数据模型（最小集）
- `faq_document(id, title, content, tags, created_at)`
- `customer(id, name, phone)`
- `product(id, name, price, stock)`
- `order(id, customer_id, product_id, status, amount, created_at)`

## 部署与依赖
使用 docker-compose 启动：Ollama、MySQL、Milvus（及其依赖）。后端与前端作为本地进程或可选容器运行。  
坚持标准化生态复用：Spring Boot + LangChain4j 官方依赖，Milvus 官方 SDK/客户端。

## 测试策略
仅覆盖核心闭环：
- 单元测试：提示词拼装、FAQ 切分与向量化、业务摘要生成。
- 冒烟测试：docker-compose 启动后固定 3 条问答链路。
- 功能测试：FAQ 导入→检索→对话链路；订单/产品查询链路。

## 假设与数据准备
业务域默认聚焦“订单/产品/售后”。FAQ 来源采用 Markdown/CSV 示例数据，随项目提供最小演示集。若后续需要替换为真实文档或数据集，可通过导入接口批量更新。
