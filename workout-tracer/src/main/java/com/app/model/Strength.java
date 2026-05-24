package com.app.model;

public class Strength extends Workout {
    private int weight; // kg

    public Strength(String name, int duration, int weight) {
        super(name, duration);
        this.weight = weight;
    }

    @Override
    public double calculateCalories() {
        return getDuration() * 5.0 + (weight * 0.1); // Basit bir formül
    }
}