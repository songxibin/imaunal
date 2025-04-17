package com.filemanager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "documents")
public class Document extends BaseEntity {
    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type")
    private String fileType;

    @ElementCollection
    @CollectionTable(name = "document_tags", joinColumns = @JoinColumn(name = "document_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(name = "company_info")
    private String companyInfo;

    @Column(name = "brand_info")
    private String brandInfo;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "language")
    private String language;

    @Column(name = "version")
    private String version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DocumentStatus status = DocumentStatus.DRAFT;
}