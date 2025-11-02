package com.example.demologin.service;

public interface ObjectStorageService {
    boolean initBucket();
    boolean uploadDocument(String objectName, String content);
    String uploadImage(String objectName, byte[] imageData, String contentType);
    String fetchDocument(String objectName);
    boolean deleteDocument(String objectName);
}
