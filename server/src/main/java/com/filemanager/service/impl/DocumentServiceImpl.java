package com.filemanager.service.impl;

import com.filemanager.model.Document;
import com.filemanager.model.User;
import com.filemanager.model.dto.DocumentDTO;
import com.filemanager.model.dto.UserDTO;
import com.filemanager.repository.DocumentRepository;
import com.filemanager.service.DocumentService;
import com.filemanager.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
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
            logger.info("Upload directory created or already exists: {}", uploadDir);
        } catch (IOException ex) {
            logger.error("Could not create the directory where the uploaded files will be stored: {}", uploadDir, ex);
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    @Transactional
    public DocumentDTO uploadDocument(MultipartFile file, String title, String description, List<String> tags) {
        logger.debug("Processing document upload - title: {}, size: {}, type: {}", 
            title, file.getSize(), file.getContentType());
        
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetLocation = Paths.get(uploadDir).resolve(fileName);
            logger.debug("Saving file to: {}", targetLocation);
            
            Files.copy(file.getInputStream(), targetLocation);
            logger.debug("File saved successfully");

            Document document = new Document();
            document.setTitle(title);
            document.setDescription(description);
            document.setFileName(fileName);
            document.setFilePath(targetLocation.toString());
            document.setFileSize(file.getSize());
            document.setFileType(file.getContentType());
            document.setTags(tags);
            
            User currentUser = (User) userService.loadUserByUsername(
                    userService.getCurrentUser().getUsername());
            document.setCreator(currentUser);
            logger.debug("Document metadata set, creator: {}", currentUser.getUsername());

            Document savedDocument = documentRepository.save(document);
            logger.info("Document saved to database with ID: {}", savedDocument.getId());
            
            return convertToDTO(savedDocument);
        } catch (IOException ex) {
            logger.error("Failed to store file: {}", title, ex);
            throw new RuntimeException("Could not store file. Please try again!", ex);
        } catch (Exception ex) {
            logger.error("Unexpected error during document upload: {}", title, ex);
            throw new RuntimeException("Unexpected error during document upload", ex);
        }
    }

    @Override
    public DocumentDTO getDocument(Long id) {
        logger.debug("Fetching document with ID: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Document not found with ID: {}", id);
                    return new RuntimeException("Document not found");
                });
        logger.debug("Document found: {}", document.getTitle());
        return convertToDTO(document);
    }

    @Override
    public Page<DocumentDTO> getDocuments(Pageable pageable) {
        logger.debug("Fetching all documents with pageable: {}", pageable);
        Page<Document> documents = documentRepository.findAll(pageable);
        logger.debug("Found {} documents", documents.getTotalElements());
        return documents.map(this::convertToDTO);
    }

    @Override
    public Page<DocumentDTO> searchDocuments(String keyword, Pageable pageable) {
        logger.debug("Searching documents with keyword: {}", keyword);
        Page<Document> documents = documentRepository.searchByKeyword(keyword, pageable);
        logger.debug("Found {} documents matching keyword: {}", documents.getTotalElements(), keyword);
        return documents.map(this::convertToDTO);
    }

    @Override
    @Transactional
    public DocumentDTO updateDocument(Long id, String title, String description, List<String> tags) {
        logger.debug("Updating document with ID: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Document not found with ID: {}", id);
                    return new RuntimeException("Document not found");
                });

        if (title != null) {
            logger.debug("Updating title from '{}' to '{}'", document.getTitle(), title);
            document.setTitle(title);
        }
        if (description != null) {
            logger.debug("Updating description");
            document.setDescription(description);
        }
        if (tags != null) {
            logger.debug("Updating tags: {}", tags);
            document.setTags(tags);
        }

        Document updatedDocument = documentRepository.save(document);
        logger.info("Document updated successfully: {}", updatedDocument.getId());
        return convertToDTO(updatedDocument);
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        logger.debug("Deleting document with ID: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Document not found with ID: {}", id);
                    return new RuntimeException("Document not found");
                });

        try {
            Path filePath = Paths.get(document.getFilePath());
            logger.debug("Deleting file at: {}", filePath);
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                logger.debug("File deleted successfully");
            } else {
                logger.warn("File not found at: {}", filePath);
            }
        } catch (IOException ex) {
            logger.error("Could not delete file: {}", document.getFilePath(), ex);
            throw new RuntimeException("Could not delete file", ex);
        }

        documentRepository.delete(document);
        logger.info("Document deleted from database: {}", id);
    }

    @Override
    public Resource downloadDocument(Long id) {
        logger.debug("Preparing download for document with ID: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Document not found with ID: {}", id);
                    return new RuntimeException("Document not found");
                });

        try {
            Path filePath = Paths.get(document.getFilePath());
            logger.debug("Loading file from: {}", filePath);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                logger.debug("File found and ready for download");
                return resource;
            } else {
                logger.error("File not found at: {}", filePath);
                throw new RuntimeException("File not found");
            }
        } catch (MalformedURLException ex) {
            logger.error("Invalid file path: {}", document.getFilePath(), ex);
            throw new RuntimeException("File not found", ex);
        }
    }

    @Override
    public String getPreviewUrl(Long id) {
        logger.debug("Generating preview URL for document with ID: {}", id);
        // TODO: Implement document preview URL generation
        String previewUrl = "/api/v1/documents/" + id + "/preview";
        logger.debug("Preview URL generated: {}", previewUrl);
        return previewUrl;
    }

    private DocumentDTO convertToDTO(Document document) {
        logger.trace("Converting Document to DTO: {}", document.getId());
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setDocumentId(document.getId().toString());
        dto.setTitle(document.getTitle());
        dto.setDescription(document.getDescription());
        dto.setFileName(document.getFileName());
        dto.setFileSize(document.getFileSize());
        dto.setFileType(document.getFileType());
        dto.setTags(document.getTags());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());
        dto.setUploadTime(document.getCreatedAt());
        dto.setDownloadUrl("/api/v1/documents/" + document.getId() + "/download");
        dto.setPreviewUrl(getPreviewUrl(document.getId()));
        
        if (document.getCreator() != null) {
            UserDTO creatorDTO = new UserDTO();
            creatorDTO.setUserid(document.getCreator().getId());
            creatorDTO.setUsername(document.getCreator().getUsername());
            dto.setCreator(creatorDTO);
        }
        
        logger.trace("Document converted to DTO successfully");
        return dto;
    }
} 