package com.chungnam.eco.common.config;


import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {
    @Value("${open_ai.secret}")
    private String API_KEY;

    @Bean
    public OpenAIClient openAiConfig() {
        return OpenAIOkHttpClient.builder()
                .apiKey(API_KEY)
                .build();
    }
}
