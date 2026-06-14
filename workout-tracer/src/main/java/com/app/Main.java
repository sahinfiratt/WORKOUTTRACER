package com.app;

import com.app.DatabaseHelper; 

import com.app.model.Strength;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Veritabanı bağlantısını başlat
        DatabaseHelper.connect();

        // 2. Ana düzen (BorderPane: Modern paneller için temel)
        BorderPane root = new BorderPane();
        
        // Sol tarafa menü (Sidebar)
        VBox sidebar = new VBox(15);
        sidebar.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20; -fx-border-color: #e9ecef;");
        sidebar.getChildren().addAll(
            new Label("Fitness Tracker"), 
            new Button("Dashboard"), 
            new Button("Workouts")
        );
        
     // Orta kısım (Ana içerik - Veri Giriş Formu ve Liste)
        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 20;");
        
        Label baslik = new Label("Yeni Antrenman Ekle");
        baslik.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        javafx.scene.control.TextField nameInput = new javafx.scene.control.TextField();
        nameInput.setPromptText("Antrenman Adı (Örn: Bench Press 60kg 3x8)");
        
        javafx.scene.control.TextField caloriInput = new javafx.scene.control.TextField();
        caloriInput.setPromptText("Yakılan Kalori (Örn: 200)");

        Button saveButton = new Button("Kaydet");
        Label mesajLabel = new Label();

        // --- YENİ EKLENEN KISIM: Geçmiş Listesi ---
        Label gecmisBaslik = new Label("Antrenman Geçmişi");
        gecmisBaslik.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");
        
        javafx.scene.control.ListView<String> historyList = new javafx.scene.control.ListView<>();
        // Uygulama ilk açıldığında veritabanındaki mevcut verileri listeye yükle
        historyList.getItems().addAll(DatabaseHelper.getWorkoutHistory());
        // ------------------------------------------

        saveButton.setOnAction(e -> {
            String name = nameInput.getText();
            try {
                double calories = Double.parseDouble(caloriInput.getText());
                DatabaseHelper.addWorkout(name, calories);
                mesajLabel.setText(name + " başarıyla kaydedildi! ✓");
                mesajLabel.setStyle("-fx-text-fill: green;");
                nameInput.clear();
                caloriInput.clear();
                
                // Kayıt başarılı olunca listeyi temizle ve veritabanından en güncel halini tekrar çek
                historyList.getItems().clear();
                historyList.getItems().addAll(DatabaseHelper.getWorkoutHistory());
                
            } catch (NumberFormatException ex) {
                mesajLabel.setText("Hata: Lütfen kalori kısmına sayı girin!");
                mesajLabel.setStyle("-fx-text-fill: red;");
            }
        });

        // Tüm elemanları (liste dahil) ekrana ekle
        content.getChildren().addAll(baslik, nameInput, caloriInput, saveButton, mesajLabel, gecmisBaslik, historyList);
        // Örnek bir veriyi veritabanına ekle ve ekrana yaz
        Strength s = new Strength("Bench Press", 45, 80);
        DatabaseHelper.addWorkout(s.getName(), s.calculateCalories());
        
        Label statusLabel = new Label("Hoş geldin! Antrenman: " + s.getName());
        content.getChildren().add(statusLabel);
        
        root.setLeft(sidebar);
        root.setCenter(content);

        // 3. Sahne ve CSS
        Scene scene = new Scene(root, 600, 400);
        
        // CSS dosyasını yükle (style.css dosyanın src/main/resources içinde olduğundan emin ol)
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS dosyası bulunamadı, varsayılan görünüm kullanılacak.");
        }

        primaryStage.setTitle("Fitness Tracker Pro");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}