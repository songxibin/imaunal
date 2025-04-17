package com.filemanager.controller;

import com.filemanager.model.TranslationRequest;
import com.filemanager.model.dto.DocumentDTO;
import com.filemanager.service.DocumentService;
import com.filemanager.service.WordFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
public class DocumentTranslationController {

    @Autowired
    private WordFileService wordFileService;
    
    @Autowired
    private DocumentService documentService;

    @PostMapping("/{id}/translate")
    public ResponseEntity<?> translateDocument(
            @PathVariable Long id,
            @RequestBody TranslationRequest request) {
        try {
            DocumentDTO documentDTO = documentService.getDocument(id);
            String filePath = documentDTO.getDownloadUrl();
            String translatedPath = wordFileService.translateWordFile(filePath, 
                request.getSourceLang(), 
                request.getTargetLang());
            return ResponseEntity.ok().body(translatedPath);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("翻译失败：" + e.getMessage());
        }
    }
}