package com.spring_ai.ai_code_reviewer.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MultiModelConfig {
    @Value("${gemini.api.key}")
    private String geminiKey;
    @Value("${gemini.api.url}")
    private String gmeiniUrl;
    @Value("${gemini.api.completions.path}")
    private String completionPath;
    @Value("${gemini.api.model.name}")
    private String geminiModelName;


    @Bean("openaiChatClient")
    @Primary
    public ChatClient openAIChatClient(OpenAiChatModel openAiChatModel){
        ChatClient client =ChatClient.create(openAiChatModel);
        return client;
    }

    @Bean("geminiChatClient")
    public ChatClient geminiChatClient(OpenAiChatModel openAiChatModel){
        OpenAiApi geminiApi= OpenAiApi.builder().baseUrl(gmeiniUrl).completionsPath(completionPath).apiKey(geminiKey).build();
        OpenAiChatModel geminiModel = OpenAiChatModel.builder()
                .openAiApi(geminiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                .model(geminiModelName)
                        .temperature(1.0).build()).build();
        ChatClient client = ChatClient.create(geminiModel);
        return client;
    }
}
