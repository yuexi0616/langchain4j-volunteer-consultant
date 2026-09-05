package com.ssm.consultant.aiservices;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

@AiService( // 声明式使用方法
        wiringMode = AiServiceWiringMode.EXPLICIT, // EXPLICIT表示需要手动装配
        chatModel = "openAiChatModel", // 指定模型
        streamingChatModel = "openAiStreamingChatModel",
//        chatMemory = "chatMemory", // 配置会话记忆对象
        chatMemoryProvider = "chatMemoryProvider", // 配置会话提供者对象，配置了这个就不需要上面的会话记忆对象了
        contentRetriever = "contentRetriever", // 配置向量数据库检索对象
        tools = {"reservationTool", "webSearchTool"} // 配置工具对象：预约工具 + 联网搜索工具
)
//@AiService // 直接使用这个也可以，默认值就是上面的那些
public interface ConsultantServices {
    // 用于聊天的方法
//    public String chat(String message);
    // 消息注解：系统消息，可以理解为系统提示词，从外部文件加载便于维护
    // 还用用户消息注解是加在用户消息前面的，效果没有系统消息那么强
    @SystemMessage(fromResource = "system.txt")
    // 此处有两个参数的时候，使用注解来规定
    public Flux<String> chat(@MemoryId String memoryId, @UserMessage String message);
}
