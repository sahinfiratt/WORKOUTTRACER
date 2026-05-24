package com.app;

import com.app.model.Strength;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Modelden veri al
        Strength s = new Strength("Bench Press", 45, 80);
        String sonuc = s.getName() + " yakılan kalori: " + s.calculateCalories();

        // Görsel arayüzü oluştur
        Label label = new Label(sonuc);
        VBox root = new VBox(label);
        Scene scene = new Scene(root, 300, 200);

        primaryStage.setTitle("Fitness Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}