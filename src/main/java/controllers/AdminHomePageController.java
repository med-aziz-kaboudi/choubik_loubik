package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import services.UserService;
import utils.SessionManager;
import services.DashboardService;


import java.sql.SQLException;
import java.util.Map;
import java.io.IOException;
import java.util.Map;


public class AdminHomePageController {



    @FXML
    private ImageView logoImage;
    @FXML
    private LineChart<String, Number> usersChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private ComboBox<String> timeIntervalComboBox;
    @FXML
    private Label totalUsersLabel; // Label for total users
    @FXML
    private Label fullIncomeLabel; // Label for full income
    @FXML
    private Label netIncomeLabel; // Label for net income
    @FXML
    private Label donationsLabel;
    private DashboardService dashboardService = new DashboardService();

    private UserService userService = new UserService();
    @FXML
    public void initialize() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/Choubikloubiik.png"));
            logoImage.setImage(logo);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading image.");
        }

        timeIntervalComboBox.getItems().addAll("Days", "Weeks", "Months", "Years");
        timeIntervalComboBox.getSelectionModel().select("Months");
        timeIntervalComboBox.setOnAction(e -> updateChart());
        dashboardService.initialize();
        updateChart();
        updateIncomesCard();
        updateTotalUsersCard();
    }
    private void updateTotalUsersCard() {
        new Thread(() -> {
            try {
                int totalUsers = dashboardService.fetchTotalUsers();
                Platform.runLater(() -> totalUsersLabel.setText(String.format("Total Users: %d", totalUsers)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateIncomesCard() {
        try {
            double totalIncome = dashboardService.fetchTotalIncomeForCurrentMonth();
            double donations = totalIncome * 0.05;
            double netIncome = totalIncome - donations;

            fullIncomeLabel.setText(String.format("Full Incomes: $%.2f", totalIncome));
            netIncomeLabel.setText(String.format("Net Incomes: $%.2f", netIncome));
            donationsLabel.setText(String.format("Donations: $%.2f", donations));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateChart() {
        usersChart.getData().clear();
        String selectedInterval = timeIntervalComboBox.getValue();

        // Fetch and display data for each user type
        displayUserTypeOverTime("client", selectedInterval, "red");
        displayUserTypeOverTime("gerant", selectedInterval, "blue");
        displayUserTypeOverTime("livreur", selectedInterval, "green");
    }
    private void displayUserTypeOverTime(String userType, String timeInterval, String color) {
        Map<String, Integer> userData = userService.fetchUserCountOverTime(userType, timeInterval);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(userType);

        userData.forEach((time, count) -> series.getData().add(new XYChart.Data<>(time, count)));



        usersChart.getData().add(series);
    }

    public void handleClientTable(ActionEvent event) {
        loadScene("ClientTable.fxml", event);
    }

    public void handleRestaurantTable(ActionEvent event) {
        loadScene("RestaurantTable.fxml", event);
    }
    public void handleLivreurTable(ActionEvent event) {
        loadScene("LivreurTable.fxml", event);
    }

    private void loadScene(String fxmlFile, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
            Stage stage = (Stage) ((MenuItem)event.getSource()).getParentPopup().getOwnerWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading FXML file: " + fxmlFile);
        }
    }



    @FXML
    private void handleLogoutAction(MouseEvent event) {
        SessionManager.clearSession();

        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/Showroom.fxml"));
            Scene scene = new Scene(parent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleCheckProfileAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AdminProfile.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) logoImage.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
