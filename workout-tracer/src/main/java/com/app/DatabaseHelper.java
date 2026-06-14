package com.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:fitness_tracker.db";

    public static void connect() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                System.out.println("Veritabanı bağlantısı başarılı!");
                createTables();
            }
        } catch (Exception e) {
            System.out.println("Bağlantı hatası: " + e.getMessage());
        }
    }

    private static void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS workouts ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "name TEXT NOT NULL,"
                   + "calories REAL"
                   + ");";
        
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.out.println("Tablo oluşturma hatası: " + e.getMessage());
        }
    }

    // Eksik olan metot buydu, eklendi:
    public static void addWorkout(String name, double calories) {
        String sql = "INSERT INTO workouts(name, calories) VALUES(?, ?)";
        
        try (Connection conn = DriverManager.getConnection(URL);
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, calories);
            pstmt.executeUpdate();
            System.out.println("Antrenman başarıyla kaydedildi: " + name);
        } catch (Exception e) {
            System.out.println("Ekleme hatası: " + e.getMessage());
        }
    }
    public static List<String> getWorkoutHistory() {
        List<String> history = new ArrayList<>();
        // En son eklenen en üstte görünsün diye ORDER BY id DESC kullanıyoruz
        String sql = "SELECT * FROM workouts ORDER BY id DESC";
        
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String record = rs.getString("name") + " | " + rs.getDouble("calories") + " kcal";
                history.add(record);
            }
        } catch (Exception e) {
            System.out.println("Veri çekme hatası: " + e.getMessage());
        }
        return history;
    }
}