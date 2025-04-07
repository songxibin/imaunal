package com.filemanager.service.impl;

import com.filemanager.exception.ResourceNotFoundException;
import com.filemanager.model.Document;
import com.filemanager.model.DocumentStatus;
import com.filemanager.model.User;
import com.filemanager.model.dto.DocumentDTO;
import com.filemanager.model.dto.UserDTO;
import com.filemanager.model.dto.DashboardStatsDTO;
import com.filemanager.repository.DocumentRepository;
import com.filemanager.service.DocumentService;
import com.filemanager.service.OssService;
import com.filemanager.service.UserService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
        "application/pdf", // .pdf
        "text/plain" // .txt
    );
    
    private final DocumentRepository documentRepository;
    private final UserService userService;
    private final OssService ossService;
    private final String uploadDir;
    private final String storageType;
    private final long urlExpiration;
    private final String publicBucketName;

    public DocumentServiceImpl(
            DocumentRepository documentRepository,
            UserService userService,
            OssService ossService,
            @Value("${file.upload-dir}") String uploadDir,
            @Value("${file.storage-type}") String storageType,
            @Value("${file.download-url-expiration}") long urlExpiration,
            @Value("${aliyun.oss.public-bucket-name}") String publicBucketName) {
        this.documentRepository = documentRepository;
        this.userService = userService;
        this.ossService = ossService;
        this.uploadDir = uploadDir;
        this.storageType = storageType;
        this.urlExpiration = urlExpiration;
        this.publicBucketName = publicBucketName;
        
        // Create local upload directory if using local storage
        if ("local".equals(storageType)) {
            try {
                Files.createDirectories(Paths.get(uploadDir));
                logger.info("Upload directory created or already exists: {}", uploadDir);
            } catch (IOException ex) {
                logger.error("Could not create the directory where the uploaded files will be stored: {}", uploadDir, ex);
                throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
            }
        }
    }

    @Override
    @Transactional
    public DocumentDTO uploadDocument(MultipartFile file, String title, String description, List<String> tags,
                                    String companyInfo, String brandInfo, String productCategory, 
                                    String documentType, String language, String version) {
        logger.debug("Processing document upload - title: {}, size: {}, type: {}", 
            title, file.getSize(), file.getContentType());
        
        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            logger.error("File size exceeds limit: {}", file.getSize());
            throw new RuntimeException("文件大小不能超过50MB");
        }
        
        // Validate file type
        String contentType = file.getContentType();
//        if (!ALLOWED_FILE_TYPES.contains(contentType) && !file.getOriginalFilename().toLowerCase().endsWith(".txt")) {
//            logger.error("Invalid file type: {}", contentType);
//            throw new RuntimeException("只支持Word(.docx)、PDF和TXT文件");
//        }
        
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String filePath;
            
            if ("oss".equals(storageType)) {
                // Upload to Aliyun OSS
                filePath = ossService.uploadFile(file, fileName);
                logger.debug("File uploaded to OSS: {}", filePath);
            } else {
                // Upload to local storage
                Path targetLocation = Paths.get(uploadDir).resolve(fileName);
                logger.debug("Saving file to: {}", targetLocation);
                Files.copy(file.getInputStream(), targetLocation);
                filePath = targetLocation.toString();
                logger.debug("File saved successfully");
            }

            Document document = new Document();
            document.setTitle(title);
            document.setDescription(description);
            document.setFileName(fileName);
            document.setFilePath(filePath);
            document.setFileSize(file.getSize());
            document.setFileType(file.getContentType());
            document.setTags(tags);
            document.setCompanyInfo(companyInfo);
            document.setBrandInfo(brandInfo);
            document.setProductCategory(productCategory);
            document.setDocumentType(documentType);
            document.setLanguage(language);
            document.setVersion(version);
            document.setStatus(DocumentStatus.DRAFT);
            
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
    public DocumentDTO updateDocument(Long id, String title, String description, List<String> tags,
                                    String companyInfo, String brandInfo, String productCategory, 
                                    String documentType, String language, String version) {
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
        if (companyInfo != null) {
            logger.debug("Updating company info");
            document.setCompanyInfo(companyInfo);
        }
        if (brandInfo != null) {
            logger.debug("Updating brand info");
            document.setBrandInfo(brandInfo);
        }
        if (productCategory != null) {
            logger.debug("Updating product category");
            document.setProductCategory(productCategory);
        }
        if (documentType != null) {
            logger.debug("Updating document type");
            document.setDocumentType(documentType);
        }
        if (language != null) {
            logger.debug("Updating language");
            document.setLanguage(language);
        }
        if (version != null) {
            logger.debug("Updating version");
            document.setVersion(version);
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
            if ("oss".equals(storageType)) {
                // Delete from Aliyun OSS if it's a public file
                if(document.getStatus().equals(DocumentStatus.PUBLISHED)){
                    ossService.deletePublicFile(document.getFileName());
                }
                // Delete from Aliyun OSS
                ossService.deleteFile(document.getFileName());
                logger.debug("File deleted from OSS: {}", document.getFileName());
            } else {
                // Delete from local storage
                Path filePath = Paths.get(document.getFilePath());
                logger.debug("Deleting file at: {}", filePath);
                boolean deleted = Files.deleteIfExists(filePath);
                if (deleted) {
                    logger.debug("File deleted successfully");
                } else {
                    logger.warn("File not found at: {}", filePath);
                }
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
            if ("oss".equals(storageType)) {
                // Download from Aliyun OSS
                return ossService.downloadFile(document.getFileName());
            } else {
                // Download from local storage
                Path filePath = Paths.get(document.getFilePath());
                logger.debug("Loading file from: {}", filePath);
                Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
                if (resource.exists()) {
                    logger.debug("File found and ready for download");
                    return resource;
                } else {
                    logger.error("File not found at: {}", filePath);
                    throw new RuntimeException("File not found");
                }
            }
        } catch (IOException ex) {
            logger.error("Error downloading file: {}", document.getFilePath(), ex);
            throw new RuntimeException("File not found", ex);
        }
    }

    @Override
    public String getPreviewUrl(Long id) {
        logger.debug("Generating preview URL for document with ID: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Document not found with ID: {}", id);
                    return new RuntimeException("Document not found");
                });
        
        if ("oss".equals(storageType)) {
            // Generate a signed URL for OSS
            URL signedUrl = ossService.generateSignedUrl(document.getFileName(), urlExpiration);
            logger.debug("Signed URL generated for OSS: {}", signedUrl);
            return signedUrl.toString();
        } else {
            // Return a local URL
            String previewUrl = "/api/v1/documents/" + id + "/preview";
            logger.debug("Preview URL generated: {}", previewUrl);
            return previewUrl;
        }
    }
    
    @Override
    @Transactional
    public DocumentDTO publishDocument(Long id) {
        logger.debug("Publishing document with ID: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Document not found with ID: {}", id);
                    return new RuntimeException("Document not found");
                });
        
        // Check if document is already published
        if (DocumentStatus.PUBLISHED.equals(document.getStatus())) {
            logger.warn("Document is already published: {}", id);
            return convertToDTO(document);
        }
        
        try {
            if ("oss".equals(storageType)) {
                // Copy file from current bucket to public bucket
                String publicFilePath = ossService.copyFile(
                    document.getFileName(), 
                    publicBucketName, 
                    document.getFileName()
                );
                logger.debug("File copied to public bucket: {}", publicFilePath);
                
                // Update document status to PUBLISHED
                document.setStatus(DocumentStatus.PUBLISHED);
                document.setFilePath(publicFilePath);
                
                Document updatedDocument = documentRepository.save(document);
                logger.info("Document published successfully: {}", updatedDocument.getId());
                return convertToDTO(updatedDocument);
            } else {
                // For local storage, we don't need to copy the file
                // Just update the document status to PUBLISHED
                document.setStatus(DocumentStatus.PUBLISHED);
                
                Document updatedDocument = documentRepository.save(document);
                logger.info("Document published successfully: {}", updatedDocument.getId());
                return convertToDTO(updatedDocument);
            }
        } catch (IOException ex) {
            logger.error("Error publishing document: {}", id, ex);
            throw new RuntimeException("Could not publish document", ex);
        }
    }
    
    @Override
    @Transactional
    public DocumentDTO unpublishDocument(Long id) {
        logger.debug("Unpublishing document with ID: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Document not found with ID: {}", id);
                    return new RuntimeException("Document not found");
                });
        
        // Check if document is already in draft status
        if (DocumentStatus.DRAFT.equals(document.getStatus())) {
            logger.warn("Document is already in draft status: {}", id);
            return convertToDTO(document);
        }
        
        try {
            if ("oss".equals(storageType)) {
                // Delete file from public bucket
                ossService.deletePublicFile(document.getFileName());
                logger.debug("File deleted from public bucket: {}", document.getFileName());
                
                // Update document status to DRAFT
                document.setStatus(DocumentStatus.DRAFT);
                
                Document updatedDocument = documentRepository.save(document);
                logger.info("Document unpublished successfully: {}", updatedDocument.getId());
                return convertToDTO(updatedDocument);
            } else {
                // For local storage, we don't need to do anything with the file
                // Just update the document status to DRAFT
                document.setStatus(DocumentStatus.DRAFT);
                
                Document updatedDocument = documentRepository.save(document);
                logger.info("Document unpublished successfully: {}", updatedDocument.getId());
                return convertToDTO(updatedDocument);
            }
        } catch (IOException ex) {
            logger.error("Error unpublishing document: {}", id, ex);
            throw new RuntimeException("Could not unpublish document", ex);
        }
    }

    @Override
    public DashboardStatsDTO getDashboardStats() {
        logger.info("Getting dashboard statistics");
        
        // Get total documents count
        long totalDocuments = documentRepository.count();
        
        // Get monthly uploads (documents created in the current month)
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long monthlyUploads = documentRepository.countByCreatedAtAfter(startOfMonth);
        
        // Get total storage used
        long totalStorage = documentRepository.sumFileSize();
        
        // Get document status distribution
        long publishedDocuments = documentRepository.countByStatus(DocumentStatus.PUBLISHED);
        long draftDocuments = documentRepository.countByStatus(DocumentStatus.DRAFT);
        
        logger.info("Dashboard statistics retrieved - total: {}, monthly: {}, storage: {}, published: {}, draft: {}", 
            totalDocuments, monthlyUploads, totalStorage, publishedDocuments, draftDocuments);
        
        return new DashboardStatsDTO(
            totalDocuments,
            monthlyUploads,
            totalStorage,
            publishedDocuments,
            draftDocuments
        );
    }

    @Override
    public Page<DocumentDTO> searchPublicDocuments(
            String keyword, String companyInfo, String brandInfo,
            String productCategory, String documentType, String language,
            String tags, Pageable pageable) {
        
        // 构建查询条件
        Specification<Document> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 只查询已发布的文档
            predicates.add(cb.equal(root.get("status"), DocumentStatus.PUBLISHED));
            
            // 关键词搜索
            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.or(
                        cb.like(root.get("title"), "%" + keyword + "%"),
                        cb.like(root.get("description"), "%" + keyword + "%")
                ));
            }
            
            // 公司信息
            if (StringUtils.hasText(companyInfo)) {
                predicates.add(cb.like(root.get("companyInfo"), "%" + companyInfo + "%"));
            }
            
            // 品牌信息
            if (StringUtils.hasText(brandInfo)) {
                predicates.add(cb.like(root.get("brandInfo"), "%" + brandInfo + "%"));
            }
            
            // 产品类别
            if (StringUtils.hasText(productCategory)) {
                predicates.add(cb.like(root.get("productCategory"), "%" + productCategory + "%"));
            }
            
            // 文档类型
            if (StringUtils.hasText(documentType)) {
                predicates.add(cb.equal(root.get("documentType"), documentType));
            }
            
            // 语言
            if (StringUtils.hasText(language)) {
                predicates.add(cb.equal(root.get("language"), language));
            }
            
            // 标签
            if (StringUtils.hasText(tags)) {
                predicates.add(cb.like(root.get("tags"), "%" + tags + "%"));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        // 执行查询
        Page<Document> documents = documentRepository.findAll(spec, pageable);
        
        // 转换为DTO
        return documents.map(this::convertToDTO);
    }
    
    @Override
    public DocumentDTO getPublicDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        
        // 检查文档是否已发布
        if (document.getStatus() != DocumentStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Document not found");
        }
        
        return convertToDTO(document);
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
        dto.setCompanyInfo(document.getCompanyInfo());
        dto.setBrandInfo(document.getBrandInfo());
        dto.setProductCategory(document.getProductCategory());
        dto.setDocumentType(document.getDocumentType());
        dto.setLanguage(document.getLanguage());
        dto.setVersion(document.getVersion());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());
        dto.setUploadTime(document.getCreatedAt());
        dto.setDownloadUrl("/api/v1/documents/" + document.getId() + "/download");
        dto.setPreviewUrl(getPreviewUrl(document.getId()));
        dto.setStatus(document.getStatus());
        
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