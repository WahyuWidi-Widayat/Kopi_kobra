package kopi_kobra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Method untuk mendapatkan koneksi ke database
    public static Connection getConnection() throws SQLException {
        // URL koneksi database, username dan password
        String url = "jdbc:mysql://localhost:3306/snakegame"; // Ganti dengan nama database yang Anda buat
        String user = "root"; // Ganti dengan username MySQL Anda
        String password = ""; // Ganti dengan password MySQL Anda jika diperlukan

        try {
            // Cek koneksi
            System.out.println("Connecting to database...");
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            throw e;
        }
    }
}