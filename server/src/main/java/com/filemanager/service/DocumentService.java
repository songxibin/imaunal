package com.filemanager.service;

import com.filemanager.model.Document;
import com.filemanager.model.dto.DocumentDTO;
import com.filemanager.model.dto.DashboardStatsDTO;
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
    
    /**
     * Publish a document, moving it from the current bucket to the public bucket
     * and updating its status to PUBLISHED
     *
     * @param id The ID of the document to publish
     * @return The updated document DTO
     */
    DocumentDTO publishDocument(Long id);
    
    /**
     * Unpublish a document, moving it from the public bucket back to the current bucket
     * and updating its status to DRAFT
     *
     * @param id The ID of the document to unpublish
     * @return The updated document DTO
     */
    DocumentDTO unpublishDocument(Long id);

    /**
     * Get dashboard statistics including total documents, monthly uploads, storage usage,
     * and document status distribution
     *
     * @return Dashboard statistics DTO
     */
    DashboardStatsDTO getDashboardStats();
} 