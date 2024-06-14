package database;

import java.sql.*;

public class Veritabani {
    private static final String url = "jdbc:sqlite:database01.db";
    static Connection conn;

    public Veritabani(){
        connect();

    }

    public void connect(){
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Veritabanına bağlandı.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void disconnect (){
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Veritabanı bağlantısı kesildi.");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery (String sql, String... params) {
        ResultSet rs = null;
        try {
            connect();
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (int i=0; i < params.length; i++) {
                stmt.setString(i+1,params[i]);
            }
            rs = stmt.executeQuery();


        } catch (SQLException e){
            e.printStackTrace();
        }
        return rs;
    }

    public String[] getDetaylarById(int id) throws SQLException {
        String[] rowData = new String[16]; // 16 elemanlı bir dizi, veritabanından gelen sütun sayısına göre ayarlanmalı

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tablolist WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            // Veritabanından gelen sütunlar, örneğin "kolon1", "kolon2", ... şeklinde sıralanabilir
            rowData[0] = rs.getString("id");
            rowData[1] = rs.getString("isim");
            rowData[2] = rs.getString("soyisim");
            rowData[3] = rs.getString("kimlik");
            rowData[4] = rs.getString("dogumyeri");
            rowData[5] = rs.getString("dogumtarihi");
            rowData[6] = rs.getString("anababa");
            rowData[7] = rs.getString("kayiptarihi");
            rowData[8] = rs.getString("muracattarihi");
            rowData[9] = rs.getString("muracatyeri");
            rowData[10] = rs.getString("olayno");
            rowData[11] = rs.getString("kayipsekli");
            rowData[12] = rs.getString("muracatkimlik");
            rowData[13] = rs.getString("muracatisim");
            rowData[14] = rs.getString("muracatyakin");
            rowData[15] = rs.getString("muracattel");

            // Diğer sütunlar için aynı şekilde devam edin
        }

        return rowData;
    }
    public static byte[] getFotoById(int id) {
        byte[] imgBytes = null;
        String url = "jdbc:sqlite:database01.db";
        String query = "SELECT foto FROM tablolist WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                imgBytes = rs.getBytes("foto");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return imgBytes;
    }
}
