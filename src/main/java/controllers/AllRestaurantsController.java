package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import models.Gerant;
import services.GerantService;
import java.sql.SQLException;
import java.util.List;

public class AllRestaurantsController {

    @FXML
    private TextField searchField;
    @FXML
    private FlowPane restaurantsContainer;

    private GerantService gerantService = new GerantService();

    @FXML
    public void initialize() {
        loadAllRestaurants();
        setupSearchField();
    }

    private void loadAllRestaurants() {
        try {
            List<Gerant> gerants = gerantService.afficher(); // Assuming this method exists and fetches all gerants
            displayRestaurants(gerants);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupSearchField() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                List<Gerant> filteredGerants = gerantService.filterGerantsByKeyword(newVal); // Implement this method to filter based on keyword
                displayRestaurants(filteredGerants);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void displayRestaurants(List<Gerant> gerants) {
        restaurantsContainer.getChildren().clear();
        for (Gerant gerant : gerants) {
            VBox card = createGerantCard(gerant);
            restaurantsContainer.getChildren().add(card);
        }
    }

    private VBox createGerantCard(Gerant gerant) {
        VBox card = new VBox(5);
        Label nameLabel = new Label(gerant.getName());
        nameLabel.setOnMouseClicked(event -> {
            // Handle navigation to the restaurant's plats page
        });
        // Add more details to the card as needed
        card.getChildren().add(nameLabel);
        // Style your card here
        return card;
    }

@FXML
    private void handleBackAction(ActionEvent event) {
        try {
            Parent adminHomePageParent = FXMLLoader.load(getClass().getResource("/ClientHomePage.fxml"));
            Scene adminHomePageScene = new Scene(adminHomePageParent);

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

            window.setScene(adminHomePageScene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
