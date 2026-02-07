# Intelligent Customer（智能客服快速落地）

## 快速开始

1. 启动依赖服务
```bash
docker compose up -d
```

2. 拉取本地模型（首次需要）
```bash
ollama pull qwen2.5:7b
ollama pull nomic-embed-text
```

3. 启动后端
```bash
cd backend
./mvnw spring-boot:run
```

4. 启动前端
```bash
cd frontend
npm install
npm run dev
```

5. 访问页面  
`http://localhost:5173`

## 配置说明

后端配置（`backend/src/main/resources/application.yml`）：
- MySQL  
  - `MYSQL_URL`（默认：`jdbc:mysql://localhost:3306/intelligent_customer`）  
  - `MYSQL_USER`（默认：`root`）  
  - `MYSQL_PASSWORD`（默认：`root`）  
- MyBatis-Plus  
  - 默认开启下划线转驼峰（`map-underscore-to-camel-case: true`）  
- Ollama  
  - `ollama.base-url`（默认：`http://localhost:11434`）  
  - `ollama.chat-model`（默认：`qwen2.5:7b`）  
  - `ollama.embedding-model`（默认：`nomic-embed-text`）  
- Milvus  
  - `MILVUS_HOST`（默认：`localhost`）  
  - `MILVUS_PORT`（默认：`19530`）  
  - `MILVUS_COLLECTION`（默认：`faq_store`）  
  - `MILVUS_DIMENSION`（默认：`768`）

前端配置：
- `VITE_API_BASE`：后端地址前缀（默认空，等于当前域名）

## 接口示例

健康检查：
```bash
curl http://localhost:8080/api/health
```

导入 FAQ：
```bash
curl -X POST http://localhost:8080/api/faq/import \
  -H "Content-Type: application/json" \
  -d '{"sourceType":"MARKDOWN","content":"# 退款\n退款流程说明..."}'
```

检索 FAQ：
```bash
curl "http://localhost:8080/api/faq/search?query=退款"
```

聊天（SSE）：
```bash
curl -N -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question":"订单什么时候发货？"}'
```

## 注意事项

- 如果 Milvus 启动失败，可根据官方部署说明调整镜像版本或 docker-compose 参数。
- 如果 Ollama 未准备就绪，请确认模型已拉取完成。
