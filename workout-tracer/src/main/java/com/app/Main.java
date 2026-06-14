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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
        
        TableView<WorkoutRecord> historyTable = new TableView<>();
        TableColumn<WorkoutRecord, String> nameColumn = new TableColumn<>("Antrenman");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<WorkoutRecord, Double> caloriesColumn = new TableColumn<>("Kalori");
        caloriesColumn.setCellValueFactory(new PropertyValueFactory<>("calories"));
        historyTable.getColumns().addAll(nameColumn, caloriesColumn);
        // Uygulama ilk açıldığında veritabanındaki mevcut verileri tabloya yükle
        historyTable.getItems().addAll(DatabaseHelper.getWorkoutHistory());
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
                historyTable.getItems().clear();
                historyTable.getItems().addAll(DatabaseHelper.getWorkoutHistory());
                
            } catch (NumberFormatException ex) {
                mesajLabel.setText("Hata: Lütfen kalori kısmına sayı girin!");
                mesajLabel.setStyle("-fx-text-fill: red;");
            }
        });

        // Tüm elemanları (liste dahil) ekrana ekle
       // --- YENİ EKLENEN KISIM: Silme Butonu ---
        Button deleteButton = new Button("Seçileni Sil");
        deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold;");

        deleteButton.setOnAction(e -> {
            WorkoutRecord selectedItem = historyTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                DatabaseHelper.deleteWorkout(selectedItem.getName());

                mesajLabel.setText("Kayıt silindi!");
                mesajLabel.setStyle("-fx-text-fill: orange;");
                
                // Listeyi yenile
                historyTable.getItems().clear();
                historyTable.getItems().addAll(DatabaseHelper.getWorkoutHistory());
            } else {
                mesajLabel.setText("Lütfen silmek için listeden bir kayıt seçin.");
                mesajLabel.setStyle("-fx-text-fill: red;");
            }
        });

        // Tüm elemanları (sil butonu dahil) ekrana ekle
        content.getChildren().addAll(baslik, nameInput, caloriInput, saveButton, mesajLabel, gecmisBaslik, historyTable, deleteButton); // Örnek bir veriyi veritabanına ekle ve ekrana yaz
       
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