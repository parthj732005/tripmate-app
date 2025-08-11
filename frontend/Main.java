import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Main extends Application {
    private boolean darkTheme = true;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        TabPane tabPane = new TabPane();

        Tab homeTab = new Tab("🏠 Home");
        homeTab.setClosable(false);
        homeTab.setContent(createHomePage());

        Tab searchTab = new Tab("🔎 Search");
        searchTab.setClosable(false);
        searchTab.setContent(createSearchPage());

        Tab chatTab = new Tab("💬 Chat Assistant");
        chatTab.setClosable(false);
        chatTab.setContent(createChatPage());

        Tab graphTab = new Tab("📊 Trends");
        graphTab.setClosable(false);
        graphTab.setContent(createGraphPage());

        tabPane.getTabs().addAll(homeTab, searchTab, chatTab, graphTab);
        root.setCenter(tabPane);

        scene = new Scene(root, 1200, 800);
        applyStyles();

        primaryStage.setTitle("TripMate - Travel Assistant");
        primaryStage.getIcons().add(new Image(getClass().getResource("/resources/icon.png").toExternalForm()));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void applyStyles() {
        scene.getStylesheets().clear();
        String themeFile = darkTheme ? "/resources/dark_theme.css" : "/resources/light_theme.css";
        scene.getStylesheets().add(getClass().getResource(themeFile).toExternalForm());
    }

    private VBox createHomePage() {
        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(50));

        Label title = new Label("🌍 Welcome to TripMate! 🚀");
        title.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 32));
        title.getStyleClass().add("title");

        ImageView imageView = new ImageView(new Image(getClass().getResource("/resources/home_image.jpg").toExternalForm(), 500, 350, true, true));

        Button toggleThemeBtn = new Button("🌙 Toggle Theme");
        toggleThemeBtn.setPrefSize(250, 60);
        toggleThemeBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        toggleThemeBtn.setOnAction(e -> {
            darkTheme = !darkTheme;
            applyStyles();
        });

        layout.getChildren().addAll(title, imageView, toggleThemeBtn);
        return layout;
    }
    private VBox createSearchPage() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("🔍 Explore Destinations");
        title.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 28));
        title.getStyleClass().add("title");

        TextField cityInput = new TextField();
        cityInput.setPromptText("Enter a city (e.g. Paris)");
        cityInput.setPrefWidth(400);
        cityInput.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        Button searchBtn = new Button("Search");
        searchBtn.setPrefSize(120, 45);
        searchBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        HBox inputBox = new HBox(10, cityInput, searchBtn);
        inputBox.setAlignment(Pos.CENTER);

        VBox resultBox = new VBox(20);
        resultBox.setPadding(new Insets(20));
        resultBox.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(resultBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);

        // Add Enter key handler to trigger the search
        cityInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchBtn.fire();
            }
        });

        searchBtn.setOnAction(e -> {
            resultBox.getChildren().clear();
            String city = cityInput.getText().trim();
            if (city.isEmpty()) return;

            try {
                String apiUrl = "http://localhost:5000/search_place?city=" + URLEncoder.encode(city, "UTF-8");
                HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) response.append(inputLine);
                in.close();

                JSONObject json = new JSONObject(response.toString());
                if (!json.getBoolean("success")) {
                    resultBox.getChildren().add(new Label("No data available."));
                    return;
                }

                JSONObject weather = json.getJSONObject("weather");
                JSONObject seasons = json.getJSONObject("seasons");
                JSONArray images = json.getJSONArray("images");
                JSONArray topPlaces = json.optJSONArray("top_places");

                // City intro label
                String cityIntro = json.optString("intro", "");
                if (!cityIntro.isEmpty()) {
                    Label cityIntroLabel = new Label("📌 About This City: " + cityIntro);
                    cityIntroLabel.setWrapText(true);
                    cityIntroLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.MEDIUM, 18));
                    cityIntroLabel.getStyleClass().add("city-intro");
                    resultBox.getChildren().add(cityIntroLabel);
                }

                // Weather info
                Label tempLabel = new Label("🌡️ " + weather.getDouble("temp_min") + "°C to " + weather.getDouble("temp_max") + "°C");
                tempLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.NORMAL, 16));
                tempLabel.getStyleClass().add("weather");

                Label descLabel = new Label("☀️ " + weather.getString("description"));
                descLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.NORMAL, 16));
                descLabel.getStyleClass().add("info");

                Label bestLabel = new Label("✅ Best Months to visit: " + seasons.getJSONArray("best_months").toString());
                bestLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.NORMAL, 16));
                bestLabel.getStyleClass().add("info");

                resultBox.getChildren().addAll(tempLabel, descLabel, bestLabel);

                // Top places
                if (topPlaces != null && topPlaces.length() > 0) {
                    Label placesTitle = new Label("📍 Top Places:");
                    placesTitle.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 16));
                    VBox placesBox = new VBox(5);
                    for (int i = 0; i < topPlaces.length(); i++) {
                        JSONObject place = topPlaces.getJSONObject(i);
                        Label placeLabel = new Label("- " + place.getString("name") + " (" + place.getInt("reviews") + " reviews)");
                        placeLabel.setWrapText(true);
                        placeLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.NORMAL, 14));
                        placeLabel.getStyleClass().add("info");
                        placesBox.getChildren().add(placeLabel);
                    }
                    resultBox.getChildren().addAll(placesTitle, placesBox);
                }

                // Images
                HBox imageBox = new HBox(15);
                imageBox.setAlignment(Pos.CENTER);
                for (int i = 0; i < Math.min(3, images.length()); i++) {
                    String imgUrl = images.getString(i);
                    ImageView imgView = new ImageView(new Image(imgUrl, 250, 160, true, true));
                    imgView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0.3, 0, 2);");
                    imageBox.getChildren().add(imgView);
                }
                resultBox.getChildren().add(imageBox);

                FadeTransition ft = new FadeTransition(Duration.millis(600), resultBox);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();

            } catch (Exception ex) {
                resultBox.getChildren().add(new Label("Error fetching data."));
                ex.printStackTrace();
            }
        });

        layout.getChildren().addAll(title, inputBox, scrollPane);
        return layout;
    }



    private VBox createChatPage() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("🤖 Ask Anything About Travel");
        title.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 24));
        title.getStyleClass().add("title");

        TextArea chatArea = new TextArea();
        chatArea.getStyleClass().add("chat-log");   // ✅ target with CSS
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(400);
        chatArea.setEditable(false);
        chatArea.setFont(Font.font("Segoe UI Emoji", FontWeight.NORMAL, 14));

        TextField input = new TextField();
        input.setPromptText("Type your question...");
        input.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        Button sendBtn = new Button("Send");
        sendBtn.setOnAction(e -> {
            String query = input.getText().trim();
            if (!query.isEmpty()) {
                String reply = getChatbotReply(query);
                chatArea.appendText("You: " + query + "\nBot: " + reply + "\n\n");
                input.clear();
            }
        });

        // Add Enter key handler to send message
        input.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendBtn.fire();
            }
        });

        VBox box = new VBox(10, input, sendBtn);
        box.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(title, chatArea, box);
        return layout;
    }

    private VBox createGraphPage() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);

        Label title = new Label("📊 Most Searched Cities");
        title.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 24));
        title.getStyleClass().add("title");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setPrefHeight(400);

        try {
            String apiUrl = "http://localhost:5000/search_statistics";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) response.append(inputLine);
            in.close();

            JSONObject json = new JSONObject(response.toString());
            if (json.getBoolean("success")) {
                JSONArray data = json.getJSONArray("data");
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.getJSONObject(i);
                    series.getData().add(new XYChart.Data<>(item.getString("city"), item.getInt("count")));
                }
                chart.getData().add(series);
            }
        } catch (Exception e) {
            layout.getChildren().add(new Label("Error loading chart."));
        }

        layout.getChildren().addAll(title, chart);
        return layout;
    }

    private String getChatbotReply(String query) {
        try {
            String urlStr = "http://localhost:5000/chatbot?query=" + URLEncoder.encode(query, "UTF-8");
            HttpURLConnection con = (HttpURLConnection) new URL(urlStr).openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) response.append(inputLine);
            in.close();
            JSONObject obj = new JSONObject(response.toString());
            return obj.getString("reply");
        } catch (Exception e) {
            return "Error connecting to chatbot.";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
