package com.kaisiaiagent.demo.app;

import com.kaisiaiagent.demo.advisor.MyLoggerAdvisor;
import com.kaisiaiagent.demo.chatmemory.DatabaseChatMemory;
import com.kaisiaiagent.demo.chatmemory.FileBaseChatMemory;
import com.kaisiaiagent.demo.model.LoveReport;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {

    private ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 创建一个新的JdbcTemplate，使用指定的URL、用户名和密码
     * @param url 数据库URL
     * @param username 用户名
     * @param password 密码
     * @return 配置好的JdbcTemplate
     */
    private JdbcTemplate createJdbcTemplate(String url, String username, String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return new JdbcTemplate(dataSource);
    }

    @PostConstruct
    public void init() {
        //初始化基于文件的聊天
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        //ChatMemory chatMemory = new FileBaseChatMemory(fileDir);
        //初始化基于内存的聊天
        //ChatMemory chatMemory = new InMemoryChatMemory();
        //基於database的聊天
        ChatMemory chatMemory = new DatabaseChatMemory(createJdbcTemplate(databaseUrl, databaseUsername, databasePassword));
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        // 自定义 advisor: 日志输出
                        new MyLoggerAdvisor()
                        // 自定义 advisor: 数据库存储对话)
                        // 自定义 advisor: 推理增强
                )
                .build();
        log.info("LoveApp initialized with database URL: {}", databaseUrl);
    }

    /**
     * AI 基础对话(支持多轮对话)
     * @param message
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("chat content: {}", content);
        return content;
    }

    /**
     * AI 恋爱报告功能 (结构化输出)
     * @param message
     * @param chatId
     * @return contect
     */
    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system( SYSTEM_PROMPT + "每次对话后都要生成恋爱结果,标题为{用户名}的恋爱报告, 内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }

    @Resource
    private VectorStore loveAppVectorStore;

//    @Resource
//    private Advisor loveAppRagCloudAdvisor;

//    @Resource
//    private VectorStore pgVectorVectorStore;


    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())
                //基于 RAG 知識庫
                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))

                //阿里云存儲
                //.advisors(loveAppRagCloudAdvisor)

//                //阿里postgre sql對象存儲
//                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("chat content: {}", content);
        return content;
    }

}
