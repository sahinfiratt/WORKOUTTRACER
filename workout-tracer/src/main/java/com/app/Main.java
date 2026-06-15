package com.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseHelper.connect();

        BorderPane root = new BorderPane();
        
        // --- 1. SOL MENÜ ---
        VBox sidebar = new VBox(15);
        sidebar.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20; -fx-border-color: #e9ecef;");
        Button btnDashboard = new Button("Dashboard");
        Button btnWorkouts = new Button("Workouts");
        sidebar.getChildren().addAll(new Label("Fitness Tracker"), btnDashboard, btnWorkouts);
        
        // --- 2. DASHBOARD ---
        VBox dashboardView = new VBox(20);
        dashboardView.setStyle("-fx-padding: 40; -fx-alignment: top-center;");
        Label dashBaslik = new Label("İstatistik Özeti");
        dashBaslik.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        Label lblTotalWorkouts = new Label("Toplam Antrenman: 0");
        lblTotalWorkouts.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        Label lblTotalCalories = new Label("Yakılan Toplam Kalori: 0 kcal");
        lblTotalCalories.setStyle("-fx-font-size: 16px; -fx-text-fill: #e84118; -fx-font-weight: bold;");
        dashboardView.getChildren().addAll(dashBaslik, lblTotalWorkouts, lblTotalCalories);

        Runnable updateDashboardStats = () -> {
            List<WorkoutRecord> records = DatabaseHelper.getWorkoutHistory();
            lblTotalWorkouts.setText("Toplam Antrenman: " + records.size());
            double totalCal = records.stream().mapToDouble(WorkoutRecord::getCalories).sum();
            lblTotalCalories.setText("Yakılan Toplam Kalori: " + Math.round(totalCal) + " kcal");
        };

        // --- 3. WORKOUTS (Tablo ve Form) ---
        VBox workoutsView = new VBox(15);
        workoutsView.setStyle("-fx-padding: 20;");
        Label baslik = new Label("Antrenman Ekle / Düzenle");
        baslik.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox inputGroup = new HBox(10);
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Kardiyo", "Güç/Ağırlık", "Esneme");
        typeBox.setValue("Güç/Ağırlık");
        
        TextField nameInput = new TextField();
        nameInput.setPromptText("Antrenman Adı");
        nameInput.setPrefWidth(250);
        inputGroup.getChildren().addAll(typeBox, nameInput);

        TextField caloriInput = new TextField();
        caloriInput.setPromptText("Yakılan Kalori (Örn: 200)");

        Button saveButton = new Button("Yeni Kaydet");
        saveButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Label mesajLabel = new Label();
        Label gecmisBaslik = new Label("Antrenman Geçmişi");
        gecmisBaslik.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");

        TableView<WorkoutRecord> table = new TableView<>();
        TableColumn<WorkoutRecord, String> dateCol = new TableColumn<>("Tarih");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(100);

        TableColumn<WorkoutRecord, String> nameCol = new TableColumn<>("Antrenman Türü ve Adı");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(250);

        TableColumn<WorkoutRecord, Double> calCol = new TableColumn<>("Kalori (kcal)");
        calCol.setCellValueFactory(new PropertyValueFactory<>("calories"));
        calCol.setPrefWidth(100);

        table.getColumns().addAll(dateCol, nameCol, calCol);
        table.getItems().addAll(DatabaseHelper.getWorkoutHistory());

        // --- GÜNCELLEME İÇİN SEÇİM DİNLEYİCİSİ (TABLOYA TIKLAMA) ---
        final String[] seciliEskiIsim = new String[1];
        
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                seciliEskiIsim[0] = newSelection.getName();
                String fullName = newSelection.getName();
                
                // "[Kardiyo] Koşu" formatından Türü ve Adı ayırıp forma doldurma
                if (fullName.startsWith("[")) {
                    int closeBracket = fullName.indexOf("]");
                    if (closeBracket != -1) {
                        typeBox.setValue(fullName.substring(1, closeBracket));
                        nameInput.setText(fullName.substring(closeBracket + 1).trim());
                    }
                } else {
                    nameInput.setText(fullName);
                }
                caloriInput.setText(String.valueOf(newSelection.getCalories()));
            }
        });

        // --- BUTON İŞLEMLERİ ---
        saveButton.setOnAction(e -> {
            String fullName = "[" + typeBox.getValue() + "] " + nameInput.getText();
            try {
                double calories = Double.parseDouble(caloriInput.getText());
                if (nameInput.getText().trim().isEmpty()) {
                    mesajLabel.setText("Hata: Antrenman adı boş olamaz!");
                    mesajLabel.setStyle("-fx-text-fill: red;");
                    return;
                }
                DatabaseHelper.addWorkout(fullName, calories);
                mesajLabel.setText("Kayıt başarılı! ✓");
                mesajLabel.setStyle("-fx-text-fill: green;");
                nameInput.clear();
                caloriInput.clear();
                seciliEskiIsim[0] = null;
                table.getItems().clear();
                table.getItems().addAll(DatabaseHelper.getWorkoutHistory());
                updateDashboardStats.run();
            } catch (NumberFormatException ex) {
                mesajLabel.setText("Hata: Lütfen kalori kısmına sayı girin!");
                mesajLabel.setStyle("-fx-text-fill: red;");
            }
        });

        Button updateButton = new Button("Seçileni Güncelle");
        updateButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black; -fx-font-weight: bold;");
        
        updateButton.setOnAction(e -> {
            if (seciliEskiIsim[0] != null) {
                String newFullName = "[" + typeBox.getValue() + "] " + nameInput.getText();
                try {
                    double calories = Double.parseDouble(caloriInput.getText());
                    if (nameInput.getText().trim().isEmpty()) {
                        mesajLabel.setText("Hata: Antrenman adı boş olamaz!");
                        mesajLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }
                    DatabaseHelper.updateWorkout(seciliEskiIsim[0], newFullName, calories);
                    mesajLabel.setText("Kayıt güncellendi! ✓");
                    mesajLabel.setStyle("-fx-text-fill: green;");
                    
                    nameInput.clear();
                    caloriInput.clear();
                    seciliEskiIsim[0] = null;
                    
                    table.getItems().clear();
                    table.getItems().addAll(DatabaseHelper.getWorkoutHistory());
                    updateDashboardStats.run();
                } catch (NumberFormatException ex) {
                    mesajLabel.setText("Hata: Lütfen kalori kısmına sayı girin!");
                    mesajLabel.setStyle("-fx-text-fill: red;");
                }
            } else {
                mesajLabel.setText("Lütfen güncellemek için tablodan bir kayıt seçin.");
                mesajLabel.setStyle("-fx-text-fill: red;");
            }
        });

        Button deleteButton = new Button("Seçileni Sil");
        deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold;");

        deleteButton.setOnAction(e -> {
            WorkoutRecord selectedItem = table.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                DatabaseHelper.deleteWorkout(selectedItem.getName());
                mesajLabel.setText("Kayıt silindi!");
                mesajLabel.setStyle("-fx-text-fill: orange;");
                
                nameInput.clear();
                caloriInput.clear();
                seciliEskiIsim[0] = null;

                table.getItems().clear();
                table.getItems().addAll(DatabaseHelper.getWorkoutHistory());
                updateDashboardStats.run();
            } else {
                mesajLabel.setText("Lütfen silmek için tablodan bir kayıt seçin.");
                mesajLabel.setStyle("-fx-text-fill: red;");
            }
        });

        // Butonları yan yana diziyoruz
        HBox formButtons = new HBox(10);
        formButtons.getChildren().addAll(saveButton, updateButton, deleteButton);

        workoutsView.getChildren().addAll(baslik, inputGroup, caloriInput, formButtons, mesajLabel, gecmisBaslik, table);
        
        // --- 4. MENÜ YÖNLENDİRMELERİ ---
        btnDashboard.setOnAction(e -> {
            updateDashboardStats.run();
            root.setCenter(dashboardView);
        });
        
        btnWorkouts.setOnAction(e -> root.setCenter(workoutsView));

        updateDashboardStats.run();
        root.setLeft(sidebar);
        root.setCenter(dashboardView);

        // --- 5. SAHNE VE BAŞLATMA ---
        Scene scene = new Scene(root, 700, 550);
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