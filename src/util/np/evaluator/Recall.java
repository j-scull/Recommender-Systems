package util.np.evaluator;

import profile.Profile;

import java.util.List;

/**
 * Evaluates the precision of a recommender algorithm
 * i.e. the percentage of items rated positive by a user that get recommended by the algorithm 
 */
public class Recall implements TestPerfInterface {
	
	private Double threshold;
	
	/**
	 * Constructor
	 * @param threshold - the threshold at which to consider a rating as positive
	 */
	public Recall(Double threshold) {
		this.threshold = threshold;
	}
	
	/**
	 * Constructor with default threshold
	 */
	public Recall() {
		this.threshold = 1.0;
	}
	
	/**
	 * Get the recall score for the recommendations given to a user
	 * @param userId - a user's ID
	 * @param testProfile - the test data with actual ratings
	 * @param recs - a list of recommendations
	 * @param k - the number of top recommendations to be considered for evaluation
	 * @return the recall score - the percentage of items rated positive by a user that get recommended by the algorithm 
	 */
	public Double testperf(Integer userId, Profile testProfile, List<Integer> recs, Integer k) {
		
		Double recall = 0.0;
		Integer numTestItems = 0;
		
		for (Integer testItem : testProfile.getIds()) {
			Double val = testProfile.getValue(testItem);
			if (val >= threshold)
				numTestItems++;
		}
		// Indicate that no test items were available by returning null
		if (numTestItems == 0)
			return null;
		
		Integer numRecItems = 0;
		for (Integer itemId : recs) {
			Double val = testProfile.getValue(itemId);
			if (val != null) {
				if (val >= threshold)
					recall = recall + 1.0;	
			}
			numRecItems++;
			if (numRecItems == k)
				break;
		}
		recall = recall / numTestItems;
		
		return recall;
	}
	
	
}
