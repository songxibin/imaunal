package com.filemanager.controller;

import com.filemanager.model.dto.ApiResponse;
import com.filemanager.model.dto.DocumentDTO;
import com.filemanager.model.dto.PageResponse;
import com.filemanager.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tags", required = false) List<String> tags) {
        logger.info("Uploading document: {}, size: {}", title, file.getSize());
        try {
            DocumentDTO document = documentService.uploadDocument(file, title, description, tags);
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
            return ResponseEntity.ok(new ApiResponse<>(
                200,
                "success",
                document
            ));
        } catch (Exception e) {
            logger.error("Failed to get document with ID: {}", id, e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocument(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tags", required = false) List<String> tags) {
        logger.info("Updating document with ID: {}", id);
        try {
            DocumentDTO document = documentService.updateDocument(id, title, description, tags);
            logger.info("Document updated successfully: {}", document.getDocumentId());
            return ResponseEntity.ok(new ApiResponse<>(
                200,
                "更新成功",
                document
            ));
        } catch (Exception e) {
            logger.error("Failed to update document with ID: {}", id, e);
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
            logger.error("Failed to delete document with ID: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        logger.info("Downloading document with ID: {}", id);
        try {
            Resource resource = documentService.downloadDocument(id);
            logger.info("Document download prepared: {}", id);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            logger.error("Failed to download document with ID: {}", id, e);
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
            logger.error("Failed to get preview URL for document with ID: {}", id, e);
            throw e;
        }
    }
} 