package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utils.SessionManager;

import java.io.IOException;
import java.util.Objects;

public class DashboardRestaurantController {
    public Label logoutLabel;
    public ImageView logoImage;


    @FXML
    private void handleLogoutAction(MouseEvent event) {
        SessionManager.clearSession();

        try {
            Parent showroomParent = FXMLLoader.load(getClass().getResource("/Showroom.fxml"));
            Scene showroomScene = new Scene(showroomParent);

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(showroomScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openTablePlat(ActionEvent event) {
        loadScene("TablePlat.fxml", event);
    }

    public void openTableOffres(ActionEvent event) {
        loadScene("TableOffres.fxml", event);
    }

    public void openTableCommandes(ActionEvent event) {
        loadScene("TableCommandes.fxml", event);
    }


    private void loadScene(String fxmlFile, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/" + fxmlFile)));
            Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading FXML file: " + fxmlFile);
        }
    }

    @FXML
    public void initialize() {
        try {
            Image image = new Image(getClass().getResourceAsStream("/images/Choubikloubiik.png"));
            logoImage.setImage(image);
        } catch (NullPointerException e) {
            System.out.println("Image not found");
        }
    }

    public void openTablegerant(ActionEvent event) {
        loadScene("AfficherCat.fxml", event);
    }
}
