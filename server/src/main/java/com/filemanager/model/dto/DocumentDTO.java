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
    private UserDTO creator;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime uploadTime;
    private String downloadUrl;
    private String previewUrl;
} 