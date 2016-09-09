package controller;





import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Movie;

public class MovieController {
	
	public ObservableList<String> showProcess()
	{
		ObservableList<String> lst= FXCollections.observableArrayList();
		lst.add("a");
		return lst;
	}

	public ObservableList<Movie> getMovieList(String userid) {
		Movie m1 = new Movie("Stream Queen", "/image/bg.jpg");
		Movie m2 = new Movie("First Girl I Loved", "/image/bg.jpg");
		Movie m3 = new Movie("Good Cop", "/image/bg.jpg");

		ObservableList<Movie> lstMovies = FXCollections.observableArrayList();
		lstMovies.add(m1);
		lstMovies.add(m2);
		lstMovies.add(m3);
		/*try {
			 URL url = new
			 URL("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=nepal");
			URL url = new URL(
					"https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=%s&start=%d&imgsz=medium");

			URLConnection connection = url.openConnection();

			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

			JSONObject json = new JSONObject(builder.toString());
			String imageUrl = json.getJSONObject("responseData").getJSONArray("results").getJSONObject(0)
					.getString("url");

			BufferedImage image = ImageIO.read(new URL(imageUrl));
			JOptionPane.showMessageDialog(null, "", "", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(image));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Failure", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}*/

		/*String key = "";
		String qry = "nepal";
		String cx = "012326713196929899698:08_j_1lcdmm";
		String fileType = "png,jpg";
		String searchType = "image";
		URL url = new URL("https://www.googleapis.com/customsearch/v1?key=" + key + "&amp;cx=" + cx + "&amp;q=" + qry
				+ "&amp;fileType=" + fileType + "&amp;searchType=" + searchType + "&amp;alt=json");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		GResults results = new Gson().fromJson(br, GResults.class);
		conn.disconnect();*/

		return lstMovies;
	}
}
