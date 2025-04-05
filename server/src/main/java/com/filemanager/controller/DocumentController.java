package com.filemanager.controller;

import com.filemanager.model.dto.ApiResponse;
import com.filemanager.model.dto.DocumentDTO;
import com.filemanager.model.dto.PageResponse;
import com.filemanager.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tags", required = false) List<String> tags) {
        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "上传成功",
            documentService.uploadDocument(file, title, description, tags)
        ));
    }

    @GetMapping
    public ResponseEntity<?> getDocuments(Pageable pageable) {
        Page<DocumentDTO> page = documentService.getDocuments(pageable);
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

    @GetMapping("/search")
    public ResponseEntity<?> searchDocuments(
            @RequestParam String keyword,
            Pageable pageable) {
        Page<DocumentDTO> page = documentService.searchDocuments(keyword, pageable);
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
        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "success",
            documentService.getDocument(id)
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocument(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tags", required = false) List<String> tags) {
        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "更新成功",
            documentService.updateDocument(id, title, description, tags)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok(new ApiResponse<>(
            200,
            "删除成功",
            null
        ));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        Resource resource = documentService.downloadDocument(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<String> getPreviewUrl(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getPreviewUrl(id));
    }
} 