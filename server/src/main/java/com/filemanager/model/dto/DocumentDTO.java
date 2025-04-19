package com.filemanager.model.dto;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime uploadTime;
    private String downloadUrl;
    private String previewUrl;
    private String status;
    private UserDTO creator;
    
    // Master document fields
    private Boolean isMaster;
    private Long masterDocumentId;
    private DocumentDTO masterDocument;
    private List<DocumentDTO> translations;
    
    // Additional metadata
    private String companyInfo;
    private String brandInfo;
    private String productCategory;
    private String documentType;
    private String language;
    private String version;
}