# LoveApp 概述

## 项目简介

LoveApp 是一个基于 Spring Boot 和 Spring AI 构建的恋爱咨询应用，它利用大语言模型和检索增强生成（RAG）技术，为用户提供恋爱和关系方面的专业建议。无论用户是单身、恋爱中还是已婚，LoveApp 都能针对其特定情况提供个性化的建议和解决方案。

## 核心功能

### 1. 基础对话功能

LoveApp 支持与用户进行多轮对话，AI 会记住之前的对话内容，提供连贯的回复。系统通过扮演恋爱心理专家的角色，引导用户详述恋爱问题，并给出专业建议。

```java
String reply = loveApp.doChat("我想让另一半更爱我", chatId);
```

### 2. 恋爱报告功能

LoveApp 可以生成结构化的恋爱报告，包含标题和建议列表。这种格式化的输出使得建议更加清晰、易于理解和执行。

```java
LoveReport report = loveApp.doChatWithReport("我和女友经常吵架", chatId);
String title = report.title();
List<String> suggestions = report.suggestions();
```

### 3. 知识库增强对话

LoveApp 利用 RAG 技术，基于预先准备的恋爱知识库提供更专业、更有针对性的建议。知识库包含单身、恋爱和已婚三个阶段的常见问题和专业解答。

```java
String reply = loveApp.doChatWithRag("婚后关系不太亲密，怎么办？", chatId);
```

## 技术特点

### 1. 多种存储方式

LoveApp 支持多种聊天记忆存储方式：
- **文件存储**：使用 FileBaseChatMemory 将对话历史保存到文件系统
- **数据库存储**：使用 ChatDatabaseAdvisor 将对话内容保存到 PostgreSQL 数据库
- **内存存储**：使用 InMemoryChatMemory 在内存中临时保存对话历史

### 2. 模块化设计

LoveApp 采用模块化设计，各组件职责明确：
- **ChatMemory**：负责对话历史的存储和检索
- **Advisor**：负责增强 AI 的能力，如日志记录、数据库存储、知识库检索等
- **VectorStore**：负责存储和检索知识库文档的向量表示
- **DocumentLoader**：负责加载和处理知识库文档

### 3. 可扩展性

LoveApp 设计具有良好的可扩展性：
- 可以添加新的知识文档扩充知识库
- 可以自定义系统提示词调整 AI 的角色和行为
- 可以添加新的 Advisor 增强 AI 的能力
- 可以实现新的 ChatMemory 支持更多存储方式

## 使用场景

### 1. 单身人士

- 提升个人魅力和吸引力
- 在社交场合主动结识心仪对象
- 线上交友技巧和注意事项
- 克服单身焦虑情绪

### 2. 恋爱中的人士

- 改善沟通方式，减少争吵
- 处理习惯差异和矛盾
- 增进感情和信任
- 解决长期关系中的倦怠问题

### 3. 已婚人士

- 维持婚后亲密关系
- 处理家庭责任分配
- 改善与亲属的关系
- 解决婚姻生活中的常见问题

## 项目资源

- **使用指南**：[LoveApp使用指南.md](LoveApp使用指南.md) - 详细的使用说明和最佳实践
- **实现指南**：[LoveAppImplementationGuide.java](../java/com/kaisiaiagent/demo/example/LoveAppImplementationGuide.java) - 从零开始构建 LoveApp 的示例代码
- **知识库文档**：位于 `src/main/resources/documents/` 目录下的 Markdown 文件

## 快速开始

1. 注入 LoveApp 实例
   ```java
   @Resource
   private LoveApp loveApp;
   ```

2. 创建会话 ID
   ```java
   String chatId = UUID.randomUUID().toString();
   ```

3. 使用基础对话功能
   ```java
   String reply = loveApp.doChat("你好，我想请教一个恋爱问题", chatId);
   ```

4. 使用恋爱报告功能
   ```java
   LoveReport report = loveApp.doChatWithReport("我和女友经常吵架，怎么办？", chatId);
   ```

5. 使用知识库增强对话功能
   ```java
   String reply = loveApp.doChatWithRag("我是单身，如何提升自己的魅力？", chatId);
   ```

## 结语

LoveApp 通过结合 AI 技术和专业知识库，为用户提供个性化的恋爱咨询服务。无论您是开发者还是用户，都可以通过本文档了解 LoveApp 的功能和使用方法，帮助您更好地利用这一工具解决恋爱和关系问题。