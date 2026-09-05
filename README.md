# LangChain4j 志愿填报 AI 顾问

这是一个面向 Java 后端和 AI 应用开发学习的 Spring Boot 项目，核心目标是用 LangChain4j 搭建一个可运行的高考志愿填报顾问。项目包含流式对话、Redis 会话记忆、Redis 向量库 RAG、知识库文档解析、工具调用和简单前端联调页，适合作为简历中的 LangChain4j/RAG 学习实践项目展示。

## 项目亮点

- 基于 Spring Boot 3 和 LangChain4j 构建 AI 服务
- 接入兼容 OpenAI 协议的通义千问模型
- 支持流式输出，前端页面可实时展示回答
- 使用 Redis 持久化多轮会话记忆
- 使用 Redis 向量库保存和检索知识库内容
- 支持 PDF 与 Markdown 资料作为 RAG 知识来源
- 通过 LangChain4j Tool Calling 实现预约信息写入和查询
- 集成 Tavily Search，支持需要实时信息的问题检索

## 技术栈

- Java 17
- Spring Boot
- LangChain4j
- MyBatis
- MySQL
- Redis Stack
- Redis Vector Store
- Apache PDFBox
- Tavily Search API
- HTML/CSS/JavaScript

## 目录结构

```text
src/main/java/com/ssm/consultant
├── aiservices      # LangChain4j AI Service 接口
├── config          # 会话记忆、RAG 检索等配置
├── controller      # 对话接口
├── mapper          # MyBatis 数据访问层
├── pojo            # 预约实体
├── repository      # Redis 会话记忆实现
├── service         # 业务服务
└── tools           # LangChain4j 工具调用

src/main/resources
├── content         # PDF 知识库资料
├── markdown        # Markdown 知识库资料
├── static          # 前端联调页面
├── system.txt      # 系统提示词
└── application.yml # 应用配置
```

## 核心功能

### 1. 流式 AI 对话

后端暴露 `/chat` 接口，返回 `Flux<String>`，前端页面通过流式读取逐步展示模型回答。

### 2. Redis 会话记忆

`RedisChatMemoryStore` 实现了 LangChain4j 的 `ChatMemoryStore`，将多轮对话记录序列化后写入 Redis，并设置过期时间，便于演示“刷新后仍能记住上下文”的效果。

### 3. RAG 知识库检索

项目使用 LangChain4j 的文档加载、PDF 解析、文本切分、Embedding 和 Redis 向量库检索能力，让模型可以结合本地高校与专业资料回答问题。

### 4. 工具调用

项目注册了两个工具：

- `ReservationTool`：新增或查询志愿填报预约信息
- `WebSearchTool`：调用 Tavily Search 查询最新政策、招生动态等实时信息

## 本地运行

### 1. 准备环境

请先安装：

- JDK 17+
- Maven 3.8+
- Docker Desktop，或自行安装 MySQL 8+ 与 Redis Stack

Redis 向量检索需要 RediSearch 能力，建议直接使用 Redis Stack。

### 2. 启动中间件

项目提供了 `docker-compose.yml`，可一键启动 MySQL 和 Redis Stack：

```bash
docker compose up -d
```

默认 MySQL root 密码为 `langchain4j_demo`。如果你要使用自己的密码，请在启动 Docker 前设置 `MYSQL_PASSWORD` 环境变量。

### 3. 初始化数据库

Docker Compose 会自动执行数据库脚本：

```sql
docs/sql/init.sql
```

如果使用本机 MySQL，请手动执行该脚本。

### 4. 配置环境变量

参考 `.env.example` 准备以下变量：

```text
ALI_API_KEY=your_dashscope_api_key
TAVILY_API_KEY=your_tavily_api_key
MYSQL_URL=jdbc:mysql://localhost:3306/langchain4jstudy?serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
MYSQL_USERNAME=root
MYSQL_PASSWORD=your_mysql_password
AI_LOG_REQUESTS=false
AI_LOG_RESPONSES=false
```

`TAVILY_API_KEY` 可选；不配置时，联网搜索工具会返回未启用提示，不影响基础对话链路。

Windows PowerShell 示例：

```powershell
$env:ALI_API_KEY="your_dashscope_api_key"
$env:TAVILY_API_KEY="your_tavily_api_key"
$env:MYSQL_PASSWORD="langchain4j_demo"
```

### 5. 启动服务

```bash
./mvnw spring-boot:run
```

Windows 可以使用：

```bash
mvnw.cmd spring-boot:run
```

启动后访问：

```text
http://localhost:8080/
```

## 接口示例

```text
GET /chat?memoryId=demo-user&message=我是山东考生，580分，想学计算机，有哪些学校可以考虑？
```

## 公开发布注意事项

- 不要提交真实的 API Key、数据库密码或个人账号信息
- 本项目已使用环境变量读取敏感配置
- `.env`、`application-local.yml`、日志文件、本地缓存和构建产物已加入 `.gitignore`
- `target/`、`.djl/`、`.idea/` 是本地生成内容，不需要上传到 GitHub
- `src/main/resources/content` 和 `src/main/resources/markdown` 中的资料请确认来源允许公开再分发
- 如需展示运行效果，建议补充页面截图或录屏到 README

## 测试说明

当前测试类依赖本地 MySQL、Redis 和模型 API Key，已标记为手动集成测试。配置完整后可移除 `@Disabled` 注解再运行：

```bash
mvn test
```

## 简历描述参考

基于 Spring Boot 和 LangChain4j 实现高考志愿填报 AI 顾问，接入通义千问模型，支持流式对话、Redis 多轮记忆、Redis 向量库 RAG、本地 PDF/Markdown 知识库检索、Tavily 联网搜索和预约信息工具调用，完整实践了 Java 后端接入大模型应用的核心链路。
