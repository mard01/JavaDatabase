package ui;

import database.Veritabani;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Panelv2 extends JFrame {
    private JPanel panel1;
    private JButton sorgulaButton;
    private JButton yeniKayitButton;
    private JButton çıkışButton;
    private JTextField kimlikTextField;
    private JTextField isimTextField;
    private JTextField soyisimTextField;
    private JTextField kayipsekliTextField;
    private JButton fotoğrafEkleButton;
    private JTextField muracatisimTextField;
    private JTextField muracatKimlik;
    private JTextField muracatYakin;
    private JTextField muracatTel;
    private JButton kayıtEkleButton;
    private JTable table1;
    private JTable table2;
    private JTextField araTextField;
    private JButton bulButton;
    private JPanel ekleSayfa;
    private JPanel bulSayfa;
    private JTextField dogumyeriTextField;
    private JTextField dogumtarihiTextField;
    private JTextField anababaTextField;
    private JTextField kayiptarihiTextField;
    private JTextField muracattarihiTextField;
    private JTextField muracatyeriTextField;
    private JTextField olaynoTextField;
    private File selectedFile;
    private JLabel fotoLabel;
    private Connection conn;
    private Veritabani dbHelper;
    private Map<Integer, String[]> detaylar = new HashMap<>();
    private byte[] fileBytes;

    DefaultTableModel modelim = new DefaultTableModel() {
        public boolean isCellEditable(int row, int column) {
            return false; // or a condition at your choice with row and column
        }
    };
    Object[] kolonlar = {"ID", "ISIM", "SOY ISIM", "KIMLIK NO", "DOGUM YERİ", "DOGUM TARİHİ", "ANA-BABA ADI", "KAYIP TARİHİ", "MURACAAT TARİHİ", "MURACAAT YERİ", "OLAY NO", "KAYIP ŞEKLİ"};
    Object[] satirlar = new Object[12];

    public Panelv2() {
        dbHelper = new Veritabani();
        add(panel1);
        setTitle("DataBase ");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        yeniKayitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ekleSayfa.setVisible(true);
                bulSayfa.setVisible(false);
            }
        });

        sorgulaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ekleSayfa.setVisible(false);
                bulSayfa.setVisible(true);
                yenilev2();
            }
        });

        yeniKayitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelim.setRowCount(0);
                table1.clearSelection();
                yenilev2();
            }
        });

        kayıtEkleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addv3();
                temizleTextField();
            }

            private void temizleTextField() {
                isimTextField.setText("");
                soyisimTextField.setText("");
                kimlikTextField.setText("");
                dogumyeriTextField.setText("");
                dogumtarihiTextField.setText("");
                anababaTextField.setText("");
                kayiptarihiTextField.setText("");
                muracattarihiTextField.setText("");
                muracatyeriTextField.setText("");
                olaynoTextField.setText("");
                kayipsekliTextField.setText("");
                muracatKimlik.setText("");
                muracatisimTextField.setText("");
                muracatYakin.setText("");
                muracatTel.setText("");
            }
        });

        fotoğrafEkleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    try {
                        byte[] resimBytes = Files.readAllBytes(selectedFile.toPath());



                        BufferedImage orjinalResim = ImageIO.read(selectedFile);
                        int newWidth = 200;
                        int newHeight = 400;
                        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
                        resizedImage.getGraphics().drawImage(orjinalResim, 0, 0, newWidth, newHeight, null);
                        Path tempFile = Files.createTempFile("resized_image", ".jpg");
                        ImageIO.write(resizedImage, "jpg", tempFile.toFile());
                        fileBytes = Files.readAllBytes(tempFile);

                        ImageIcon scaledIcon = new ImageIcon(resizedImage);
                        fotoLabel.setIcon(scaledIcon);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        });


        araTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                String aranacak = araTextField.getText();

                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelim);
                table2.setRowSorter(sorter);
                if (aranacak.length() == 0) {
                    sorter.setRowFilter(null); // Filtre yoksa tüm satırları göster
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter(aranacak)); // Metne göre satırları filtrele
                }
            }
        });
        table2.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();
                    int id = Integer.parseInt((String) target.getValueAt(row, 0));

                    String[] rowData;
                    try {
                        rowData = dbHelper.getDetaylarById(id);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        return;
                    }

                    JDialog detayDialog = new JDialog();
                    detayDialog.setTitle("Kayıt Detayları");
                    detayDialog.setSize(400, 600);
                    detayDialog.setLocationRelativeTo(null);

                    JPanel detayPanel = new JPanel();
                    detayPanel.setLayout(new GridBagLayout());
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(5, 5, 5, 5);
                    gbc.anchor = GridBagConstraints.WEST;

                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    detayPanel.add(new JLabel("ID: " + rowData[0]), gbc);
                    gbc.gridy++;
                    detayPanel.add(new JLabel("İsim: " + rowData[1]), gbc);
                    gbc.gridy++;
                    detayPanel.add(new JLabel("Soyisim: " + rowData[2]), gbc);
                    gbc.gridy++;
                    detayPanel.add(new JLabel("Kimlik: " + rowData[3]), gbc);
                    gbc.gridy++;
                    detayPanel.add(new JLabel("Doğum Yeri: " + rowData[4]), gbc);
                    gbc.gridy++;
                    detayPanel.add(new JLabel("Doğum Tarihi: " + rowData[5]), gbc);
                    gbc.gridy++;
                    detayPanel.add(new JLabel("Ana-Baba: " + rowData[6]), gbc);
                    gbc.gridy++;
                    detayPanel.add(new JLabel("Kayıp Tarihi: " + rowData[7]), gbc);
                    gbc.gridy++;
                    detayPanel.add(new JLabel("Müracaat Tarihi: " + rowData[8]), gbc);
                    gbc.gridy++;
                    detayPanel.add(new JLabel("Müracaat Yeri: " + rowData[9]), gbc);
                    gbc.gridy++;
                    detayPanel.add(new JLabel("Olay No: " + rowData[10]), gbc);
                    gbc.gridy++;
                    detayPanel.add(new JLabel("Kayıp Şekli: " + rowData[11]), gbc);
                    gbc.gridy++;
                    detayPanel.add(new JLabel("Müracaat Kimlik: " + rowData[12]), gbc);
                    gbc.gridy++;
                    detayPanel.add(new JLabel("Müracaat İsim: " + rowData[13]), gbc);
                    gbc.gridy++;
                    detayPanel.add(new JLabel("Müracaat Yakın: " + rowData[14]), gbc);
                    gbc.gridy++;
                    detayPanel.add(new JLabel("Müracaat Tel: " + rowData[15]), gbc);

                    // Fotoğrafı ekleyin
                    gbc.gridx = 1;
                    gbc.gridy = 0;
                    gbc.gridheight = GridBagConstraints.REMAINDER; // Fotoğrafın tüm metin etiketleri boyunca uzanmasını sağlamak için
                    gbc.anchor = GridBagConstraints.NORTH; // Üst tarafa hizalamak için
                    byte[] fotoBytes = dbHelper.getFotoById(id);
                    if (fotoBytes != null) {
                        ImageIcon imageIcon = new ImageIcon(fotoBytes);
                        JLabel resimLabel = new JLabel(imageIcon);
                        detayPanel.add(resimLabel, gbc);
                    } else {
                        detayPanel.add(new JLabel("Fotoğraf bulunamadı"), gbc);
                    }

                    detayDialog.add(new JScrollPane(detayPanel)); // JScrollPane ekleyerek kaydırma işlevi ekleyin
                    detayDialog.setVisible(true);
                }
            }
        });
    }



    public void addv3() {
        String sql = "INSERT INTO tablolist (isim, soyisim, kimlik, dogumyeri, dogumtarihi, anababa, kayiptarihi, muracattarihi, muracatyeri, olayno, kayipsekli, muracatkimlik, muracatisim, muracatyakin, muracattel, foto) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        String isim = isimTextField.getText();
        String soyisim = soyisimTextField.getText();
        String kimlik = kimlikTextField.getText();
        String dogumyeri = dogumyeriTextField.getText();
        String dogumtarihi = dogumtarihiTextField.getText();
        String anababa = anababaTextField.getText();
        String kayiptarihi = kayiptarihiTextField.getText();
        String muracattarihi = muracattarihiTextField.getText();
        String muracatyeri = muracatyeriTextField.getText();
        String olayno = olaynoTextField.getText();
        String kayipsekli = kayipsekliTextField.getText();
        String muracatkimlik = muracatKimlik.getText();
        String muracatisim = muracatisimTextField.getText();
        String muracatyakin = muracatYakin.getText();
        String muracattel = muracatTel.getText();

        if (fileBytes != null) {
            try {
                dbHelper.connect();
                conn = DriverManager.getConnection("jdbc:sqlite:database01.db");
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, isim);
                stmt.setString(2, soyisim);
                stmt.setString(3, kimlik);
                stmt.setString(4, dogumyeri);
                stmt.setString(5, dogumtarihi);
                stmt.setString(6, anababa);
                stmt.setString(7, kayiptarihi);
                stmt.setString(8, muracattarihi);
                stmt.setString(9, muracatyeri);
                stmt.setString(10, olayno);
                stmt.setString(11, kayipsekli);
                stmt.setString(12, muracatkimlik);
                stmt.setString(13, muracatisim);
                stmt.setString(14, muracatyakin);
                stmt.setString(15, muracattel);
                stmt.setBytes(16, fileBytes);

                stmt.executeUpdate();
                dbHelper.disconnect();

                JOptionPane.showMessageDialog(null, "Kayıt başarıyla eklendi.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                dbHelper.connect();
                conn = DriverManager.getConnection("jdbc:sqlite:database01.db");
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, isim);
                stmt.setString(2, soyisim);
                stmt.setString(3, kimlik);
                stmt.setString(4, dogumyeri);
                stmt.setString(5, dogumtarihi);
                stmt.setString(6, anababa);
                stmt.setString(7, kayiptarihi);
                stmt.setString(8, muracattarihi);
                stmt.setString(9, muracatyeri);
                stmt.setString(10, olayno);
                stmt.setString(11, kayipsekli);
                stmt.setString(12, muracatkimlik);
                stmt.setString(13, muracatisim);
                stmt.setString(14, muracatyakin);
                stmt.setString(15, muracattel);

                stmt.executeUpdate();
                dbHelper.disconnect();

                JOptionPane.showMessageDialog(null, "Kayıt başarıyla eklendi.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    public void yenilev2() {
        String listeleSql = "select * from tablolist";

        dbHelper.connect();
        ResultSet rs = dbHelper.executeQuery(listeleSql);
        modelim.setColumnIdentifiers(kolonlar);

        try {
            while (rs.next()) {
                satirlar[0] = rs.getString("id");
                satirlar[1] = rs.getString("isim");
                satirlar[2] = rs.getString("soyisim");
                satirlar[3] = rs.getString("kimlik");
                satirlar[4] = rs.getString("dogumyeri");
                satirlar[5] = rs.getString("dogumtarihi");
                satirlar[6] = rs.getString("anababa");
                satirlar[7] = rs.getString("kayiptarihi");
                satirlar[8] = rs.getString("muracattarihi");
                satirlar[9] = rs.getString("muracatyeri");
                satirlar[10] = rs.getString("olayno");
                satirlar[11] = rs.getString("kayipsekli");

                modelim.addRow(satirlar);
            }
            table1.setModel(modelim);
            table2.setModel(modelim);
        } catch (SQLException e1) {
            throw new RuntimeException(e1);
        }
    }
    public String[] getDetaylarById(int id) {
        String[] rowData = new String[16]; // 16 elemanlı bir dizi, veritabanından gelen sütun sayısına göre ayarlanmalı

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database01.db");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tablolist WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Örnek olarak sırasıyla kolon1, kolon2, ..., kolon16 şeklinde alabilirsiniz
                rowData[0] = satirlar [0].toString();
                rowData[1] = satirlar [1].toString();

                rowData[2] = satirlar [2].toString();

                rowData[3] = satirlar [3].toString();

                rowData[4] = satirlar [4].toString();

                rowData[5] = satirlar [5].toString();

                rowData[6] = satirlar [6].toString();

                rowData[7] = satirlar [7].toString();

                rowData[8] = satirlar [8].toString();

                rowData[9] = satirlar [9].toString();

                rowData[10] = satirlar [10].toString();

                rowData[11] = satirlar [11].toString();

                rowData[12] = satirlar [12].toString();

                rowData[13] = satirlar [13].toString();
                rowData[14] = satirlar [14].toString();
                rowData[15] = satirlar [15].toString();
                rowData[16] = satirlar [16].toString();






                // Diğer kolonlar için aynı şekilde devam edin
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rowData;
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Panelv2().setVisible(true);
            }
        });
    }
}








/*
*/
