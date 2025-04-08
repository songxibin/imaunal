package com.filemanager.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_stats")
public class UserStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", unique = true)
    private Long userId;
    
    @Column(name = "total_word_count")
    private Long totalWordCount;

    @Column(name = "total_word_used")
    private Long totalWordUsed;
    
    @Column(name = "language_count")
    private Integer languageCount;
    
    @Column(name = "storage_used")
    private Long storageUsed;
    
    @Column(name = "storage_limit")
    private Long storageLimit;
    
    @Column(name = "storage_used_formatted")
    private String storageUsedFormatted;
    
    @Column(name = "storage_limit_formatted")
    private String storageLimitFormatted;
    
    @Column(name = "storage_usage_percent")
    private Double storageUsagePercent;
}