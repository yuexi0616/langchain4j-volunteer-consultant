package com.ssm.consultant.repository;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
public class RedisChatMemoryStore implements ChatMemoryStore {
    // 注入RedisTemplate
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        // 获取会话信息
        String json = redisTemplate.opsForValue().get(memoryId.toString());
        // 如果Redis中没有该memoryId的记录，返回空集合，避免NPE
        if (json == null || json.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        // 把json字符串转换成List<ChatMessage>，使用langchain4j提供的反序列
        List<ChatMessage> list = ChatMessageDeserializer.messagesFromJson(json);
        return list;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        // 更新会话消息
        // 1.把list数据转换成json
        // langchain4j提供了一个序列化的方法
        String json = ChatMessageSerializer.messagesToJson(list);
        // 2.把json存储到redis中
        // 借助redisTemplate将json存储到redis中
        // 如果永久存储在redis中可能会爆，所以设置一个过期时间
        redisTemplate.opsForValue().set(memoryId.toString(), json, Duration.ofDays(1));
    }

    @Override
    public void deleteMessages(Object memoryId) {
        // 删除会话记录
        redisTemplate.delete(memoryId.toString());
    }
}
