package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.lodgon.openmapfx.desktop.MapView;
import utils.SessionManager;




import java.io.IOException;

public class LivreurHomePageController {




    @FXML
    private void handleLogoutAction(ActionEvent event) {
        SessionManager.clearSession();
        try {
            Parent showroomParent = FXMLLoader.load(getClass().getResource("/Showroom.fxml"));
            Scene showroomScene = new Scene(showroomParent);

            Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
            window.setScene(showroomScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
