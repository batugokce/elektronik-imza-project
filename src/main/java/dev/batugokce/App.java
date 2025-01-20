package dev.batugokce;

import dev.batugokce.ui.UserInterface;

import javax.swing.*;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserInterface::createAndShowGUI);
    }


}
