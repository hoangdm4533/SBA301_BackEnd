package com.example.demologin.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demologin.service.CloudinaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_DOCUMENT_SIZE = 50 * 1024 * 1024; // 50MB for documents
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB for images
    private static final List<String> IMAGE_EXTENSIONS = List.of(
        "jpg", "jpeg", "png", "gif", "webp", "bmp", "svg"
    );
    private static final List<String> ALLOWED_EXTENSIONS = List.of(
        "jpg", "jpeg", "png", "gif", "webp", "pdf", "doc", "docx"
    );
    private static final List<String> DOCUMENT_EXTENSIONS = List.of(
        "pdf", "doc", "docx", "txt", "xls", "xlsx", "ppt", "pptx"
    );

    // Image upload methods
    @Override
    public Map<String, Object> uploadImage(MultipartFile file, String folder) throws IOException {
        validateImage(file);

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String publicId = folder + "/" + UUID.randomUUID().toString();

        Map<String, Object> uploadParams = ObjectUtils.asMap(
            "public_id", publicId,
            "folder", folder,
            "resource_type", "image",
            "overwrite", false
        );

        @SuppressWarnings("rawtypes")
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        
        String secureUrl = (String) uploadResult.get("secure_url");
        String cloudinaryPublicId = (String) uploadResult.get("public_id");
        
        log.info("Uploaded image to Cloudinary: {} -> {}", originalFilename, secureUrl);
        
        return Map.of(
            "file_url", secureUrl,
            "cloudinary_public_id", cloudinaryPublicId,
            "file_name", publicId,
            "original_file_name", originalFilename,
            "file_type", fileExtension,
            "file_size", file.getSize()
        );
    }

    @Override
    public List<Map<String, Object>> uploadMultipleImages(MultipartFile[] files, String folder) throws IOException {
        List<Map<String, Object>> uploadedFiles = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                Map<String, Object> uploadInfo = uploadImage(file, folder);
                uploadedFiles.add(uploadInfo);
            }
        }
        
        log.info("Uploaded {} images to Cloudinary folder: {}", uploadedFiles.size(), folder);
        return uploadedFiles;
    }

    @Override
    public void deleteImage(String publicId) throws IOException {
        @SuppressWarnings("rawtypes")
        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
        log.info("Deleted image from Cloudinary: {} - Result: {}", publicId, result.get("result"));
    }

    @Override
    public void deleteDocument(String publicId) throws IOException {
        @SuppressWarnings("rawtypes")
        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "raw"));
        log.info("Deleted document from Cloudinary: {} - Result: {}", publicId, result.get("result"));
    }

    @Override
    public Map<String, Object> uploadDocument(MultipartFile file, String folder) throws IOException {
        // Validate document file
        validateDocument(file);

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String publicId = folder + "/" + UUID.randomUUID().toString();

        // Upload options for documents
        Map<String, Object> uploadParams = ObjectUtils.asMap(
            "public_id", publicId,
            "folder", folder,
            "resource_type", "raw", // Use 'raw' for non-image files
            "overwrite", false
        );

        // Upload to Cloudinary
        @SuppressWarnings("rawtypes")
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        
        String secureUrl = (String) uploadResult.get("secure_url");
        String cloudinaryPublicId = (String) uploadResult.get("public_id");
        
        // For raw resources (documents), Cloudinary doesn't support fl_attachment in the URL path
        // We'll return the direct URL and handle download in the controller via Content-Disposition header
        String downloadUrl = secureUrl;
        
        log.info("Uploaded document to Cloudinary: {} -> {}", originalFilename, secureUrl);
        
        // Return detailed information
        return Map.of(
            "file_url", downloadUrl,  // Direct Cloudinary URL
            "cloudinary_public_id", cloudinaryPublicId,
            "file_name", publicId,
            "original_file_name", originalFilename,
            "file_type", fileExtension,
            "file_size", file.getSize()
        );
    }

    @Override
    public List<Map<String, Object>> uploadMultipleDocuments(MultipartFile[] files, String folder) throws IOException {
        List<Map<String, Object>> uploadedFiles = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                Map<String, Object> uploadInfo = uploadDocument(file, folder);
                uploadedFiles.add(uploadInfo);
            }
        }
        
        log.info("Uploaded {} documents to Cloudinary folder: {}", uploadedFiles.size(), folder);
        return uploadedFiles;
    }

    private void validateDocument(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_DOCUMENT_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 50MB");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!DOCUMENT_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: " + DOCUMENT_EXTENSIONS);
        }
    }

    private void validateImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("Image size exceeds maximum allowed size of 10MB");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Image type not allowed. Allowed types: " + IMAGE_EXTENSIONS);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
