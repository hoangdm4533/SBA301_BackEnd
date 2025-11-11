package com.example.demologin.config;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.ThinkingConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeminiConfig {
    private final Client client;

    public GeminiConfig( @Value("${gemini.key}") String apiKey) {

        this.client = Client
                .builder()
                .apiKey(apiKey)
                .build();
    }

    /**
     * Hàm dùng chung để generate content từ Gemini.
     * @param modelName tên model (ví dụ: gemini-2.5-flash)
     * @param content nội dung (text, image...)
     * @param config cấu hình (temperature, system instruction, thinking...)
     * @return GenerateContentResponse
     */
    public GenerateContentResponse generate(
            String modelName,
            Content content,
            GenerateContentConfig config
    ) {
        return client.models.generateContent(modelName, content, config);
    }

}
