package com.filemanager.service.impl;

import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import com.filemanager.service.TranslateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TranslateServiceImpl implements TranslateService {
    private static final Logger logger = LoggerFactory.getLogger(TranslateServiceImpl.class);


    public String TranslateText(String text) throws DeepLException, InterruptedException {
//        String text = "We choose to go to the Moon in this decade and do the other things, not because they are easy, but because they are hard, because that goal will serve to organize and measure the best of our energies and skills, because that challenge is one that we are willing to accept, one we are unwilling to postpone, and one which we intend to win, and the others, too.";
        String authKey = "3b624cc5-ef9d-4dec-9b22-3386b3a1d6f2:fx";  // Replace with your key
        Translator translator = new Translator(authKey);
        TextResult result = translator.translateText(text, "en", "ZH-HANS");
        System.out.println(result.getText()); // "Bonjour, le mond
        return result.getText();
    }
}