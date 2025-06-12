package com.kaisiaiagent.demo.demo.invoke.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 查询扩展器demo
 */
@Component
public class LoveAppQueryExpander {

    @Resource
    private ChatClient.Builder chatClientBuilder;

    public List<Query> expand(String query) {
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(chatClientBuilder)
                .numberOfQueries(3)
                .build();
        List<Query> queries = queryExpander.expand(new Query("什么是恋爱指南? 哈哈呵呵呵哈哈哈哈"));
        return queries;
    }
}
