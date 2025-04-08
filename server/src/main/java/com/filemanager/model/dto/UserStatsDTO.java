package com.filemanager.model.dto;

import lombok.Data;

@Data
public class UserStatsDTO {
    private Long userId;
    private Long totalWordCount;      // 总字数
    private Integer languageCount;     // 支持的语言数量
    private Long storageUsed;         // 已使用存储空间(bytes)
    private Long storageLimit;        // 存储空间限制(bytes)
    private String storageUsedFormatted;  // 格式化的存储空间显示
    private String storageLimitFormatted; // 格式化的限制显示
    private Double storageUsagePercent;   // 存储空间使用百分比
}