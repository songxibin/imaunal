package com.filemanager.service.impl;

import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import com.filemanager.service.TranslateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TranslateServiceImpl implements TranslateService {
    private static final Logger logger = LoggerFactory.getLogger(TranslateServiceImpl.class);

    @Value("${translate.authkey}")
    private String authkey;

    public String translateText(String text, String sourceLang, String targetLang) throws DeepLException, InterruptedException {
//        String text = "We choose to go to the Moon in this decade and do the other things, not because they are easy, but because they are hard, because that goal will serve to organize and measure the best of our energies and skills, because that challenge is one that we are willing to accept, one we are unwilling to postpone, and one which we intend to win, and the others, too.";

        Translator translator = new Translator(authkey);
        TextResult result = translator.translateText(text, sourceLang, targetLang);
        System.out.println(result.getText()); // "Bonjour, le mond
        return result.getText();
    }
}