package com.kaisiaiagent.demo.chatmemory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FileBaseChatMemoryTest {

    @TempDir
    Path tempDir;

    private FileBaseChatMemory chatMemory;
    private String testDir;

    @BeforeEach
    void setup() {
        testDir = tempDir.toString();
        chatMemory = new FileBaseChatMemory(testDir);
    }

    @AfterEach
    void cleanup() throws Exception {
        // 清理临时目录中的所有文件
        if (Files.exists(tempDir)) {
            Files.walk(tempDir)
                 .filter(path -> !path.equals(tempDir))
                 .map(Path::toFile)
                 .forEach(File::delete);
        }
    }

    @Test
    void testAddAndGetMessages() {
        String conversationId = UUID.randomUUID().toString();
        UserMessage message1 = new UserMessage("你好");
        UserMessage message2 = new UserMessage("世界");

        chatMemory.add(conversationId, message1);
        chatMemory.add(conversationId, message2);

        List<Message> messages = chatMemory.get(conversationId, 10);
        assertEquals(2, messages.size());
        assertTrue(messages.get(0) instanceof UserMessage);
        assertTrue(messages.get(1) instanceof UserMessage);
        UserMessage retrievedMessage1 = (UserMessage) messages.get(0);
        UserMessage retrievedMessage2 = (UserMessage) messages.get(1);
        assertEquals("你好", retrievedMessage1.getText());
        assertEquals("世界", retrievedMessage2.getText());
    }

    @Test
    void testAddMultipleMessagesAtOnce() {
        String conversationId = UUID.randomUUID().toString();
        List<Message> messages = Arrays.asList(
                new UserMessage("消息1"),
                new UserMessage("消息2"),
                new UserMessage("消息3")
        );

        chatMemory.add(conversationId, messages);

        List<Message> retrievedMessages = chatMemory.get(conversationId, 10);
        assertEquals(3, retrievedMessages.size());
    }

    @Test
    void testGetLastNMessages() {
        String conversationId = UUID.randomUUID().toString();
        chatMemory.add(conversationId, new UserMessage("消息1"));
        chatMemory.add(conversationId, new UserMessage("消息2"));
        chatMemory.add(conversationId, new UserMessage("消息3"));
        chatMemory.add(conversationId, new UserMessage("消息4"));
        chatMemory.add(conversationId, new UserMessage("消息5"));

        List<Message> lastThreeMessages = chatMemory.get(conversationId, 3);
        assertEquals(3, lastThreeMessages.size());
        assertEquals("消息5", ((UserMessage) lastThreeMessages.get(2)).getText());
        assertEquals("消息4", ((UserMessage) lastThreeMessages.get(1)).getText());
        assertEquals("消息3", ((UserMessage) lastThreeMessages.get(0)).getText());
    }
    @Test
    void testClearMessages() {
        String conversationId = UUID.randomUUID().toString();
        chatMemory.add(conversationId, new UserMessage("测试消息"));

        List<Message> messagesBeforeClear = chatMemory.get(conversationId, 10);
        assertEquals(1, messagesBeforeClear.size());

        chatMemory.clear(conversationId);

        List<Message> messagesAfterClear = chatMemory.get(conversationId, 10);
        assertTrue(messagesAfterClear.isEmpty());

        // 验证文件是否被删除
        File filePath = new File(testDir, conversationId + ".kryo");
        assertFalse(filePath.exists());
    }

    @Test
    void testFileExists() {
        String conversationId = UUID.randomUUID().toString();
        chatMemory.add(conversationId, new UserMessage("测试消息"));

        File filePath = new File(testDir, conversationId + ".kryo");
        assertTrue(filePath.exists());
    }
}
