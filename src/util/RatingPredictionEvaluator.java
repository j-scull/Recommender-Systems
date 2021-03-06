package util;

import alg.RatingPredictionAlg;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * A class to evaluate a collaborative filtering algorithm.
 */
public class RatingPredictionEvaluator {	
	
	private static double DELTA = 0.0001;
	
	private Map<UserItemPair,RatingsPair> results; // a map to store all test data predictions
	
	/**
	 * Constructor
	 * @param alg - the UBCF algorithm
	 * @param testData - a map containing the test data
	 */
	public RatingPredictionEvaluator(final RatingPredictionAlg alg, final Map<UserItemPair,Double> testData) {
		results = new HashMap<UserItemPair,RatingsPair>(); // instantiate the results hash map
		
		// iterate over test data and make predictions for all user-item pairs
		for(Iterator<Map.Entry<UserItemPair,Double>> it = testData.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<UserItemPair,Double> entry = (Map.Entry<UserItemPair,Double>)it.next();
			UserItemPair pair = entry.getKey();
			Double actualRating = entry.getValue();
			Double predictedRating = alg.getPrediction(pair.getUserId(), pair.getItemId());
			// store the actual rating and prediction together 
			results.put(pair, new RatingsPair(actualRating, predictedRating));			
		}
	}

	/**
	 * Evaluate the coverage of the recommender algorithm
	 * @returns the coverage (as a percentage)
	 */
	public double getCoverage()	{
		int counter = 0;
		for(RatingsPair ratings: results.values())
			if(ratings.getPredictedRating() != null)
				counter++;
		
		return (results.size() > 0) ? counter * 100.0 / results.size() : 0;
	}
	
	/**
	 * Evaluate the RMSE of the recommender algorithm
	 * @returns the root mean square error (RMSE) or null if the actual ratings are not available
	 */
	public Double getRMSE()	{
		int counter = 0;
		double squareError = 0;
		for(RatingsPair ratings: results.values()) {	
			if(ratings.getActualRating() == null) // actual ratings not available, exit loop
				break;
				
			 // a predicted rating has been computed
			if(ratings.getPredictedRating() != null) {
				squareError += Math.pow(ratings.getActualRating().doubleValue() - ratings.getPredictedRating().doubleValue(), 2);
				counter++;
			}
		}

		if(counter == 0)
			return null;
		else
			return (Math.sqrt(squareError / counter));	
	}
	
	/**
	 * @param targetRating - RMSE is computed where actual rating = target rating
	 * @returns the root mean square error (RMSE) or null if the actual ratings are not available
	 */
	public Double getRMSE(double targetRating) {
		
		int counter = 0;
		double squareError = 0;
		for(RatingsPair ratings: results.values()) {	
			if(ratings.getActualRating() == null) // actual ratings not available, exit loop
				break;
			
			if(Math.abs(ratings.getActualRating().doubleValue() - targetRating) < DELTA) {
				// a predicted rating has been computed
				if(ratings.getPredictedRating() != null) {
					squareError += Math.pow(ratings.getActualRating().doubleValue() - ratings.getPredictedRating().doubleValue(), 2);
					counter++;
				}
			}
		}

		if(counter == 0)
			return null;
		else
			return Math.sqrt(squareError / counter);	
	}
	
	/**
	 * Evaluate the MAE of the recommender algorithm
	 * @returns the mean absolute error (MAE) or null if the actual ratings are not available
	 */
	public Double getMAE() {
		int counter = 0;
		double error = 0;
		for(RatingsPair ratings: results.values()) {	
			if(ratings.getActualRating() == null) // actual ratings not available, exit loop
				break;
				
			// a predicted rating has been computed
			if(ratings.getPredictedRating() != null) {
				error += Math.abs(ratings.getActualRating().doubleValue() - ratings.getPredictedRating().doubleValue());
				counter++;
			}
		}

		if(counter == 0)
			return null;
		else
			return error / counter;	
	}
	
	/**
	 * Write the results to a file
	 * @param filename - the path and filename of the output file
	 */
	public void writeResults(final String outputFile) {
		
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(outputFile)); // open output file for writing
		
			// iterate over all predictions
			for(Iterator<Map.Entry<UserItemPair,RatingsPair>> it = results.entrySet().iterator(); it.hasNext(); ) {
				Map.Entry<UserItemPair,RatingsPair> entry = (Map.Entry<UserItemPair,RatingsPair>)it.next();
				UserItemPair pair = entry.getKey();
				RatingsPair ratings = entry.getValue();

				// a predicted rating has been computed
				if(ratings.getPredictedRating() != null) {
					pw.print(pair.toString() + " ");
					
					// print the actual rating if available
					if(ratings.getActualRating() != null) 
						pw.print(ratings.getActualRating() + " ");
					pw.println(ratings.getPredictedRating());
				}
			}
			
			pw.close(); // close output file
        }
		catch(IOException e) {
			System.out.println("Error writing to output file ...\n");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
