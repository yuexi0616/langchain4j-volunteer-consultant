package com.ssm.consultant.controller;

import com.ssm.consultant.aiservices.ConsultantServices;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    @Autowired
    private ConsultantServices consultantServices;

//    @RequestMapping("/chat")
//    public String chat(String message){
//        String result = consultantServices.chat(message);
//        return result;
//    }

    // 在使用流式调用时，此处应该修改result和返回值类型
    @RequestMapping(value = "/chat",produces = "text/html;charset=utf-8")
    public Flux<String> chat(String memoryId, String message){
        Flux<String> result = consultantServices.chat(memoryId,message);
        return result;
    }

//    @Autowired
//    private OpenAiChatModel model;
//
//    @RequestMapping("/chat")
//    public String chat(String message){ // 用户传递的问题
//        String result = model.chat(message);
//        return result;
//    }
}
