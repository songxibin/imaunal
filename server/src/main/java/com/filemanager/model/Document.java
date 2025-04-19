package com.filemanager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "documents")
public class Document extends BaseEntity {
    @Column(nullable = false)
    public String title;

    public String description;

    @Column(name = "file_name")
    public String fileName;

    @Column(name = "file_path")
    public String filePath;

    @Column(name = "file_size")
    public Long fileSize;

    @Column(name = "file_type")
    public String fileType;

    @ElementCollection
    @CollectionTable(name = "document_tags", joinColumns = @JoinColumn(name = "document_id"))
    @Column(name = "tag")
    public List<String> tags;

    @Column(name = "company_info")
    public String companyInfo;

    @Column(name = "brand_info")
    public String brandInfo;

    @Column(name = "product_category")
    public String productCategory;

    @Column(name = "document_type")
    public String documentType;

    @Column(name = "language")
    public String language;

    @Column(name = "version")
    public String version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    public User creator;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    public DocumentStatus status = DocumentStatus.DRAFT;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @Column(name = "is_master")
    public Boolean isMaster;

    @Column(name = "master_document_id")
    public Long masterDocumentId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isMaster == null) {
            isMaster = true; // Default to master document
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}