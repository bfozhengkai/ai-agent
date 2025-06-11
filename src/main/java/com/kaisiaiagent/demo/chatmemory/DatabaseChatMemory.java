package com.kaisiaiagent.demo.chatmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 自定义 ChatMemory: 将对话内容保存到PostgreSQL数据库
 */
@Slf4j
@Component
public class DatabaseChatMemory implements ChatMemory {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseChatMemory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            // 创建存储对话内容的表
            jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS chat_conversations (" +
                "id VARCHAR(255) PRIMARY KEY, " +
                "conversation_id VARCHAR(255), " +
                "message_type VARCHAR(50), " +
                "content TEXT, " +
                "created_at TIMESTAMP" +
                ")"
            );
            log.info("Chat conversations table initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize database: {}", e.getMessage(), e);
        }
    }

    @Override
    public void add(String conversationId, Message message) {
        this.add(conversationId, List.of(message));
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        for (Message message : messages) {
            String messageType = message.getMessageType().toString();
            String content = extractContent(message);
            saveMessage(conversationId, messageType, content);
        }
        log.info("Added {} messages to conversation {}", messages.size(), conversationId);
    }

    private String extractContent(Message message) {
        if (message instanceof org.springframework.ai.chat.messages.UserMessage) {
            return ((org.springframework.ai.chat.messages.UserMessage) message).getText();
        } else if (message instanceof org.springframework.ai.chat.messages.SystemMessage) {
            return ((org.springframework.ai.chat.messages.SystemMessage) message).getText();
        } else if (message instanceof org.springframework.ai.chat.messages.AssistantMessage) {
            return ((org.springframework.ai.chat.messages.AssistantMessage) message).getText();
        } else {
            log.warn("Unknown message type: {}", message.getClass().getName());
            return message.toString();
        }
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<Message> messages = getMessagesFromDatabase(conversationId);

        if (lastN <= 0 || lastN >= messages.size()) {
            return messages;
        }

        return messages.subList(messages.size() - lastN, messages.size());
    }

    @Override
    public void clear(String conversationId) {
        try {
            int deletedCount = jdbcTemplate.update(
                "DELETE FROM chat_conversations WHERE conversation_id = ?",
                conversationId
            );
            log.info("Cleared {} messages from conversation {}", deletedCount, conversationId);
        } catch (Exception e) {
            log.error("Failed to clear messages for conversation {}: {}", conversationId, e.getMessage(), e);
            throw new RuntimeException("Failed to clear messages for conversation: " + conversationId, e);
        }
    }

    private List<Message> getMessagesFromDatabase(String conversationId) {
        try {
            List<MessageData> messageDataList = jdbcTemplate.query(
                "SELECT message_type, content FROM chat_conversations WHERE conversation_id = ? ORDER BY created_at ASC",
                new MessageDataRowMapper(),
                conversationId
            );

            List<Message> messages = new ArrayList<>();
            for (MessageData data : messageDataList) {
                messages.add(MessageConverter.toMessage(data.messageType, data.content));
            }

            return messages;
        } catch (Exception e) {
            log.error("Failed to retrieve messages for conversation {}: {}", conversationId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private void saveMessage(String conversationId, String messageType, String content) {
        try {
            String id = UUID.randomUUID().toString();
            LocalDateTime now = LocalDateTime.now();

            jdbcTemplate.update(
                "INSERT INTO chat_conversations (id, conversation_id, message_type, content, created_at) VALUES (?, ?, ?, ?, ?)",
                id, conversationId, messageType, content, now
            );

            log.info("Message saved to database successfully. ID: {}", id);
        } catch (Exception e) {
            log.error("Failed to save message to database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save message to database", e);
        }
    }

    @Override
    public String toString() {
        return DatabaseChatMemory.class.getSimpleName();
    }

    // Helper class for database row mapping
    private static class MessageData {
        String messageType;
        String content;

        MessageData(String messageType, String content) {
            this.messageType = messageType;
            this.content = content;
        }
    }

    // Row mapper for message data
    private static class MessageDataRowMapper implements RowMapper<MessageData> {
        @Override
        public MessageData mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MessageData(
                rs.getString("message_type"),
                rs.getString("content")
            );
        }
    }

    // Utility class to convert between Message and database representation
    private static class MessageConverter {
        public static Message toMessage(String messageType, String content) {
            return switch (messageType.toUpperCase()) {
                case "USER" -> new org.springframework.ai.chat.messages.UserMessage(content);
                case "SYSTEM" -> new org.springframework.ai.chat.messages.SystemMessage(content);
                case "ASSISTANT", "AI" -> new org.springframework.ai.chat.messages.AssistantMessage(content);
                default -> new org.springframework.ai.chat.messages.UserMessage(content);
            };
        }
    }
}
