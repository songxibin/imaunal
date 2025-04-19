package com.filemanager.model.dto;

import lombok.Data;

@Data
public class TranslateRequest {
    private String sourceLang;
    private String targetLang;
} 