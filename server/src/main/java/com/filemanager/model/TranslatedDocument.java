package com.filemanager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "translated_documents")
public class TranslatedDocument extends Document {

    @Column(name = "translation_status")
    @Enumerated(EnumType.STRING)
    private DocumentStatus translationStatus = DocumentStatus.IN_PROGRESS;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "translator_id")
    private User translator;

    @Column(name = "target_language")
    private String targetLanguage;

    @Column(name = "translation_comments")
    private String translationComments;

    @PrePersist
    @Override
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (isMaster == null) {
            isMaster = false; // Default to master document
        }
    }
}