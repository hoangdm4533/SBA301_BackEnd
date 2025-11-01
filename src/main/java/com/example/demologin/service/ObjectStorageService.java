package com.example.demologin.service;

public interface ObjectStorageService {
    void initBucket();
    void uploadDocument(String objectName, String content);
    String uploadImage(String objectName, byte[] imageData, String contentType);
    String fetchDocument(String objectName);
    void deleteDocument(String objectName);
}
