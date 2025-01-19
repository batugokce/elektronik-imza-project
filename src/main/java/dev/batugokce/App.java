package dev.batugokce;

import dev.batugokce.sign.BesSign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;

/**
 * Hello world!
 */
public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final String IMZALANACAK_ICERIK_DEFAULT_PATH = "";


    public static void main(String[] args) throws Exception {
        LOGGER.info("İmzalama uygulamasına hoş geldiniz.");

        String filePath = getFilePath();

        if (filePath == null || filePath.isEmpty()) {
            LOGGER.error("İmzalanacak içeriğin dosya yolu alınamadı.");
            return;
        }

        new BesSign().signCadesBes(filePath);
    }

    private static String getFilePath() {
        if (IMZALANACAK_ICERIK_DEFAULT_PATH != null && !IMZALANACAK_ICERIK_DEFAULT_PATH.isEmpty()) {
            return IMZALANACAK_ICERIK_DEFAULT_PATH;
        }

        Console console = System.console();

        if (console == null) {
            LOGGER.error("Console desteği bulunmuyor.");
            return null;
        }

        return console.readLine("İmzalayacağınız dosyanın path'ini yazın: ");
    }


}
