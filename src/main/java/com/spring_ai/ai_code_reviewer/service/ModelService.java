package com.spring_ai.ai_code_reviewer.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ModelService {

    @Autowired
    @Qualifier("openaiChatClient")
    private ChatClient openAIChatClient;
    @Autowired
    @Qualifier("geminiChatClient")
    private  ChatClient geminiChatClient;

    public ChatClient getChatClient(String provider){
        if (provider==null || provider.isEmpty()){
            return openAIChatClient;
        }
        return switch (provider.toLowerCase()){
            case "openai"->{
                yield openAIChatClient;
            }
            case "gemini"->{
                yield geminiChatClient;
            }
            default -> throw new IllegalArgumentException(
                    "Unknown Provider"
            );
        };
    }
}