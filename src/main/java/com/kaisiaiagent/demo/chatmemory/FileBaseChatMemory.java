package com.kaisiaiagent.demo.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBaseChatMemory implements ChatMemory {
    private final String BASE_PATH;

    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    public FileBaseChatMemory(String dir) {
        this.BASE_PATH = dir;
        File baseDir = new File(dir);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }

    @Override
    public void add(String conversationId, Message message) {
        this.add(conversationId, List.of(message));
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> existingMessages = getOrLoadConversation(conversationId);
        existingMessages.addAll(messages);
        saveConversation(conversationId, existingMessages);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<Message> messages = getOrLoadConversation(conversationId);
        if (lastN <= 0 || lastN >= messages.size()) {
            return messages;
        }
        return messages.subList(messages.size() - lastN, messages.size());
    }

    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            if (!file.delete()) {
                throw new RuntimeException("无法删除会话文件: " + file.getAbsolutePath());
            }
        }
    }


    /**
     * 读取会话文件，如果不存在则返回空列表
     * @param conversationId
     * @return
     */
    private List<Message> getOrLoadConversation(String conversationId) {
        File file = getConversationFile(conversationId);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (com.esotericsoftware.kryo.io.Input input = new com.esotericsoftware.kryo.io.Input(new java.io.FileInputStream(file))) {
            return kryo.readObject(input, ArrayList.class);
        } catch (java.io.IOException e) {
            throw new RuntimeException("无法读取会话文件: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * 每个会话单独保存
     * @param conversationId
     * @param messages
     */
    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        try (com.esotericsoftware.kryo.io.Output output = new com.esotericsoftware.kryo.io.Output(new java.io.FileOutputStream(file))) {
            kryo.writeObject(output, new java.util.ArrayList<>(messages));
            output.flush();
        } catch (java.io.IOException e) {
            throw new RuntimeException("无法保存会话到文件: " + file.getAbsolutePath(), e);
        }
    }

    private File getConversationFile(String conversationId) {
        return new File(BASE_PATH, conversationId + ".kryo");
    }
}
