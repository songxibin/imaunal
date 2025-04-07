package com.filemanager.model.dto;

import com.filemanager.model.DocumentStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DocumentDTO {
    private Long id;
    private String documentId;
    private String title;
    private String description;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private List<String> tags;
    private String companyInfo;
    private String brandInfo;
    private String productCategory;
    private String documentType;
    private String language;
    private String version;
    private UserDTO creator;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime uploadTime;
    private String downloadUrl;
    private String previewUrl;
    private DocumentStatus status;
} 