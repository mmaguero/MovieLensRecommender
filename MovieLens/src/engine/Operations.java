package engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class Operations {

	private Map<Integer, Map<Integer, Integer>> userMovieRating;

	private Users[] userAvgRating;

	public Operations() {
		super();
		userAvgRating = new Users[944];
		userMovieRating = new HashMap<>();
	}

	public void getDataRatings(String file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();

			String[] splitValue = null;
			int idUser, idMovie, rating;

			while (line != null) {

				splitValue = line.split("\t");
				idUser = Integer.parseInt(splitValue[0]);
				idMovie = Integer.parseInt(splitValue[1]);
				rating = Integer.parseInt(splitValue[2]);

				if (userMovieRating.containsKey(idUser)) {
					userMovieRating.get(idUser).put(idMovie, rating);
					userAvgRating[idUser] = new Users(idUser, userAvgRating[idUser].getAverageRating() + rating);
				} else {
					Map<Integer, Integer> movieRating = new HashMap<>();
					movieRating.put(idMovie, rating);
					userMovieRating.put(idUser, movieRating);
					userAvgRating[idUser] = new Users(idUser, (double) rating);
				}

				line = br.readLine();
			}
			br.close();

			for (Users ra : userAvgRating) {
				if (ra != null)
					userAvgRating[ra.getIdUser()].setAverageRating(
							ra.getAverageRating() / (double) userMovieRating.get(ra.getIdUser()).size());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private double getAverage(Map<Integer, Integer> uRatings) {
		double uAverage = 0;
		Entry<Integer, Integer> entry;
		Iterator<Entry<Integer, Integer>> entriesIterator = uRatings.entrySet().iterator();
		while (entriesIterator.hasNext()) {
			entry = entriesIterator.next();
			uAverage += (int) entry.getValue();
		}
		return uAverage / uRatings.size();
	}

	public Map<Integer, Double> getNeighbours(Map<Integer, Integer> uRatings, int kNears) {
		Map<Integer, Double> neighbours = new HashMap<>();
		ValueComparator valueComparator = new ValueComparator(neighbours);
		Map<Integer, Double> sortedNeighbours = new TreeMap<>(valueComparator);

		double userAverage = getAverage(uRatings);
		ArrayList<Integer> matched;
		double numerator, denominator, otherDenominator, matching, u, v;

		Iterator<Entry<Integer, Double>> entriesIterator;
		Entry<Integer, Double> entry;

		Map<Integer, Double> returnMap = new TreeMap<>();

		for (int user : userMovieRating.keySet()) {
			matched = new ArrayList<>();
			for (int movie : uRatings.keySet()) {
				if (userMovieRating.get(user).containsKey(movie)) {
					matched.add(movie);
				}
			}

			if (matched.size() > 0) {
				numerator = 0;
				denominator = 0;
				otherDenominator = 0;

				for (int movie : matched) {
					u = uRatings.get(movie) - userAverage;
					v = userMovieRating.get(user).get(movie) - userAvgRating[user].getAverageRating();

					numerator += u * v;
					denominator += u * u;
					otherDenominator += v * v;
				}
				if (denominator == 0 || otherDenominator == 0) {
					matching = 0;
				} else {
					matching = numerator / (Math.sqrt(denominator) * Math.sqrt(otherDenominator));
				}
			} else {
				matching = 0;
			}

			neighbours.put(user, matching);

		}

		sortedNeighbours.putAll(neighbours);
		entriesIterator = sortedNeighbours.entrySet().iterator();

		int i = 0;
		while (entriesIterator.hasNext() && i < kNears) {
			entry = entriesIterator.next();
			if ((double) entry.getValue() > 0) {
				returnMap.put((int) entry.getKey(), (double) entry.getValue());
				i++;
			}
		}

		return returnMap;
	}

	public Map<Integer, Double> getRecommendations(Map<Integer, Integer> uRatings, Map<Integer, Double> neighbours,
			Movies[] movies) {
		Map<Integer, Double> predictedRatings = new HashMap<>();

		int movie;
		double userAverage = getAverage(uRatings), numerator, denominator, matching, predictedRating;

		for (Movies m : movies) {
			if (m != null) {
				movie = m.getId();
				if (!uRatings.containsKey(movie)) {
					numerator = 0;
					denominator = 0;
					for (int neighbourhood : neighbours.keySet()) {
						if (userMovieRating.get(neighbourhood).containsKey(movie)) {
							matching = neighbours.get(neighbourhood);
							numerator += matching * (userMovieRating.get(neighbourhood).get(movie)
									- userAvgRating[neighbourhood].getAverageRating());
							denominator += Math.abs(matching);
						}
					}
					predictedRating = 0;
					if (denominator > 0) {
						predictedRating = userAverage + numerator / denominator;
						if (predictedRating > 5) {
							predictedRating = 5;
						}
					}
					predictedRatings.put(movie, predictedRating);
				}
			}
		}

		return predictedRatings;
	}

}
