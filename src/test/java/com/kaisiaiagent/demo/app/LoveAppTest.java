package com.kaisiaiagent.demo.app;

import com.kaisiaiagent.demo.model.LoveReport;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;
    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();

        //第一轮
        String message = "你好，我是zks";
        String content = loveApp.doChat(message, chatId);
        //第二轮
        message = "我想让另一半, 她叫leetcode 更爱我";
        content = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(content);
        //第三轮
        message = "你好，问一下我的另一半叫什么名字,我刚刚跟你说过";
        content = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(content);
    }

    @Test
    void testDoChatWithReport() {
        String chatId = UUID.randomUUID().toString();

        //第一轮
        String message = "你好，我是zks, 我想让另一半更爱我, 但我不知道怎么做.";
        LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }

    @Test
    void testDoChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了, 但是婚后关系不太亲密, 怎么办?";
        String answer = loveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }
}
