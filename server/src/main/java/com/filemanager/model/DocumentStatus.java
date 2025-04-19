package com.filemanager.model;

/**
 * Enum representing the status of a document.
 * DRAFT: Document is in draft state, not yet published
 * PUBLISHED: Document has been published and is publicly accessible
 */
public enum DocumentStatus {
    DRAFT,
    IN_PROGRESS,    // 翻译进行中
    REVIEWING,      // 审核中
    PUBLISHED,      // 已发布
    NEEDS_UPDATE,   // 需要更新（原文档已更新）
    ARCHIVED        // 已归档
} 