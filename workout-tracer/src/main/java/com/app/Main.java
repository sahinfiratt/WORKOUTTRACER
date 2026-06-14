package com.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseHelper.connect();

        BorderPane root = new BorderPane();
        
        // --- 1. SOL MENÜ (Sidebar) ---
        VBox sidebar = new VBox(15);
        sidebar.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20; -fx-border-color: #e9ecef;");
        
        Button btnDashboard = new Button("Dashboard");
        Button btnWorkouts = new Button("Workouts");
        
        sidebar.getChildren().addAll(new Label("Fitness Tracker"), btnDashboard, btnWorkouts);
        
        // --- 2. DASHBOARD EKRANI ---
        VBox dashboardView = new VBox(20);
        dashboardView.setStyle("-fx-padding: 40; -fx-alignment: top-center;");
        
        Label dashBaslik = new Label("İstatistik Özeti");
        dashBaslik.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        
        Label lblTotalWorkouts = new Label("Toplam Antrenman: 0");
        lblTotalWorkouts.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        
        Label lblTotalCalories = new Label("Yakılan Toplam Kalori: 0 kcal");
        lblTotalCalories.setStyle("-fx-font-size: 16px; -fx-text-fill: #e84118; -fx-font-weight: bold;");
        
        dashboardView.getChildren().addAll(dashBaslik, lblTotalWorkouts, lblTotalCalories);

        // Dashboard verilerini güncelleyen metot
        Runnable updateDashboardStats = () -> {
            List<WorkoutRecord> records = DatabaseHelper.getWorkoutHistory();
            lblTotalWorkouts.setText("Toplam Antrenman: " + records.size());
            
            // Tüm kalorileri topla
            double totalCal = records.stream().mapToDouble(WorkoutRecord::getCalories).sum();
            lblTotalCalories.setText("Yakılan Toplam Kalori: " + totalCal + " kcal");
        };

        // --- 3. WORKOUTS EKRANI (Tablo ve Form) ---
        VBox workoutsView = new VBox(15);
        workoutsView.setStyle("-fx-padding: 20;");
        
        Label baslik = new Label("Yeni Antrenman Ekle");
        baslik.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField nameInput = new TextField();
        nameInput.setPromptText("Antrenman Adı (Örn: Bench Press 60kg 3x8)");
        
        TextField caloriInput = new TextField();
        caloriInput.setPromptText("Yakılan Kalori (Örn: 200)");

        Button saveButton = new Button("Kaydet");
        Label mesajLabel = new Label();

        Label gecmisBaslik = new Label("Antrenman Geçmişi");
        gecmisBaslik.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");

        TableView<WorkoutRecord> table = new TableView<>();
        TableColumn<WorkoutRecord, String> nameCol = new TableColumn<>("Antrenman Adı");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(250);

        TableColumn<WorkoutRecord, Double> calCol = new TableColumn<>("Kalori (kcal)");
        calCol.setCellValueFactory(new PropertyValueFactory<>("calories"));
        calCol.setPrefWidth(100);

        table.getColumns().addAll(nameCol, calCol);
        table.getItems().addAll(DatabaseHelper.getWorkoutHistory());

        saveButton.setOnAction(e -> {
            String name = nameInput.getText();
            try {
                double calories = Double.parseDouble(caloriInput.getText());
                DatabaseHelper.addWorkout(name, calories);
                mesajLabel.setText(name + " başarıyla kaydedildi! ✓");
                mesajLabel.setStyle("-fx-text-fill: green;");
                nameInput.clear();
                caloriInput.clear();
                
                table.getItems().clear();
                table.getItems().addAll(DatabaseHelper.getWorkoutHistory());
                updateDashboardStats.run(); // Dashboard'u da anında güncelle
            } catch (NumberFormatException ex) {
                mesajLabel.setText("Hata: Lütfen kalori kısmına sayı girin!");
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
                
                table.getItems().clear();
                table.getItems().addAll(DatabaseHelper.getWorkoutHistory());
                updateDashboardStats.run(); // Dashboard'u da anında güncelle
            } else {
                mesajLabel.setText("Lütfen silmek için tablodan bir kayıt seçin.");
                mesajLabel.setStyle("-fx-text-fill: red;");
            }
        });

        workoutsView.getChildren().addAll(baslik, nameInput, caloriInput, saveButton, mesajLabel, gecmisBaslik, table, deleteButton);
        
        // --- 4. MENÜ YÖNLENDİRMELERİ ---
        btnDashboard.setOnAction(e -> {
            updateDashboardStats.run();
            root.setCenter(dashboardView);
        });
        
        btnWorkouts.setOnAction(e -> root.setCenter(workoutsView));

        // İlk açılışta Dashboard ekranı gelsin
        updateDashboardStats.run();
        root.setLeft(sidebar);
        root.setCenter(dashboardView);

        // --- 5. SAHNE VE BAŞLATMA ---
        Scene scene = new Scene(root, 600, 500);
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