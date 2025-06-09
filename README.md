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



# 安装 yt-dlp：

## 访问 yt-dlp 的官方 GitHub 仓库：https://github.com/yt-dlp/yt-dlp。
### 在发布页面（Releases）找到适用于您操作系统的最新可执行文件（对于 Windows，通常是 yt-dlp.exe）。
下载该文件并将其保存到一个您容易记住的目录，例如 C:\yt-dlp\。
将 yt-dlp 添加到系统 PATH 环境变量中（以 Windows 为例）：

在 Windows 搜索栏中输入“环境变量”，然后选择“编辑系统环境变量”。
在弹出的“系统属性”窗口中，点击“环境变量”按钮。
在“用户变量”或“系统变量”部分（推荐在“系统变量”中，这样所有用户都可以访问），找到名为 Path 的变量，然后点击“编辑”。
点击“新建”，然后输入您保存 yt-dlp.exe 的目录路径（例如 C:\yt-dlp\）。
点击“确定”关闭所有窗口。
验证安装：

打开一个新的命令提示符或 PowerShell 窗口（非常重要，因为旧窗口可能没有加载新的环境变量）。
输入 yt-dlp --version 并按回车。
如果安装成功，您应该会看到 yt-dlp 的版本信息。如果仍然显示“命令未找到”或类似错误，请仔细检查上述步骤，特别是路径是否正确，以及是否打开了新的命令行窗口。
完成这些步骤后，您的 Spring AI 应用程序应该就能找到并执行 yt-dlp 了。