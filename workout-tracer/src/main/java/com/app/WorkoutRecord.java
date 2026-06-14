package com.app;

public class WorkoutRecord {
    private String name;
    private double calories;

    public WorkoutRecord(String name, double calories) {
        this.name = name;
        this.calories = calories;
    }

    // TableView bu "get" metotlarını kullanarak sütunları doldurur
    public String getName() { return name; }
    public double getCalories() { return calories; }
}