package engine;

import java.util.HashMap;
import java.util.Map;

public class Ratings {

	private Map<Integer, Map<Integer, Integer>> userMovieRating;

	public Ratings() {
		super();
		// TODO Auto-generated constructor stub
		userMovieRating = new HashMap<>();
	}

	public Ratings(Map<Integer, Map<Integer, Integer>> userMovieRating) {
		super();
		this.userMovieRating = userMovieRating;
	}

	public Map<Integer, Map<Integer, Integer>> getUserMovieRating() {
		return userMovieRating;
	}

	public void setUserMovieRating(Map<Integer, Map<Integer, Integer>> userMovieRating) {
		this.userMovieRating = userMovieRating;
	}

}
