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

# DatabaseChatMemory 实现

这个项目实现了一个名为 `DatabaseChatMemory` 的类，它继承了 Spring AI 的 `ChatMemory` 接口，提供了将对话历史记录存储到 PostgreSQL 数据库的功能。

## 特点

1. **持久化支持**：将聊天记录存储到 PostgreSQL 数据库中，支持多轮对话。
2. **符合 Spring AI 规范**：实现了标准的 `ChatMemory` 接口，可以无缝集成到 Spring AI 项目中。
3. **查询灵活**：支持按对话 ID 检索最近 N 条消息，也可清理指定会话的全部记录。
4. **数据库自动初始化**：在项目启动时，自动检查并初始化所需的数据表。

## 数据库表结构

`DatabaseChatMemory` 使用以下表结构存储消息数据：


## 使用方法

以下是如何使用 `DatabaseChatMemory` 的简单示例：

### 1. 引入依赖

确保项目中已经添加了以下依赖：

- Spring Boot 数据库支持 (`spring-boot-starter-jdbc`)
- PostgreSQL JDBC 驱动

### 2. 初始化 ChatMemory

通过配置好的 `JdbcTemplate` 对象初始化 `DatabaseChatMemory`：

### 3. 添加消息

您可以向指定会话 ID 中添加新消息，单条或多条均可：
### 4. 检索消息

查询一个会话中最近的消息：
### 5. 清除历史记录

清除指定会话的所有消息

## 日志功能

`DatabaseChatMemory` 提供了详细的日志记录，默认会输出以下信息：

1. 数据库表初始化状态
2. 每次添加消息的数量及对应会话 ID
3. 检索到的消息数量
4. 被清理的消息数量

## 环境要求

- **数据库**：PostgreSQL 9.6 或更高版本。
- **Java**：JDK 11 或更高。
- **Spring Boot**：2.6.0 或更高。
- **依赖库**：
    - Spring JDBC
    - PostgreSQL JDBC 驱动

## 注意事项

1. **安全**：为避免 SQL 注入问题，所有数据库操作均使用参数化查询。
2. **性能**：在高并发场景下，推荐对 `conversation_id` 字段添加索引，以提升查询性能。
3. **扩展**：可以进一步扩展表结构，例如添加用户信息、对话元数据等。

## 总结

`DatabaseChatMemory` 提供了在 PostgreSQL 中持久化存储聊天记录的强大能力，适用于需要长期存储和管理聊天对话记录的场景，同时保持与 Spring AI 的无缝集成。


# 安装 yt-dlp

以下是安装 yt-dlp 的详细步骤，适用于 Windows 用户：

## 步骤 1：下载 yt-dlp

1. 访问 yt-dlp 的官方 GitHub 仓库：[https://github.com/yt-dlp/yt-dlp](https://github.com/yt-dlp/yt-dlp)。
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