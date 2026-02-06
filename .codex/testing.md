日期：2026-02-03  
作者：Codex  

## 后端单元/冒烟测试

1) 健康检查  
命令：
```bash
mvn -q -f backend/pom.xml -Dtest=HealthControllerTest test
```
结果：PASS

2) AI 配置加载  
命令：
```bash
mvn -q -f backend/pom.xml -Dtest=AiConfigTest test
```
结果：PASS

3) 业务数据仓储冒烟  
命令：
```bash
mvn -q -f backend/pom.xml -Dtest=RepositorySmokeTest test
```
结果：PASS

4) FAQ 解析  
命令：
```bash
mvn -q -f backend/pom.xml -Dtest=FaqParserTest test
```
结果：PASS

5) 提示词拼装  
命令：
```bash
mvn -q -f backend/pom.xml -Dtest=PromptComposerTest test
```
结果：PASS

6) API 端点存在性  
命令：
```bash
mvn -q -f backend/pom.xml -Dtest=ChatControllerTest test
```
结果：PASS

## 前端构建

命令：
```bash
npm run build
```
结果：PASS

日期：2026-02-04  
作者：Codex  

## 全链路联测（本机 Ollama + Docker 依赖）

1) 依赖容器状态  
命令：
```bash
docker compose ps
```
结果：mysql/etcd/minio/milvus 运行中

2) Ollama 服务  
命令：
```bash
curl -s http://localhost:11434/api/tags
```
结果：返回本地模型列表（含 qwen2.5:7b、nomic-embed-text）

3) 启动后端（使用本机 IP 连接容器 MySQL）  
命令：
```bash
MYSQL_URL="jdbc:mysql://192.168.31.214:3306/intelligent_customer?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" MYSQL_USER=root MYSQL_PASSWORD=root ./mvnw spring-boot:run
```
结果：启动成功（日志：`/tmp/intelligent-backend.log`）

4) 健康检查  
命令：
```bash
curl -s http://localhost:8080/api/health
```
结果：OK

5) FAQ 导入  
命令：
```bash
python3 - <<'PY' | curl -s -X POST http://localhost:8080/api/faq/import -H "Content-Type: application/json" --data-binary @-
import json
content = """# 退款说明
Q: 退款多久到账？
A: 一般1-3个工作日到账，原路退回。

# 发票与报销
Q: 可以开具电子发票吗？
A: 可以，支付完成后在订单详情页下载。

# 物流与发货
Q: 什么时候发货？
A: 通常24小时内发货，节假日顺延。"""
print(json.dumps({"sourceType": "MARKDOWN", "content": content}, ensure_ascii=False))
PY
```
结果：`{"count":3}`

6) FAQ 搜索  
命令：
```bash
curl -s --get --data-urlencode "query=退款" "http://localhost:8080/api/faq/search"
```
结果：返回 3 条 FAQ 片段

7) 聊天 SSE  
命令：
```bash
curl -s -N -X POST http://localhost:8080/api/chat -H "Content-Type: application/json" --data-binary '{"question":"我想退款多久到账？"}'
```
结果：返回 `data:一般1-3个工作日到账，原路退回。`

日期：2026-02-05  
作者：Codex  

## MyBatis-Plus 迁移测试

1) MyBatis-Plus 读数冒烟（RED）  
命令：
```bash
./mvnw -q -Dtest=MybatisPlusSmokeTest test
```
结果：FAIL（预期，Mapper 类未实现导致 `ClassNotFoundException`）

2) MyBatis-Plus 读数冒烟（GREEN）  
命令：
```bash
./mvnw -q -Dtest=MybatisPlusSmokeTest clean test
```
结果：PASS

3) 后端全量测试  
命令：
```bash
./mvnw -q test
```
结果：PASS

4) MyBatis-Plus 写入与更新冒烟  
命令：
```bash
./mvnw -q -Dtest=MybatisPlusSmokeTest test
```
结果：PASS

## NL2SQL 业务查询测试

1) DSL/SQL/摘要链路测试  
命令：
```bash
./mvnw -q -Dtest=BusinessQueryDslParserTest,BusinessQuerySqlBuilderTest,BusinessQueryServiceTest test
```
结果：PASS

日期：2026-02-05  
作者：Codex  
