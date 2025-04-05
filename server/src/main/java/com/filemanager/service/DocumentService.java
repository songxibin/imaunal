package com.filemanager.service;

import com.filemanager.model.Document;
import com.filemanager.model.dto.DocumentDTO;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    DocumentDTO uploadDocument(MultipartFile file, String title, String description, List<String> tags);
    DocumentDTO getDocument(Long id);
    Page<DocumentDTO> getDocuments(Pageable pageable);
    Page<DocumentDTO> searchDocuments(String keyword, Pageable pageable);
    DocumentDTO updateDocument(Long id, String title, String description, List<String> tags);
    void deleteDocument(Long id);
    Resource downloadDocument(Long id);
    String getPreviewUrl(Long id);
} 