package com.filemanager.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

public interface OssService {
    /**
     * Upload a file to Aliyun OSS
     *
     * @param file The file to upload
     * @param objectName The object name in OSS
     * @return The URL of the uploaded file
     * @throws IOException If an I/O error occurs
     */
    String uploadFile(MultipartFile file, String objectName) throws IOException;

    /**
     * Download a file from Aliyun OSS
     *
     * @param objectName The object name in OSS
     * @return The file as a Resource
     * @throws IOException If an I/O error occurs
     */
    Resource downloadFile(String objectName) throws IOException;

    /**
     * Delete a file from Aliyun OSS
     *
     * @param objectName The object name in OSS
     * @throws IOException If an I/O error occurs
     */
    void deleteFile(String objectName) throws IOException;

    /**
     * Generate a signed URL for a file in Aliyun OSS
     *
     * @param objectName The object name in OSS
     * @param expirationInMillis The expiration time in milliseconds
     * @return The signed URL
     */
    URL generateSignedUrl(String objectName, long expirationInMillis);

    /**
     * Copy a file from one bucket to another
     *
     * @param sourceObjectName The source object name
     * @param targetBucketName The target bucket name
     * @param targetObjectName The target object name
     * @return The URL of the copied file
     * @throws IOException If an I/O error occurs
     */
    String copyFile(String sourceObjectName, String targetBucketName, String targetObjectName) throws IOException;
} 