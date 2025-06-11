package com.kaisiaiagent.demo.example;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * LoveApp 实现指南
 * 本类演示如何从零开始构建一个 LoveApp
 */
public class LoveAppImplementationGuide {

    /**
     * 第一步：定义系统提示词
     * 系统提示词决定了 AI 的角色和行为方式
     */
    private static final String SYSTEM_PROMPT = "扮演恋爱心理专家，提供专业的恋爱建议。";

    /**
     * 第二步：创建聊天记忆接口
     * 这里我们创建一个简单的内存聊天记忆实现
     */
    public static class SimpleChatMemory implements ChatMemory {
        private final List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();

        @Override
        public void add(String conversationId, org.springframework.ai.chat.messages.Message message) {
            messages.add(message);
        }

        @Override
        public void add(String conversationId, List<org.springframework.ai.chat.messages.Message> messages) {
            this.messages.addAll(messages);
        }

        @Override
        public List<org.springframework.ai.chat.messages.Message> get(String conversationId, int lastN) {
            if (lastN <= 0 || lastN >= messages.size()) {
                return new ArrayList<>(messages);
            }
            return new ArrayList<>(messages.subList(messages.size() - lastN, messages.size()));
        }

        @Override
        public void clear(String conversationId) {
            messages.clear();
        }
    }

    /**
     * 第三步：创建文档加载器
     * 用于加载知识库文档
     */
    public static class SimpleDocumentLoader {
        private final ResourcePatternResolver resourcePatternResolver;

        public SimpleDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
            this.resourcePatternResolver = resourcePatternResolver;
        }

        public List<Document> loadDocuments(String pattern) {
            List<Document> documents = new ArrayList<>();
            try {
                Resource[] resources = resourcePatternResolver.getResources(pattern);
                for (Resource resource : resources) {
                    MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                            .withHorizontalRuleCreateDocument(true)
                            .build();
                    MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                    documents.addAll(reader.get());
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load documents", e);
            }
            return documents;
        }
    }

    /**
     * 第四步：创建结构化输出模型
     * 用于生成恋爱报告
     */
    public static record SimpleLoveReport(String title, List<String> suggestions) {
    }

    /**
     * 第五步：创建 LoveApp 主类
     */
    @Component
    public static class SimpleLoveApp {
        private final ChatClient chatClient;
        private final VectorStore vectorStore;

        public SimpleLoveApp(ChatModel chatModel, EmbeddingModel embeddingModel, 
                             ResourcePatternResolver resourcePatternResolver) {
            // 创建聊天记忆
            ChatMemory chatMemory = new InMemoryChatMemory();
            
            // 创建向量存储并加载文档
            SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
            SimpleDocumentLoader documentLoader = new SimpleDocumentLoader(resourcePatternResolver);
            List<Document> documents = documentLoader.loadDocuments("classpath:documents/*.md");
            simpleVectorStore.add(documents);
            this.vectorStore = simpleVectorStore;
            
            // 创建聊天客户端
            this.chatClient = ChatClient.builder(chatModel)
                    .defaultSystem(SYSTEM_PROMPT)
                    .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                    .build();
        }

        /**
         * 基础对话功能
         */
        public String chat(String message, String chatId) {
            ChatResponse response = chatClient.prompt()
                    .user(message)
                    .advisors(spec -> spec
                            .param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                            .param(MessageChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                    .call()
                    .chatResponse();
            return response.getResult().getOutput().getText();
        }

        /**
         * 结构化报告功能
         */
        public SimpleLoveReport chatWithReport(String message, String chatId) {
            return chatClient.prompt()
                    .system(SYSTEM_PROMPT + " 生成标题为{用户名}的恋爱报告，内容为建议列表。")
                    .user(message)
                    .advisors(spec -> spec
                            .param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                            .param(MessageChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                    .call()
                    .entity(SimpleLoveReport.class);
        }

        /**
         * 知识库增强对话功能
         */
        public String chatWithRag(String message, String chatId) {
            ChatResponse response = chatClient.prompt()
                    .user(message)
                    .advisors(spec -> spec
                            .param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                            .param(MessageChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                    .advisors(new QuestionAnswerAdvisor(vectorStore))
                    .call()
                    .chatResponse();
            return response.getResult().getOutput().getText();
        }
    }

    /**
     * 第六步：使用示例
     */
    public static void usageExample() {
        // 这里仅作为示例，实际使用时需要通过依赖注入获取实例
        // SimpleLoveApp loveApp = ...;
        
        // 创建会话ID
        String chatId = UUID.randomUUID().toString();
        
        // 基础对话
        // String reply = loveApp.chat("你好，我想请教一个恋爱问题", chatId);
        
        // 生成报告
        // SimpleLoveReport report = loveApp.chatWithReport("我和女友经常吵架，怎么办？", chatId);
        
        // 知识库增强对话
        // String ragReply = loveApp.chatWithRag("我是单身，如何提升自己的魅力？", chatId);
    }
}