日期：2026-02-03  
作者：Codex  

## 已执行验证

- `docker compose config`：通过  
- 后端单元/冒烟测试：已执行（见 `.codex/testing.md`）  
- 前端构建：已执行（见 `.codex/testing.md`）  

## 未执行项与原因

- 未执行完整链路（Ollama + Milvus + MySQL + 前后端联调）：需要本机启动容器与拉取模型，当前未自动启动以避免长时间占用资源。
  - 尝试启动 `docker compose up -d` 失败：Docker 使用的镜像源 `docker.nju.edu.cn` 超时。
  - 重新验证时镜像拉取仍受阻：`ollama/ollama:latest` 拉取超时（层 `11fd8b8660d7` 反复重试），`milvusdb/milvus:v2.6.9` 未拉取完成，导致无法启动依赖服务。

## 风险说明

- Milvus 与 Ollama 的实际连通性尚未在本次验证中覆盖，首次运行可能需检查端口与模型是否拉取完成。

日期：2026-02-04  
作者：Codex  

## 已执行验证（新增）

- `docker compose ps`：mysql/etcd/minio/milvus 运行中  
- Ollama 本地服务可用（`http://localhost:11434/api/tags` 返回模型）  
- 后端启动并完成接口联测：健康检查、FAQ 导入、FAQ 搜索、聊天 SSE（详见 `.codex/testing.md`）  

## 关键环境说明

- 本机存在 `mysqld` 占用 `127.0.0.1:3306`，默认数据源会连接到本机 MySQL 而非 Docker 容器。
- 本次联测通过将 `MYSQL_URL` 指向本机 IP `192.168.31.214:3306` 访问容器端口完成。

## 剩余风险

- `ollama/ollama:latest` Docker 镜像仍未验证（本次使用本机 Ollama 服务替代）。

日期：2026-02-05  
作者：Codex  

## 已执行验证（新增）

- 数据访问层迁移为 MyBatis-Plus 后，冒烟测试通过（见 `.codex/testing.md`）
- 后端全量测试通过（见 `.codex/testing.md`）
- MyBatis-Plus 写入与更新冒烟通过（见 `.codex/testing.md`）

## 风险说明

- 仅验证了基础 `selectById` 的读数能力，复杂查询需按业务场景补充测试。

日期：2026-02-05  
作者：Codex  

## 已执行验证（新增）

- NL2SQL 业务查询链路测试通过（DSL 解析、SQL 构建、摘要输出，见 `.codex/testing.md`）

## 风险说明

- NL2SQL 仍依赖模型输出质量，复杂指令可能需要进一步完善 DSL 约束与提示词。
