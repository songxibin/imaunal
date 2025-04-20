package com.filemanager.service;

import com.filemanager.model.Document;
import com.filemanager.model.dto.DocumentDTO;
import com.filemanager.model.dto.DashboardStatsDTO;
import com.filemanager.model.dto.UserDTO;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    DocumentDTO uploadDocument(MultipartFile file, String title, String description, List<String> tags, 
                             String companyInfo, String brandInfo, String productCategory, 
                             String documentType, String language, String version);
    DocumentDTO getDocument(Long id);
    Page<DocumentDTO> getDocuments(Pageable pageable);
    Page<DocumentDTO> searchDocuments(String keyword, Pageable pageable);
    DocumentDTO updateDocument(Long id, String title, String description, List<String> tags,
                             String companyInfo, String brandInfo, String productCategory, 
                             String documentType, String language, String version);
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

    /**
     * Translate a document to another language
     *
     * @param id The ID of the document to translate
     * @param sourceLang The source language code
     * @param targetLang The target language code
     * @return The translated document DTO
     */
    DocumentDTO translateDocument(Long id, String sourceLang, String targetLang);

    /**
     * Get all translations for a master document
     *
     * @param masterDocumentId The ID of the master document
     * @return List of translated document DTOs
     */
    List<DocumentDTO> getDocumentTranslations(Long masterDocumentId);

    /**
     * 搜索公共文档（无需登录）
     * @param keyword 关键词
     * @param companyInfo 公司信息
     * @param brandInfo 品牌信息
     * @param productCategory 产品类别
     * @param documentType 文档类型
     * @param language 语言
     * @param tags 标签
     * @param pageable 分页参数
     * @return 文档列表
     */
    Page<DocumentDTO> searchPublicDocuments(
            String keyword, String companyInfo, String brandInfo, 
            String productCategory, String documentType, String language, 
            String tags, Pageable pageable);
    
    /**
     * 获取公共文档详情（无需登录）
     * @param id 文档ID
     * @return 文档详情
     */
    DocumentDTO getPublicDocument(Long id);
    
    Page<DocumentDTO> getPublicDocuments(Pageable pageable);

    /**
     * Get the current authenticated user's information
     *
     * @return The current user's DTO
     */
    UserDTO getCurrentUser();
} 