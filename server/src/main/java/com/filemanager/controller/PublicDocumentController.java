package com.filemanager.controller;

import com.filemanager.model.dto.DocumentDTO;
import com.filemanager.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/public/documents")
@RequiredArgsConstructor
public class PublicDocumentController {
    private static final Logger logger = LoggerFactory.getLogger(PublicDocumentController.class);
    
    private final DocumentService documentService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPublicDocuments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String companyInfo,
            @RequestParam(required = false) String brandInfo,
            @RequestParam(required = false) String productCategory,
            @RequestParam(required = false) String documentType,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            Pageable pageable) {
        
        logger.info("Public document search request - keyword: {}, companyInfo: {}, brandInfo: {}, productCategory: {}, documentType: {}, language: {}, tags: {}",
                keyword, companyInfo, brandInfo, productCategory, documentType, language, tags);
        
        Page<DocumentDTO> documents = documentService.searchPublicDocuments(
                keyword, companyInfo, brandInfo, productCategory, documentType, language, tags, pageable);
        
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取公共文档列表成功",
                "data", Map.of(
                        "items", documents.getContent(),
                        "total", documents.getTotalElements(),
                        "page", documents.getNumber(),
                        "size", documents.getSize()
                )
        ));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPublicDocumentById(@PathVariable Long id) {
        logger.info("Public document detail request - id: {}", id);
        
        DocumentDTO document = documentService.getPublicDocument(id);
        
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取公共文档详情成功",
                "data", document
        ));
    }
} 