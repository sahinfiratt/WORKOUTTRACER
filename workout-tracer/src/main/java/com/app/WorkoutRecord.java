package com.app;

public class WorkoutRecord {
    private String name;
    private double calories;
    private String date; // Yeni eklenen tarih değişkeni

    public WorkoutRecord(String name, double calories, String date) {
        this.name = name;
        this.calories = calories;
        this.date = date;
    }

    public String getName() { return name; }
    public double getCalories() { return calories; }
    public String getDate() { return date; } // Yeni getter metodu
}