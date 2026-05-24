package com.app.model;

public abstract class Workout {
    private String name;
    private int duration; // dakika cinsinden

    public Workout(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    // Abstract method: Her alt sınıf kendi kalori yakımını hesaplayacak
    public abstract double calculateCalories();

    // Getter ve Setter'lar
    public String getName() { return name; }
    public int getDuration() { return duration; }
}