package dev.batugokce;

import dev.batugokce.sign.BesSign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.batugokce.util.ConsoleUtil.getFilePath;

/**
 * Hello world!
 */
public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        LOGGER.info("İmzalama uygulamasına hoş geldiniz.");

        String filePath = getFilePath();

        if (filePath == null || filePath.isEmpty()) {
            LOGGER.error("İmzalanacak içeriğin dosya yolu alınamadı.");
            return;
        }

        new BesSign().signCadesBes(filePath);
    }


}
