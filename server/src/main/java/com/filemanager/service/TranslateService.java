package com.filemanager.service;


import com.deepl.api.DeepLException;

import java.io.IOException;

public interface TranslateService {

    String translateText(String text, String sourceLang, String targetLang) throws DeepLException, InterruptedException;


} 