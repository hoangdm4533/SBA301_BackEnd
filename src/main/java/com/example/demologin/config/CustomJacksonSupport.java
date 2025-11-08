package com.example.demologin.config;

import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.lang.reflect.Field;

public class CustomJacksonSupport extends JacksonJsonSupport {
    public CustomJacksonSupport() {
        super();
        try {
            // Tạo ObjectMapper tùy chỉnh
            ObjectMapper customMapper = new ObjectMapper();
            customMapper.registerModule(new JavaTimeModule());
            customMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // Gán vào JacksonJsonSupport (field private "objectMapper")
            Field mapperField = JacksonJsonSupport.class.getDeclaredField("objectMapper");
            mapperField.setAccessible(true);
            mapperField.set(this, customMapper);

        } catch (Exception e) {
            throw new RuntimeException("Failed to configure custom Jackson mapper for Socket.IO", e);
        }
    }
}
