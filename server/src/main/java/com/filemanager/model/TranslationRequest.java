package com.filemanager.model;

import lombok.Data;

@Data
public class TranslationRequest {
    private String sourceLang;
    private String targetLang;
}