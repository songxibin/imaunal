package com.filemanager.service.impl;

import com.filemanager.model.Document;
import com.filemanager.model.User;
import com.filemanager.model.dto.DocumentDTO;
import com.filemanager.repository.DocumentRepository;
import com.filemanager.service.DocumentService;
import com.filemanager.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final UserService userService;
    private final String uploadDir;

    public DocumentServiceImpl(
            DocumentRepository documentRepository,
            UserService userService,
            @Value("${file.upload-dir}") String uploadDir) {
        this.documentRepository = documentRepository;
        this.userService = userService;
        this.uploadDir = uploadDir;
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    @Transactional
    public DocumentDTO uploadDocument(MultipartFile file, String title, String description, List<String> tags) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetLocation = Paths.get(uploadDir).resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);

            Document document = new Document();
            document.setTitle(title);
            document.setDescription(description);
            document.setFileName(fileName);
            document.setFilePath(targetLocation.toString());
            document.setFileSize(file.getSize());
            document.setFileType(file.getContentType());
            document.setTags(tags);
            document.setCreator((User) userService.loadUserByUsername(
                    userService.getCurrentUser().getUsername()));

            Document savedDocument = documentRepository.save(document);
            return convertToDTO(savedDocument);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    @Override
    public DocumentDTO getDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return convertToDTO(document);
    }

    @Override
    public Page<DocumentDTO> getDocuments(Pageable pageable) {
        return documentRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<DocumentDTO> searchDocuments(String keyword, Pageable pageable) {
        return documentRepository.searchByKeyword(keyword, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public DocumentDTO updateDocument(Long id, String title, String description, List<String> tags) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (title != null) {
            document.setTitle(title);
        }
        if (description != null) {
            document.setDescription(description);
        }
        if (tags != null) {
            document.setTags(tags);
        }

        Document updatedDocument = documentRepository.save(document);
        return convertToDTO(updatedDocument);
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        try {
            Path filePath = Paths.get(document.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file", ex);
        }

        documentRepository.delete(document);
    }

    @Override
    public Resource downloadDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        try {
            Path filePath = Paths.get(document.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found");
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found", ex);
        }
    }

    @Override
    public String getPreviewUrl(Long id) {
        // TODO: Implement document preview URL generation
        return "/api/v1/documents/" + id + "/preview";
    }

    private DocumentDTO convertToDTO(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setDescription(document.getDescription());
        dto.setFileName(document.getFileName());
        dto.setFileSize(document.getFileSize());
        dto.setFileType(document.getFileType());
        dto.setTags(document.getTags());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());
        dto.setDownloadUrl("/api/v1/documents/" + document.getId() + "/download");
        dto.setPreviewUrl(getPreviewUrl(document.getId()));
        return dto;
    }
} 