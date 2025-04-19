package com.filemanager.controller;

import com.filemanager.model.dto.*;
import com.filemanager.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "companyInfo", required = false) String companyInfo,
            @RequestParam(value = "brandInfo", required = false) String brandInfo,
            @RequestParam(value = "productCategory", required = false) String productCategory,
            @RequestParam(value = "documentType", required = false) String documentType,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "version", required = false) String version) {
        logger.info("Uploading document: {}, size: {}", title, file.getSize());
        try {
            DocumentDTO document = documentService.uploadDocument(file, title, description, tags, 
                companyInfo, brandInfo, productCategory, documentType, language, version);
            logger.info("Document uploaded successfully: {}", document.getDocumentId());
            return ResponseEntity.ok(new ApiResponse<>(
                200,
                "上传成功",
                document
            ));
        } catch (Exception e) {
            logger.error("Failed to upload document: {}", title, e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<?> getDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        
        logger.info("Getting documents - page: {}, size: {}, keyword: {}, sortBy: {}, sortOrder: {}", 
            page, size, keyword, sortBy, sortOrder);
        
        Pageable pageable = PageRequest.of(page, size, 
            sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, 
            sortBy);
            
        Page<DocumentDTO> documentPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            logger.debug("Searching documents with keyword: {}", keyword);
            documentPage = documentService.searchDocuments(keyword, pageable);
        } else {
            logger.debug("Getting all documents");
            documentPage = documentService.getDocuments(pageable);
        }
        
        logger.info("Found {} documents", documentPage.getTotalElements());
        
        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "success",
            new PageResponse<>(
                documentPage.getTotalElements(),
                documentPage.getTotalPages(),
                documentPage.getNumber() + 1,
                documentPage.getContent()
            )
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchDocuments(
            @RequestParam String keyword,
            Pageable pageable) {
        logger.info("Searching documents with keyword: {}", keyword);
        Page<DocumentDTO> page = documentService.searchDocuments(keyword, pageable);
        logger.info("Found {} documents matching keyword: {}", page.getTotalElements(), keyword);
        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "success",
            new PageResponse<>(
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber() + 1,
                page.getContent()
            )
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDocument(@PathVariable Long id) {
        logger.info("Getting document with ID: {}", id);
        try {
            DocumentDTO document = documentService.getDocument(id);
            logger.info("Document found: {}", document.getDocumentId());
            
            // If this is a translation, get the master document
            if (document.getMasterDocumentId() != null) {
                DocumentDTO masterDocument = documentService.getDocument(document.getMasterDocumentId());
                document.setMasterDocument(masterDocument);
            }
            
            // If this is a master document, get all translations
            if (document.getIsMaster()) {
                List<DocumentDTO> translations = documentService.getDocumentTranslations(id);
                document.setTranslations(translations);
            }
            
            return ResponseEntity.ok(new ApiResponse<>(
                200,
                "success",
                document
            ));
        } catch (Exception e) {
            logger.error("Failed to get document: {}", id, e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocument(
            @PathVariable Long id,
            @RequestBody DocumentDTO documentDTO) {
        logger.info("Updating document with ID: {}", id);
        try {
            DocumentDTO updatedDocument = documentService.updateDocument(
                id, 
                documentDTO.getTitle(), 
                documentDTO.getDescription(), 
                documentDTO.getTags(),
                documentDTO.getCompanyInfo(),
                documentDTO.getBrandInfo(),
                documentDTO.getProductCategory(),
                documentDTO.getDocumentType(),
                documentDTO.getLanguage(),
                documentDTO.getVersion()
            );
            logger.info("Document updated successfully: {}", updatedDocument.getDocumentId());
            return ResponseEntity.ok(new ApiResponse<>(
                200,
                "更新成功",
                updatedDocument
            ));
        } catch (Exception e) {
            logger.error("Failed to update document: {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        logger.info("Deleting document with ID: {}", id);
        try {
            documentService.deleteDocument(id);
            logger.info("Document deleted successfully: {}", id);
            return ResponseEntity.ok(new ApiResponse<>(
                200,
                "删除成功",
                null
            ));
        } catch (Exception e) {
            logger.error("Failed to delete document: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        logger.info("Downloading document with ID: {}", id);
        try {
            Resource resource = documentService.downloadDocument(id);
            DocumentDTO document = documentService.getDocument(id);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            logger.error("Failed to download document: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<String> getPreviewUrl(@PathVariable Long id) {
        logger.info("Getting preview URL for document with ID: {}", id);
        try {
            String previewUrl = documentService.getPreviewUrl(id);
            logger.info("Preview URL generated: {}", previewUrl);
            return ResponseEntity.ok(previewUrl);
        } catch (Exception e) {
            logger.error("Failed to get preview URL for document: {}", id, e);
            throw e;
        }
    }
    
    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publishDocument(@PathVariable Long id) {
        logger.info("Publishing document with ID: {}", id);
        try {
            DocumentDTO document = documentService.publishDocument(id);
            logger.info("Document published successfully: {}", document.getDocumentId());
            return ResponseEntity.ok(new ApiResponse<>(
                200,
                "发布成功",
                document
            ));
        } catch (Exception e) {
            logger.error("Failed to publish document: {}", id, e);
            throw e;
        }
    }
    
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<?> unpublishDocument(@PathVariable Long id) {
        logger.info("Unpublishing document with ID: {}", id);
        try {
            DocumentDTO document = documentService.unpublishDocument(id);
            logger.info("Document unpublished successfully: {}", document.getDocumentId());
            return ResponseEntity.ok(new ApiResponse<>(
                200,
                "取消发布成功",
                document
            ));
        } catch (Exception e) {
            logger.error("Failed to unpublish document: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        logger.info("Getting dashboard statistics");
        try {
            DashboardStatsDTO stats = documentService.getDashboardStats();
            logger.info("Dashboard statistics retrieved successfully");
            return ResponseEntity.ok(new ApiResponse<>(
                200,
                "success",
                stats
            ));
        } catch (Exception e) {
            logger.error("Failed to get dashboard statistics", e);
            throw e;
        }
    }

    @PostMapping("/{id}/translate")
    public ResponseEntity<?> translateDocument(
            @PathVariable Long id,
            @RequestBody TranslateRequest request) {
        logger.info("Translating document with ID: {} to language: {}", id, request.getTargetLang());
        try {
            DocumentDTO translatedDocument = documentService.translateDocument(
                id,
                request.getSourceLang(),
                request.getTargetLang()
            );
            logger.info("Document translated successfully: {}", translatedDocument.getDocumentId());
            return ResponseEntity.ok(new ApiResponse<>(
                200,
                "翻译成功",
                translatedDocument
            ));
        } catch (Exception e) {
            logger.error("Failed to translate document: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/{id}/translations")
    public ResponseEntity<?> getDocumentTranslations(@PathVariable Long id) {
        logger.info("Getting translations for document with ID: {}", id);
        try {
            List<DocumentDTO> translations = documentService.getDocumentTranslations(id);
            logger.info("Found {} translations", translations.size());
            return ResponseEntity.ok(new ApiResponse<>(
                200,
                "success",
                translations
            ));
        } catch (Exception e) {
            logger.error("Failed to get document translations: {}", id, e);
            throw e;
        }
    }
} 