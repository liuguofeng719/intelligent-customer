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
