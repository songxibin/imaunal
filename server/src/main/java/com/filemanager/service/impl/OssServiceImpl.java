package com.filemanager.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.filemanager.service.OssService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

@Service
public class OssServiceImpl implements OssService {
    private static final Logger logger = LoggerFactory.getLogger(OssServiceImpl.class);
    private final OSS ossClient;
    private final String bucketName;
    private final String domain;
    private final String directoryPrefix;
    private final long urlExpiration;

    public OssServiceImpl(
            OSS ossClient,
            @Value("${aliyun.oss.bucketName}") String bucketName,
            @Value("${aliyun.oss.domain}") String domain,
            @Value("${aliyun.oss.directory-prefix}") String directoryPrefix,
            @Value("${aliyun.oss.url-expiration}") long urlExpiration) {
        this.ossClient = ossClient;
        this.bucketName = bucketName;
        this.domain = domain;
        this.directoryPrefix = directoryPrefix;
        this.urlExpiration = urlExpiration;
    }

    @Override
    public String uploadFile(MultipartFile file, String objectName) throws IOException {
        logger.debug("Uploading file to OSS: {}", objectName);
        
        // Create object metadata
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        
        // Create the full object name with directory prefix
        String fullObjectName = directoryPrefix + "/" + objectName;
        
        // Create put object request
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fullObjectName, file.getInputStream(), metadata);
        
        // Upload the file
        ossClient.putObject(putObjectRequest);
        
        logger.info("File uploaded successfully to OSS: {}", fullObjectName);
        
        // Return the URL of the uploaded file
        return "https://" + bucketName + "." + domain + "/" + fullObjectName;
    }

    @Override
    public Resource downloadFile(String objectName) throws IOException {
        logger.debug("Downloading file from OSS: {}", objectName);
        
        // Create the full object name with directory prefix
        String fullObjectName = directoryPrefix + "/" + objectName;
        
        // Get the object
        OSSObject ossObject = ossClient.getObject(bucketName, fullObjectName);
        
        // Create a resource from the input stream
        InputStream inputStream = ossObject.getObjectContent();
        Resource resource = new InputStreamResource(inputStream) {
            @Override
            public String getFilename() {
                return objectName;
            }
            
            @Override
            public long contentLength() {
                return ossObject.getObjectMetadata().getContentLength();
            }
        };
        
        logger.info("File downloaded successfully from OSS: {}", fullObjectName);
        
        return resource;
    }

    @Override
    public void deleteFile(String objectName) throws IOException {
        logger.debug("Deleting file from OSS: {}", objectName);
        
        // Create the full object name with directory prefix
        String fullObjectName = directoryPrefix + "/" + objectName;
        
        // Delete the object
        ossClient.deleteObject(bucketName, fullObjectName);
        
        logger.info("File deleted successfully from OSS: {}", fullObjectName);
    }

    @Override
    public URL generateSignedUrl(String objectName, long expirationInMillis) {
        logger.debug("Generating signed URL for OSS object: {}", objectName);
        
        // Create the full object name with directory prefix
        String fullObjectName = directoryPrefix + "/" + objectName;
        
        // Set the expiration date
        Date expiration = new Date(System.currentTimeMillis() + expirationInMillis);
        
        // Generate the signed URL
        URL url = ossClient.generatePresignedUrl(bucketName, fullObjectName, expiration);
        
        logger.info("Signed URL generated successfully for OSS object: {}", fullObjectName);
        
        return url;
    }
} 