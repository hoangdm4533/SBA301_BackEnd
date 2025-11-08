package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.AuthenticatedEndpoint;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.ObjectStorageService;
import com.example.demologin.utils.AccountUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/essay-images")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Essay Image Upload", description = "APIs for uploading images for essay submissions")
public class EssayImageController {
    private final ObjectStorageService storageService;
    private final AccountUtils accountUtils;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/jpg", "image/gif", "image/webp");

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @AuthenticatedEndpoint
    @ApiResponse(message = "Image uploaded successfully")
    @Operation(
        summary = "Upload essay image",
        description = "Upload a single image for essay submission. Max size: 5MB. Supported formats: JPG, PNG, GIF, WEBP"
    )
    public ResponseEntity<ResponseObject> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = accountUtils.getCurrentUser().getUserId();
            
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ResponseObject(400, "File is empty", null));
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest()
                    .body(new ResponseObject(400, "File size exceeds 5MB limit", null));
            }

            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
                return ResponseEntity.badRequest()
                    .body(new ResponseObject(400, "Invalid file type. Allowed: JPG, PNG, GIF, WEBP", null));
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
            
            String objectKey = String.format("essay-images/%d/%d_%d%s", 
                userId, 
                System.currentTimeMillis(),
                (int)(Math.random() * 10000),
                extension
            );

            String imageUrl = storageService.uploadImage(objectKey, file.getBytes(), contentType);
            
            log.info("Image uploaded successfully: {} for user: {}", objectKey, userId);

            Map<String, Object> data = new HashMap<>();
            data.put("image_url", imageUrl);
            data.put("object_key", objectKey);
            data.put("size", file.getSize());
            data.put("content_type", contentType);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(201, "Image uploaded successfully", data));

        } catch (Exception e) {
            log.error("Failed to upload image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject(500, "Failed to upload image: " + e.getMessage(), null));
        }
    }

    @PostMapping(value = "/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @AuthenticatedEndpoint
    @ApiResponse(message = "Images uploaded successfully")
    @Operation(
        summary = "Upload multiple essay images",
        description = "Upload multiple images at once. Max 10 images. Max size per image: 5MB."
    )
    public ResponseEntity<ResponseObject> uploadMultipleImages(@RequestParam("files") MultipartFile[] files) {
        try {
            Long userId = accountUtils.getCurrentUser().getUserId();
            
            if (files == null || files.length == 0) {
                return ResponseEntity.badRequest()
                    .body(new ResponseObject(400, "No files provided", null));
            }

            if (files.length > 10) {
                return ResponseEntity.badRequest()
                    .body(new ResponseObject(400, "Maximum 10 images allowed", null));
            }

            List<Map<String, Object>> uploadedImages = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                
                try {
                    if (file.isEmpty()) {
                        errors.add("File " + (i + 1) + ": empty");
                        continue;
                    }

                    if (file.getSize() > MAX_FILE_SIZE) {
                        errors.add("File " + (i + 1) + ": exceeds 5MB");
                        continue;
                    }

                    String contentType = file.getContentType();
                    if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
                        errors.add("File " + (i + 1) + ": invalid type");
                        continue;
                    }

                    String originalFilename = file.getOriginalFilename();
                    String extension = originalFilename != null && originalFilename.contains(".") 
                        ? originalFilename.substring(originalFilename.lastIndexOf("."))
                        : ".jpg";
                    
                    String objectKey = String.format("essay-images/%d/%d_%d%s", 
                        userId, 
                        System.currentTimeMillis(),
                        i,
                        extension
                    );

                    String imageUrl = storageService.uploadImage(objectKey, file.getBytes(), contentType);
                    
                    Map<String, Object> imageData = new HashMap<>();
                    imageData.put("image_url", imageUrl);
                    imageData.put("object_key", objectKey);
                    imageData.put("size", file.getSize());
                    imageData.put("index", i);
                    
                    uploadedImages.add(imageData);

                } catch (Exception e) {
                    log.error("Failed to upload file " + (i + 1), e);
                    errors.add("File " + (i + 1) + ": " + e.getMessage());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("uploaded", uploadedImages);
            result.put("uploaded_count", uploadedImages.size());
            result.put("failed_count", errors.size());
            result.put("errors", errors);

            String message = String.format("Uploaded %d/%d images successfully", 
                uploadedImages.size(), files.length);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(201, message, result));

        } catch (Exception e) {
            log.error("Failed to upload images", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject(500, "Failed to upload images: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{objectKey}")
    @AuthenticatedEndpoint
    @ApiResponse(message = "Image deleted successfully")
    @Operation(
        summary = "Delete essay image",
        description = "Delete an uploaded image by object key"
    )
    public ResponseEntity<ResponseObject> deleteImage(@PathVariable String objectKey) {
        try {
            Long userId = accountUtils.getCurrentUser().getUserId();
            
            if (!objectKey.startsWith("essay-images/" + userId + "/")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseObject(403, "You can only delete your own images", null));
            }

            storageService.deleteDocument(objectKey);
            
            log.info("Image deleted: {} by user: {}", objectKey, userId);

            return ResponseEntity.ok(new ResponseObject(200, "Image deleted successfully", null));

        } catch (Exception e) {
            log.error("Failed to delete image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject(500, "Failed to delete image: " + e.getMessage(), null));
        }
    }
}
