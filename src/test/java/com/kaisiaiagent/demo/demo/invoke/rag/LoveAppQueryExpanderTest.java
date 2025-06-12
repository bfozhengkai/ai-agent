package com.kaisiaiagent.demo.demo.invoke.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.rag.Query;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveAppQueryExpanderTest {
    @Resource
    private LoveAppQueryExpander loveAppQueryExpander;

    @Test
    void expand() {
        List<Query> queries = loveAppQueryExpander.expand("我想让另一半更爱我, 但我不知道怎么做. heheheha hhahaha");
        assertEquals(4, queries.size());
    }
}