# AI Agent - Spring AI Chat Memory Implementation

[![Java CI with Maven](https://github.com/bfozhengkai/ai-agent/actions/workflows/ci.yml/badge.svg)](https://github.com/bfozhengkai/ai-agent/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)

一个基于 Spring AI 的聊天记忆实现项目，提供文件和数据库两种持久化方式。

## 📋 目录

- [特点](#特点)
- [快速开始](#快速开始)
- [使用方法](#使用方法)
- [依赖项](#依赖项)
- [贡献](#贡献)
- [许可证](#许可证)

## ✨ 特点

本项目包含多个 ChatMemory 实现和 AI 相关功能：

### 1. 文件持久化 ChatMemory (FileChatMemory)

`FileChatMemory` 类实现了 Spring AI 的 `ChatMemory` 接口，用于将聊天消息持久化到文件系统中。

**特点：**
- 符合 Spring AI 的 `ChatMemory` 接口规范
- 将对话消息保存为 JSON 文件
- 支持多个独立的对话会话
- 提供检索最近 N 条消息的功能
- 支持清除特定对话的历史记录

### 2. 数据库持久化 ChatMemory (DatabaseChatMemory)

`DatabaseChatMemory` 类继承了 Spring AI 的 `ChatMemory` 接口，提供了将对话历史记录存储到 PostgreSQL 数据库的功能。

**特点：**
- 持久化支持：将聊天记录存储到 PostgreSQL 数据库中
- 符合 Spring AI 规范：实现了标准的 `ChatMemory` 接口
- 查询灵活：支持按对话 ID 检索最近 N 条消息
- 数据库自动初始化：在项目启动时自动检查并初始化所需的数据表

### 3. 其他功能

- YouTube 视频内容文档读取器
- RAG (Retrieval-Augmented Generation) 支持
- PGVector 向量存储集成
- 多种 AI 模型集成（Google GenAI, Alibaba DashScope）

## 🚀 快速开始

### 前置要求

- Java 17 或更高版本
- Maven 3.9+
- PostgreSQL 数据库（用于 DatabaseChatMemory）

### 安装步骤

1. 克隆仓库：
```bash
git clone https://github.com/bfozhengkai/ai-agent.git
cd ai-agent
```

2. 安装依赖：
```bash
./mvnw clean install
```

3. 配置数据库（如果使用 DatabaseChatMemory）：
   - 修改 `src/main/resources/application.yml` 中的数据库配置
   - 确保 PostgreSQL 数据库正在运行

4. 运行应用：
```bash
./mvnw spring-boot:run
```

5. 访问 API 文档：
   - Swagger UI: http://localhost:8123/api/swagger-ui.html
   - Knife4j UI: http://localhost:8123/api/doc.html

## 📖 使用方法

### FileChatMemory 使用示例

### FileChatMemory 使用示例

#### 创建 FileChatMemory 实例

```java
// 使用默认目录 (临时目录下的 chat-memory 文件夹)
ChatMemory memory = new FileChatMemory();

// 或指定自定义目录
ChatMemory memory = new FileChatMemory("/path/to/your/directory");
```

### 添加消息

```java
String conversationId = "user123";
UserMessage message = new UserMessage("你好，AI助手！");
memory.add(conversationId, message);

// 或者一次添加多条消息
List<Message> messages = Arrays.asList(
    new UserMessage("第一条消息"),
    new SystemMessage("第二条消息")
);
memory.add(conversationId, messages);
```

### 获取消息

```java
// 获取某个对话的所有消息
List<Message> allMessages = memory.get(conversationId, Integer.MAX_VALUE);

// 获取最近的 5 条消息
List<Message> recentMessages = memory.get(conversationId, 5);
```

### 清除消息

```java
// 清除特定对话的所有消息
memory.clear(conversationId);
```

#### 存储格式

消息将以 JSON 格式存储在文件系统中，每个对话 ID 对应一个独立的 JSON 文件。文件名为 `{conversationId}.json`。

### DatabaseChatMemory 使用示例

详细的使用说明请参考项目源代码中的示例。

## 📦 依赖项

- Spring Boot 3.5.0
- Spring AI 框架
- Jackson (JSON 序列化/反序列化)
- Lombok (代码简化)
- PostgreSQL JDBC 驱动
- Google GenAI SDK
- Alibaba DashScope SDK
- PGVector (向量存储)
- yt-dlp Java 包装器

完整依赖列表请参阅 `pom.xml`。

## ⚙️ 环境要求

- **Java**: JDK 17 或 21
- **Maven**: 3.9+
- **数据库**: PostgreSQL 9.6 或更高版本
- **Spring Boot**: 3.5.0

## 🔒 安全注意事项

1. **安全**: 所有数据库操作均使用参数化查询以避免 SQL 注入
2. **性能**: 在高并发场景下，推荐对 `conversation_id` 字段添加索引
3. **扩展**: 可以进一步扩展表结构，例如添加用户信息、对话元数据等
4. **API 密钥**: 永远不要将 API 密钥提交到版本控制系统

## 📚 附加安装指南

### 安装 yt-dlp

以下是安装 yt-dlp 的详细步骤，适用于 Windows 用户：

#### 步骤 1：下载 yt-dlp
2. 在页面的 **Releases** （发布版本）部分，找到适用于您操作系统的最新可执行文件。例如，对于 Windows，通常文件名为 `yt-dlp.exe`。
3. 将 `yt-dlp.exe` 下载并保存至一个易于访问的目录，例如：`C:\yt-dlp\`。

---

## 步骤 2：将 yt-dlp 添加到系统 PATH 环境变量

1. 在 Windows 搜索栏中输入 **“环境变量”**，点击 **“编辑系统环境变量”**。
2. 在弹出的 **“系统属性”** 窗口中，点击下方的 **“环境变量”** 按钮。
3. 在 **“系统变量”** 部分（推荐在此处，这样可供所有用户使用），找到名为 `Path` 的变量，选中后点击 **“编辑”**。
4. 在弹出的窗口中点击 **“新建”**，然后输入您保存 `yt-dlp.exe` 的路径，例如：`C:\yt-dlp\`。
5. 确定修改，保存并关闭窗口。

---

## 步骤 3：验证安装

1. 打开一个 **新的命令提示符** 或 **PowerShell 窗口**（非常重要，旧窗口无法加载新环境变量）。
2. 输入以下命令并按回车：
   ```shell
   yt-dlp --version
   ```
3. 如果安装成功，您将会看到 yt-dlp 的版本号输出。如果显示 **“命令未找到”** 或其他错误信息：
   - 确保前述步骤操作正确。
   - 检查 `yt-dlp.exe` 是否被正确保存到指定目录。
   - 确保打开的是新窗口加载 PATH 变量后的环境。

---

完成以上步骤后，您将成功安装并配置 yt-dlp。此工具已准备就绪，可用于您需要的应用场景。

## 🤝 贡献

欢迎贡献！请查看我们的 [贡献指南](CONTRIBUTING.md) 了解详情。

在参与贡献之前，请阅读我们的 [行为准则](CODE_OF_CONDUCT.md)。

## 📄 许可证

本项目基于 MIT 许可证开源 - 详见 [LICENSE](LICENSE) 文件。

## 🔗 相关链接

- [Spring AI 文档](https://docs.spring.io/spring-ai/reference/)
- [Spring Boot 文档](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [问题追踪](https://github.com/bfozhengkai/ai-agent/issues)
- [安全政策](SECURITY.md)

## 📧 联系方式

如有问题或建议，请通过 GitHub Issues 联系我们。

---

**注意**: 请确保不要在代码或配置文件中提交敏感信息（如 API 密钥、数据库密码等）。使用环境变量或安全的配置管理方案来处理敏感数据。
