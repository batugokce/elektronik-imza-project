package dev.batugokce.ui;

import dev.batugokce.ImzaSonucu;
import dev.batugokce.sign.BesSign;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserInterface {

    private static final Insets INSETS_WITH_SPACE = new Insets(10, 0, 10, 0);
    private static final Insets INSETS_WITHOUT_SPACE = new Insets(0, 0, 0, 0);
    private static final List<JLabel> VISIBLE_AT_RESULT = new ArrayList<>();

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Elektronik İmza Uygulaması");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = INSETS_WITH_SPACE;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        frame.add(mainPanel);

        JLabel headerLabel = new JLabel("Elektronik İmza Uygulaması", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        mainPanel.add(headerLabel, gbc);

        // File selection
        gbc.insets = INSETS_WITHOUT_SPACE;
        JLabel fileLabel = new JLabel("İmzalanacak Dosyayı Seç:");
        fileLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        mainPanel.add(fileLabel, gbc);

        JButton fileButton = new JButton("Gözat...");
        fileButton.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        mainPanel.add(fileButton, gbc);

        JLabel filePathField = new JLabel("-");
        filePathField.setFont(new Font("Arial", Font.PLAIN, 14));
        filePathField.setForeground(new Color(102, 102, 102));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(filePathField, gbc);

        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        JLabel pinLabel = new JLabel("Akıllı Kart Şifresi:");
        pinLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 5;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(pinLabel, gbc);

        JPasswordField pinField = new JPasswordField();
        pinField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 5;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(pinField, gbc);

        JButton signButton = new JButton("İmzala");
        signButton.setFont(new Font("Arial", Font.BOLD, 14));
        signButton.setBackground(new Color(0, 153, 51));
        signButton.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 5;
        gbc.insets = new Insets(10, 0, 10, 0);
        mainPanel.add(signButton, gbc);

        JLabel resultLabel = new JLabel("İmzalı Veri Dosya Yolu:");
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        resultLabel.setVisible(false);
        VISIBLE_AT_RESULT.add(resultLabel);
        mainPanel.add(resultLabel, gbc);

        JLabel signedFilePathLabel = new JLabel("-");
        signedFilePathLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        signedFilePathLabel.setForeground(new Color(102, 102, 102));
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        signedFilePathLabel.setVisible(false);
        VISIBLE_AT_RESULT.add(signedFilePathLabel);
        mainPanel.add(signedFilePathLabel, gbc);


        JLabel imzaciLabel = new JLabel("İmzacı Bilgileri:");
        imzaciLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        imzaciLabel.setVisible(false);
        VISIBLE_AT_RESULT.add(imzaciLabel);
        mainPanel.add(imzaciLabel, gbc);

        JLabel imzaciResultLabel = new JLabel("-");
        imzaciResultLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        imzaciResultLabel.setForeground(new Color(102, 102, 102));
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        imzaciResultLabel.setVisible(false);
        VISIBLE_AT_RESULT.add(imzaciResultLabel);
        mainPanel.add(imzaciResultLabel, gbc);

        signButton.addActionListener(e -> {
            String filePath = filePathField.getText();
            char[] pin = pinField.getPassword();

            if (filePath.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Lütfen bir dosya seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (pin.length == 0) {
                JOptionPane.showMessageDialog(frame, "Lütfen akıllı kart şifresini girin.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                ImzaSonucu imzaSonucu = simulateSigning(filePath, new String(pin));
                signedFilePathLabel.setText(imzaSonucu.getImzaliVeriDosyaYolu());
                imzaciResultLabel.setText(imzaSonucu.getImzaciAdSoyad());
                JOptionPane.showMessageDialog(frame, "Dosya başarıyla imzalandı!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                VISIBLE_AT_RESULT.forEach(item -> item.setVisible(true));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "İmza işlemi sırasında bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private static ImzaSonucu simulateSigning(String filePath, String pin) throws Exception {
        return new BesSign().signCadesBes(filePath, pin);
    }

}
