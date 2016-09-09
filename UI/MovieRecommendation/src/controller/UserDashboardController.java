package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import model.Movie;

public class UserDashboardController implements Initializable {
	@FXML
	private Label lblUser;

	@FXML
	private Label lblDashboardUser;

	@FXML
	private Button btnLoadMovies;

	@FXML
	private ListView<Movie> movieList;

	@FXML
	public void initialize() {

	}

	@FXML
	private ImageView progressimage;

	@FXML
	private Label lblProgress;
	@FXML
	private ProgressBar progressbar;
	@FXML
	private ListView<String> processlist;

	@FXML
	void btnLoadMovies(ActionEvent event) {

		ObservableList<Movie> movieData = FXCollections.observableArrayList();
		MovieController mc = new MovieController();
		movieData = mc.getMovieList("movieid");
		movieList.setItems(movieData);

		movieList.setCellFactory(new Callback<ListView<Movie>, ListCell<Movie>>() {
			private ImageView imageView = new ImageView();

			@Override
			public ListCell<Movie> call(ListView<Movie> param) {
				ListCell<Movie> cell = new ListCell<Movie>() {

					@Override
					protected void updateItem(Movie item, boolean empty) {
						super.updateItem(item, empty);
						if (item != null) {
							setText(item.name);
							Image movieImage = new Image(item.image, 100, 120, false, false);
							imageView.setImage(movieImage);
							setGraphic(imageView);
						}
					}
				};
				return cell;
			}
		});
		ObservableList<String> lstProcess = FXCollections.observableArrayList();
		lstProcess = mc.showProcess();
		processlist.setItems(lstProcess);

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		lblUser.setText("WELCOME To Dashboard! UserId: " + HomeController.username);
	}

}
