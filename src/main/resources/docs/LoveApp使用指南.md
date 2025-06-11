# LoveApp 使用指南

## 什么是 LoveApp

LoveApp 是一个基于 AI 的恋爱咨询应用，它利用大语言模型和检索增强生成（RAG）技术，为用户提供恋爱和关系方面的建议。LoveApp 可以处理单身、恋爱中和已婚人士的各种关系问题，并提供个性化的解决方案。

## LoveApp 的核心功能

LoveApp 提供以下三种主要功能：

1. **基础对话功能**：支持多轮对话，AI 会记住之前的对话内容，提供连贯的回复。
2. **恋爱报告功能**：生成结构化的恋爱报告，包含标题和建议列表。
3. **知识库增强对话**：利用 RAG 技术，基于预先准备的恋爱知识库提供更专业、更有针对性的建议。

## 技术架构

LoveApp 基于 Spring Boot 和 Spring AI 构建，主要组件包括：

### 1. 聊天模型和客户端

- 使用 Spring AI 的 ChatModel 和 ChatClient 进行 AI 对话
- 系统提示词设置为恋爱心理专家角色

### 2. 聊天记忆管理

- 支持基于文件的聊天记忆 (FileBaseChatMemory)
- 支持基于数据库的聊天记忆 (ChatDatabaseAdvisor)
- 使用 MessageChatMemoryAdvisor 将聊天记忆集成到对话中

### 3. 知识库检索增强

- 使用 SimpleVectorStore 存储恋爱知识文档
- 通过 LoveAppDocumentLoader 加载 Markdown 格式的知识文档
- 使用 QuestionAnswerAdvisor 将知识库检索结果融入到 AI 回复中

### 4. 结构化输出

- 使用 LoveReport 记录类定义结构化输出格式
- 支持生成包含标题和建议列表的恋爱报告

## 如何使用 LoveApp

### 基础对话

```java
// 创建一个唯一的会话 ID
String chatId = UUID.randomUUID().toString();

// 发送用户消息并获取 AI 回复
String message = "你好，我是小明";
String reply = loveApp.doChat(message, chatId);

// 继续对话，AI 会记住之前的对话内容
message = "我想让我的女朋友更爱我，有什么建议？";
reply = loveApp.doChat(message, chatId);
```

### 生成恋爱报告

```java
// 创建一个唯一的会话 ID
String chatId = UUID.randomUUID().toString();

// 发送用户消息并获取结构化的恋爱报告
String message = "我和女朋友经常因为小事吵架，怎么改善我们的沟通？";
LoveReport report = loveApp.doChatWithReport(message, chatId);

// 使用报告内容
String title = report.title();
List<String> suggestions = report.suggestions();
```

### 知识库增强对话

```java
// 创建一个唯一的会话 ID
String chatId = UUID.randomUUID().toString();

// 发送用户消息并获取基于知识库的 AI 回复
String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
String reply = loveApp.doChatWithRag(message, chatId);
```

## 如何扩展 LoveApp

### 添加新的知识文档

1. 在 `src/main/resources/documents/` 目录下创建新的 Markdown 文档
2. 按照现有文档的格式组织内容，使用标题表示问题，正文提供详细回答
3. 重启应用后，新文档会自动被加载到知识库中

### 自定义系统提示词

修改 LoveApp.java 中的 SYSTEM_PROMPT 常量，可以调整 AI 的角色和行为：

```java
private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
        "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
        "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
        "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";
```

### 添加新的 Advisor

可以创建自定义的 Advisor 来增强 AI 的能力，例如：

```java
// 创建自定义 Advisor
public class EmotionAnalysisAdvisor implements CallAroundAdvisor {
    // 实现 Advisor 接口方法
}

// 在 LoveApp 中使用
chatClient = ChatClient.builder(dashscopeChatModel)
        .defaultSystem(SYSTEM_PROMPT)
        .defaultAdvisors(
                new MessageChatMemoryAdvisor(chatMemory),
                new MyLoggerAdvisor(),
                new ChatDatabaseAdvisor(jdbcTemplate),
                new EmotionAnalysisAdvisor() // 添加新的 Advisor
        )
        .build();
```

## 最佳实践

1. **始终使用唯一的会话 ID**：为每个用户会话创建唯一的 ID，确保对话上下文的正确管理
2. **合理设置记忆大小**：通过 CHAT_MEMORY_RETRIEVE_SIZE_KEY 参数控制检索的历史消息数量
3. **定期维护知识库**：更新和扩充知识文档，以提供更准确、更全面的建议
4. **监控日志输出**：使用 MyLoggerAdvisor 记录的日志来分析和改进系统性能

## 故障排除

1. **AI 回复不连贯**：检查会话 ID 是否正确传递，确保使用相同的 ID 进行多轮对话
2. **知识库检索失效**：确认 Markdown 文档格式正确，并且 SimpleVectorStore 正确配置
3. **数据库存储问题**：检查数据库连接配置和表结构是否正确

## 结论

LoveApp 是一个功能强大的恋爱咨询应用，通过 AI 技术和知识库增强，为用户提供专业的恋爱和关系建议。通过本指南，您可以了解 LoveApp 的核心功能、技术架构和使用方法，以及如何进一步扩展和优化应用。