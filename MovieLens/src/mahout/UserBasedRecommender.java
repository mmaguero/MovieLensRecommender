package mahout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.*;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

import engine.MoviesArray;

public class UserBasedRecommender {

	private static void dataRatingsToCSV(String file, HashMap<Integer, Integer> uRatings) {

		FileWriter fichero = null;
		PrintWriter pw = null;

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();

			String[] splitValue = null;
			int idUser, idMovie, rating;

			fichero = new FileWriter("ml-data/u.data.csv");
			pw = new PrintWriter(fichero);

			Iterator<Entry<Integer, Integer>> entries = uRatings.entrySet().iterator();
			Entry<Integer, Integer> entry;

			while (line != null) {

				splitValue = line.split("\t");
				idUser = Integer.parseInt(splitValue[0]);
				idMovie = Integer.parseInt(splitValue[1]);
				rating = Integer.parseInt(splitValue[2]);

				pw.println(idUser + "," + idMovie + "," + rating);

				line = br.readLine();
			}
			br.close();

			while (entries.hasNext()) {
				entry = entries.next();

				idUser = 944;
				idMovie = entry.getKey();
				rating = entry.getValue();

				pw.println(idUser + "," + idMovie + "," + rating);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fichero)
					fichero.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public static String getRecommendations(boolean stats, HashMap<Integer, Integer> uRatings, MoviesArray movies)
			throws Exception {
		String result = "";
		RandomUtils.useTestSeed(); // to randomize the evaluation result

		dataRatingsToCSV("ml-data/u.data", uRatings);
		DataModel model = new FileDataModel(new File("ml-data/u.data.csv"));

		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel model) throws TasteException {

				UserSimilarity similarity;
				if (system.Recommender.FUNCTION.equals("SPEARMAN")) {
					similarity = new SpearmanCorrelationSimilarity(model);
				} else if (system.Recommender.FUNCTION.equals("COSINE")) {
					similarity = new UncenteredCosineSimilarity(model);
				} else if (system.Recommender.FUNCTION.equals("EUCLIDEAN")) {
					similarity = new EuclideanDistanceSimilarity(model);
				} else if (system.Recommender.FUNCTION.equals("TANIMOTO")) {
					similarity = new TanimotoCoefficientSimilarity(model);
				} else if (system.Recommender.FUNCTION.equals("LIKELIHOOD")) {
					similarity = new LogLikelihoodSimilarity(model);
				} else {
					similarity = new PearsonCorrelationSimilarity(model);
				}

				UserNeighborhood neighborhood = new NearestNUserNeighborhood(system.Recommender.NEIGHBOURS, similarity,
						model);
				return new GenericUserBasedRecommender(model, neighborhood, similarity);
			}
		};

		// Recommend certain number of items for a particular user
		Recommender recommender = recommenderBuilder.buildRecommender(model);
		List<RecommendedItem> recomendations = recommender.recommend(944, system.Recommender.RECOMMENDATIONS);

		result += "Function Similarity => " + system.Recommender.FUNCTION + "\r\n";
		result += system.Recommender.SEPARATOR + "\r\n";

		for (RecommendedItem recommendedItem : recomendations) {
			if (recommendedItem.getValue() >= system.Recommender.MIN_VALUE_SHOWED) {
				result += "Movie => " + movies.getName((int) recommendedItem.getItemID()) + " | Rating => "
						+ recommendedItem.getValue() + "\r\n";
			}
		}

		if (stats)
			return result += RecommenderStats(model, recommenderBuilder);
		else
			return result;
	}

	private static String RecommenderStats(DataModel model, RecommenderBuilder recommenderBuilder)
			throws TasteException {
		String result = system.Recommender.SEPARATOR + "\r\n";;
		RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
		double score = evaluator.evaluate(recommenderBuilder, null, model, 0.9, 1.0);
		result += "RMSE: " + score + "\r\n";

		RecommenderIRStatsEvaluator statsEvaluator = new GenericRecommenderIRStatsEvaluator();
		// evaluate precision recall at 10
		IRStatistics stats = statsEvaluator.evaluate(recommenderBuilder, null, model, null, 10, 4, 0.9);

		result += "Precision: " + stats.getPrecision() + "\r\n";
		result += "Recall: " + stats.getRecall() + "\r\n";
		result += "F1 Score: " + stats.getF1Measure() + "\r\n";

		return result;
	}
}
