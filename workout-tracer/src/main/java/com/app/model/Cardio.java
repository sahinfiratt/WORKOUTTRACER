package com.app.model;

public class Cardio extends Workout {
    private double distance; // km

    public Cardio(String name, int duration, double distance) {
        super(name, duration);
        this.distance = distance;
    }

    @Override
    public double calculateCalories() {
        return getDuration() * 8.0 + (distance * 10);
    }
}