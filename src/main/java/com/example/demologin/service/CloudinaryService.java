package com.example.demologin.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    // Image uploads
    Map<String, Object> uploadImage(MultipartFile file, String folder) throws IOException;
    List<Map<String, Object>> uploadMultipleImages(MultipartFile[] files, String folder) throws IOException;
    void deleteImage(String publicId) throws IOException;
    
    // Document uploads
    Map<String, Object> uploadDocument(MultipartFile file, String folder) throws IOException;
    List<Map<String, Object>> uploadMultipleDocuments(MultipartFile[] files, String folder) throws IOException;
    void deleteDocument(String publicId) throws IOException;
}
