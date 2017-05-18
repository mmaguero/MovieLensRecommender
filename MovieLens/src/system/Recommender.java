package system;

import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import engine.MoviesArray;
import engine.Operations;
import engine.ValueComparator;

import mahout.UserBasedRecommender;

public class Recommender {

	/*
	 * Functions: PEARSON, COSINE, SPEARMAN, EUCLIDEAN, TANIMOTO, LIKELIHOOD
	 */
	public final static String SEPARATOR = "-----------------------------------------------------------------";
	public final static int RATINGS = 20, NEIGHBOURS = 10, MIN_VALUE_SHOWED = 4, RECOMMENDATIONS = 20;
	/* if true ratings are random and automatic */
	public static boolean RANDOMIZED = true;
	public static String FUNCTION = "PEARSON";

	public static void main(String[] args) throws Exception {

		if (args.length == 1)
			RANDOMIZED = args[0].equals("1") ? true : false;
		else if (args.length == 2) {
			FUNCTION = args[1].toUpperCase();
			RANDOMIZED = args[0].equals("1") ? true : false;
		}

		MoviesArray movies = new MoviesArray();
		movies.getDataMovies("ml-data/u.item");

		Operations operations = new Operations();
		operations.getDataRatings("ml-data/u.data");

		HashMap<Integer, Integer> ratings = new HashMap<>();

		Random random = new Random();
		Scanner scannerIn = new Scanner(System.in);

		System.out.println(SEPARATOR);
		System.out.println("Hi! user ... Please say you rating value for the next movies ...");
		System.out.println(SEPARATOR);

		setRatings(movies, ratings, random, scannerIn);

		scannerIn.close();

		Map<Integer, Double> neighbourhoods = operations.getNeighbours(ratings, NEIGHBOURS);
		Map<Integer, Double> recommendations = operations.getRecommendations(ratings, neighbourhoods,
				movies.getMovies());

		ValueComparator valueComparator = new ValueComparator(recommendations);
		Map<Integer, Double> sortedRecommendations = new TreeMap<>(valueComparator);
		sortedRecommendations.putAll(recommendations);

		System.out.println(SEPARATOR);
		System.out.println("Thanks! See our recommendations for you: ");
		System.out.println(SEPARATOR);

		getMoviesRecommended(movies, sortedRecommendations);

		System.out.println(SEPARATOR);
		System.out.println("MAHOUT: ");
		System.out.println(SEPARATOR);
		System.out.println("Thanks! See our others recommendations for you: ");
		System.out.println(SEPARATOR);

		// for Mahout log
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.OFF);

		// for Mahout data => if true, show stats, else, no show
		String mahoutRecommendations = UserBasedRecommender.getRecommendations(false, ratings, movies);
		System.out.println(mahoutRecommendations);

	}

	private static void getMoviesRecommended(MoviesArray movies, Map<Integer, Double> sortedRecommendations) {
		Iterator<Entry<Integer, Double>> entries = sortedRecommendations.entrySet().iterator();
		Entry<Integer, Double> entry;
		int i = 0;
		while (entries.hasNext() && i < RECOMMENDATIONS) {
			entry = entries.next();
			if ((double) entry.getValue() >= MIN_VALUE_SHOWED) {
				System.out.println(
						"Movie => " + movies.getName((int) entry.getKey()) + " | Rating => " + entry.getValue());
				i++;
			}
		}
	}

	private static void setRatings(MoviesArray movies, HashMap<Integer, Integer> ratings, Random random, Scanner in) {
		for (int i = 0; i < RATINGS; i++) {
			int idMovie = random.nextInt(movies.size());
			while (ratings.containsKey(idMovie)) {
				idMovie = random.nextInt(movies.size());
			}

			int rating;
			do {
				System.out.println("Movie title => " + movies.getName(idMovie));
				System.out.println("Please, enter a rating [1 (worst) to 5 (best)]:");
				if (RANDOMIZED) {
					rating = random.nextInt(5) + 1;
					System.out.println(rating);
				} else {
					rating = Integer.parseInt(in.nextLine());
				}
			} while (rating < 0 || rating > 5);

			ratings.put(idMovie, rating);
		}
	}
}
