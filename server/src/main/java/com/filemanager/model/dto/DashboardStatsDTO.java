package com.filemanager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalDocuments;
    private long monthlyUploads;
    private long totalStorage;
    private long publishedDocuments;
    private long draftDocuments;
} 