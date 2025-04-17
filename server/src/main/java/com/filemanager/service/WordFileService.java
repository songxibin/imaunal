package com.filemanager.service;


import com.deepl.api.DeepLException;

import java.io.IOException;

public interface TranslateService {

    String TranslateText(String text) throws DeepLException, InterruptedException;


} 