package com.kaisiaiagent.demo.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/agent")
public class DeepSeekController{
    private final DashScopeChatModel chatModel;

    public DeepSeekController(DashScopeChatModel chatModel) {
        this.chatModel = chatModel;
    }


    @GetMapping("/{prompt}")
    public String chat(@PathVariable(value = "prompt") String prompt) {

        ChatResponse chatResponse = chatModel.call(new Prompt(prompt));

        if (!chatResponse.getResults().isEmpty()) {
            Map<String, Object> metadata = chatResponse.getResults()
                    .get(0)
                    .getOutput()
                    .getMetadata();

            System.out.println(metadata.get("reasoningContent"));
        }
        return chatResponse.getResult().getOutput().getText();
    }
}