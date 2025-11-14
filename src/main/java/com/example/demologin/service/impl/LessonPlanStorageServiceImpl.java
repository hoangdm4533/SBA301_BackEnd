package com.example.demologin.service.impl;

import com.example.demologin.service.LessonPlanStorageService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class LessonPlanStorageServiceImpl implements LessonPlanStorageService {
    private final MinioClient minioClient;
    private final String bucketName;

    public LessonPlanStorageServiceImpl(MinioClient minioClient, @Value("${minio.bucket}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    public boolean uploadContent(String objectKey, String content) {
        try (ByteArrayInputStream inputStream =
                     new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {

            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType("application/json")
                    .build();

            minioClient.putObject(args);
            return true;

        } catch (Exception e) {
            log.error("Failed to upload object '{}' to bucket '{}': {}", objectKey, bucketName, e.getMessage(), e);
            throw new RuntimeException("Không thể upload file lên MinIO", e);
        }
    }


    public String downloadContent(String objectKey) throws Exception {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectKey)
                        .build()
        )) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
