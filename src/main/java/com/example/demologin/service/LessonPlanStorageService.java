package com.example.demologin.service;

public interface LessonPlanStorageService {
    boolean uploadContent(String objectKey, String content) throws Exception;
    String downloadContent(String objectKey) throws Exception;
}
