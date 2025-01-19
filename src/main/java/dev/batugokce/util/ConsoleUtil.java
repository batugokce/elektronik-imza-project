package dev.batugokce.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;

import static dev.batugokce.constant.Constants.*;

public class ConsoleUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleUtil.class);

    public static String getFilePath() {
        if (IMZALANACAK_ICERIK_DEFAULT_PATH != null && !IMZALANACAK_ICERIK_DEFAULT_PATH.isEmpty()) {
            return IMZALANACAK_ICERIK_DEFAULT_PATH;
        }
        Console console = getConsole();
        return console.readLine("Lütfen imzalanacak dosyanın tam yolunu giriniz: ");
    }

    public static String getPin() {
        if (SMART_CARD_PIN != null && !SMART_CARD_PIN.isEmpty()) {
            return SMART_CARD_PIN;
        }
        Console console = getConsole();
        String pin = console.readLine("Lütfen akıllı kartın PIN kodunu giriniz: ");

        if (pin == null || pin.isEmpty()) {
            throw new RuntimeException("Pin boş olamaz!");
        }

        return pin;
    }

    private static Console getConsole() {
        Console console = System.console();

        if (console == null) {
            LOGGER.error("Console desteği bulunmuyor.");
            throw new RuntimeException("Console desteği bulunmuyor.");
        }

        return console;
    }

}
