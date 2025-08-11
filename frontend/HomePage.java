import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomePage extends Application {

    private BarChart<String, Number> barChart;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);

        Label greeting = new Label("Hey User! 👋");
        greeting.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 32));
        greeting.getStyleClass().add("title");

        Label subtitle = new Label("Your Travel Statistics 📊");
        subtitle.setFont(Font.font("Segoe UI Emoji", FontWeight.NORMAL, 20));

        // Buttons
        HBox buttons = new HBox(30);
        buttons.setAlignment(Pos.CENTER);

        Button chatBtn = new Button("💬 Chat Assistant");
        chatBtn.setPrefWidth(180);
        chatBtn.setPrefHeight(50);
        chatBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        Button searchBtn = new Button("🔍 Search Places");
        searchBtn.setPrefWidth(180);
        searchBtn.setPrefHeight(50);
        searchBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        // Switch to Main.java when clicking buttons (you can adapt as per your full app design)
        chatBtn.setOnAction(e -> {
            Main mainApp = new Main();
            try {
                mainApp.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        searchBtn.setOnAction(e -> {
            Main mainApp = new Main();
            try {
                mainApp.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        buttons.getChildren().addAll(chatBtn, searchBtn);

        // Bar Chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Top Searched Places");
        barChart.setPrefHeight(400);
        barChart.setLegendVisible(false);

        // Load chart data
        loadChartData();

        // Add static image
        ImageView homeImage = new ImageView(new Image("file:frontend/resources/home_image.jpg"));
        homeImage.setFitWidth(400);
        homeImage.setPreserveRatio(true);

        content.getChildren().addAll(greeting, subtitle, buttons, barChart, homeImage);
        root.setCenter(content);

        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("TripMate - Home");
        primaryStage.show();
    }

    private void loadChartData() {
        try {
            String urlStr = "http://127.0.0.1:5000/search_statistics";
            HttpURLConnection con = (HttpURLConnection) new URL(urlStr).openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();

            JSONObject obj = new JSONObject(response.toString());
            JSONArray data = obj.getJSONArray("data");

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (int i = 0; i < data.length(); i++) {
                JSONObject entry = data.getJSONObject(i);
                String city = entry.getString("city");
                int count = entry.getInt("count");
                series.getData().add(new XYChart.Data<>(city, count));
            }

            barChart.getData().clear();
            barChart.getData().add(series);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
