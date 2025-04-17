package com.filemanager.service;


import com.deepl.api.DeepLException;

public interface WordFileService {

    String translateWordFile(String file, String sourceLang, String targetLang) throws DeepLException, InterruptedException;

} 