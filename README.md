# 文件持久化 ChatMemory 实现

这个项目实现了一个 `FileChatMemory` 类，它实现了 Spring AI 的 `ChatMemory` 接口，用于将聊天消息持久化到文件系统中。

## 特点

- 符合 Spring AI 的 `ChatMemory` 接口规范
- 将对话消息保存为 JSON 文件
- 支持多个独立的对话会话
- 提供检索最近 N 条消息的功能
- 支持清除特定对话的历史记录

## 使用方法

### 创建 FileChatMemory 实例

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

## 存储格式

消息将以 JSON 格式存储在文件系统中，每个对话 ID 对应一个独立的 JSON 文件。文件名为 `{conversationId}.json`。

## 依赖项

- Spring AI 框架
- Jackson 用于 JSON 序列化/反序列化
- Lombok 用于简化代码
