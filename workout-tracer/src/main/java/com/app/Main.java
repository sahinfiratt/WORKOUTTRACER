package com.app;

import com.app.model.Strength;
import com.app.model.Cardio;

public class Main {
    public static void main(String[] args) {
        // Strength sınıfını test edelim
        Strength s = new Strength("Bench Press", 45, 80);
        System.out.println(s.getName() + " yakılan kalori: " + s.calculateCalories());

        // Cardio sınıfını test edelim
        Cardio c = new Cardio("Kosu", 30, 5.0);
        System.out.println(c.getName() + " yakılan kalori: " + c.calculateCalories());
    }
}