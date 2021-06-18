package util.np.evaluator;

import java.util.List;

import alg.np.similarity.SimilarityMap;
import profile.Profile;

/**
 * Evaluates the recommendation set diversity of a recommender algorithm 
 */
public class Diversity implements TestPerfInterface {
	
	private SimilarityMap distanceMap;
	
	/**
	 * Constructor
	 * @param distanceMap - a map containing item-item distance
	 */
	public Diversity(SimilarityMap distanceMap)	{
		this.distanceMap = distanceMap;
	}
	
	
	/**
	 * Get the diversity score for the recommendations given to a user
	 * @param userId - a user's ID
	 * @param testProfile - the test data with actual ratings
	 * @param recs - a list of recommendations
	 * @param k - the number of top recommendations to be considered for evaluation
	 * @return the diversity (average item-item distance) in the recommendation set 
	 */
	public Double testperf(Integer userId, Profile testProfile, List<Integer> recs, Integer k) {
		
		Integer numRecItems = 0;
		Double diversity = 0.0;
		for (Integer itemId1 : recs) {
			
			int nrecs = 0;
			for (Integer itemId2 : recs) {
				if (itemId2 != itemId1) {
					double d = distanceMap.getSimilarity(itemId1, itemId2);
					diversity += d ;
				}
				nrecs++;
				if (nrecs == k)
					break;
			}
			numRecItems ++;
			if (numRecItems == k)
				break;
		}
		diversity = diversity/(k*(k-1));
		return diversity;
	}
	
	
}
