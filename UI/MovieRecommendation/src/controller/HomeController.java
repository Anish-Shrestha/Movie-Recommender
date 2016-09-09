package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class HomeController {
	public static String username;
	@FXML
	private Button btnSubmitUser;

	@FXML
	private TextField txtUserId;

	@FXML
	void btnSubmitUser(ActionEvent event) {
		try {
			username = txtUserId.getText();
			if (txtUserId.getText() != "") {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UserDashboard.fxml"));
				Parent root1 = (Parent) fxmlLoader.load();
				Stage stage = new Stage();
				stage.setScene(new Scene(root1));
				stage.setTitle("Welcome to Movie Recommendation System");
				stage.show();
			} else {
				// Message.showWarningDialog("Please enter user id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
