package com.app.model;

public abstract class Workout {
    private String name;
    private int duration;

    public Workout(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    // Alt sınıflar (Strength, Cardio) bu metodu kendi formüllerine göre dolduracak
    public abstract double calculateCalories();
}