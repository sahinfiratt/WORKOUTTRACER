package com.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:workout.db";

    public static void connect() {
        String sql = "CREATE TABLE IF NOT EXISTS workouts ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + "name TEXT NOT NULL, "
                   + "calories REAL NOT NULL, "
                   + "date TEXT NOT NULL" 
                   + ");";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.out.println("Veritabanı bağlantı hatası: " + e.getMessage());
        }
    }

    public static void addWorkout(String name, double calories) {
        String sql = "INSERT INTO workouts(name, calories, date) VALUES(?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, calories);
            pstmt.setString(3, LocalDate.now().toString());
            pstmt.executeUpdate();
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
                history.add(new WorkoutRecord(
                    rs.getString("name"), 
                    rs.getDouble("calories"),
                    rs.getString("date")
                ));
            }
        } catch (Exception e) {
            System.out.println("Veri çekme hatası: " + e.getMessage());
        }
        return history;
    }

    // --- YENİ EKLENEN GÜNCELLEME METODU ---
    public static void updateWorkout(String oldName, String newName, double newCalories) {
        String sql = "UPDATE workouts SET name = ?, calories = ? WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setDouble(2, newCalories);
            pstmt.setString(3, oldName);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Güncelleme hatası: " + e.getMessage());
        }
    }

    public static void deleteWorkout(String name) {
        String sql = "DELETE FROM workouts WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Silme hatası: " + e.getMessage());
        }
    }
}