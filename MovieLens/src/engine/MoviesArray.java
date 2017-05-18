package engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import engine.Movies;

public class MoviesArray {

	private Movies[] movies;

	public MoviesArray() {
		super();
		// TODO Auto-generated constructor stub

	}

	public int size() {
		return movies.length;
	}

	public Movies[] getMovies() {
		return movies;
	}

	public String getName(int id) {
		return movies[id].getTitle();
	}

	public void getDataMovies(String file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			movies = new Movies[1683];

			String[] splitValue = null;

			while (line != null) {
				splitValue = line.split("\\|");

				movies[Integer.parseInt(splitValue[0])] = new Movies(Integer.parseInt(splitValue[0]), splitValue[1],
						splitValue[4]);

				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
