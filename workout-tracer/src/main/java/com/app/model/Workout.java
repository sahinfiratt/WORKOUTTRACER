package com.app.model;

public abstract class Workout {
    private String name;
    private int duration;

    public Workout(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    public abstract double calculateCalories();

    // BURAYI KONTROL ET: 'public' olduğundan emin ol!
    public int getDuration() { 
        return duration; 
    }

    public String getName() { 
        return name; 
    }
}