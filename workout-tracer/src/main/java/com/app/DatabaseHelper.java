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
    public static List<WorkoutRecord> getWorkoutHistory() {
        List<WorkoutRecord> history = new ArrayList<>();
        String sql = "SELECT * FROM workouts ORDER BY id DESC";
        
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                history.add(new WorkoutRecord(rs.getString("name"), rs.getDouble("calories")));
            }
        } catch (Exception e) {
            System.out.println("Veri çekme hatası: " + e.getMessage());
        }
        return history;
    }
    public static void deleteWorkout(String name) {
        String sql = "DELETE FROM workouts WHERE name = ?";
        
        try (Connection conn = DriverManager.getConnection(URL);
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            System.out.println("Kayıt silindi: " + name);
        } catch (Exception e) {
            System.out.println("Silme hatası: " + e.getMessage());
        }
    }
}