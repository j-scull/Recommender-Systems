package alg.rerank;

import alg.np.similarity.SimilarityMap;
import profile.Profile;

import util.ScoredThingDsc;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Recommendation re-ranking algorithm.
 * Re-ranks a set of baseline recommendations according to item set diversity
 * e.g. results for nusers = 100
 *  
 *  L    N   Div      Prec     Recall
 *	0.0, 10, 0.14968, 0.01515, 0.01666
 *	0.1, 10, 0.23679, 0.01212, 0.01334
 *	0.2, 10, 0.29279, 0.01212, 0.01451
 *	0.3, 10, 0.39726, 0.01010, 0.01217
 *	0.4, 10, 0.54172, 0.00909, 0.01157
 *	0.5, 10, 0.71089, 0.00303, 0.00415
 *	0.6, 10, 0.88347, 0.00505, 0.00785
 *	0.7, 10, 0.96487, 0.00202, 0.00328
 *	0.8, 10, 0.98889, 0.00202, 0.00328
 *	0.9, 10, 0.99201, 0.00202, 0.00328
 *	1.0, 10, 0.98913, 0.00404, 0.00558
 *
 */
public class DiversityReranker implements Reranker {
	
	SimilarityMap distanceMap;
	double lambda;  
	
	/**
	 * Constructor
	 * @param distanceMap - a matrix of normalised item-item distances
	 * @param lambda - determines the tradeoff between accuracy and diversity when re-ranking items
	 */
	public DiversityReranker(SimilarityMap distanceMap, double lambda) {
		this.distanceMap = distanceMap;
		this.lambda = lambda;
	}
	
	/**
	 * Re-ranks the items recommended for a user in order to maximise diversity according lambda
	 * This method is called in the RerankingRecommender class by getRecommendationsgetRecommendations
	 * @param userProfile - a user profile for a given user
	 * @param scores - a profile of scores <itemId,score> given by the baseline recommender for the user
	 */
	public List<Integer> rerank(Profile userProfile, Profile scores) {
		
		// create a list to store recommendations
		List<Integer> recs = new ArrayList<Integer>();
		if (scores == null)
			return recs;
		
		// Create a candidate set c
		HashSet<Integer> c = new HashSet<>();
		
		// store all scores in descending order in a sorted set
		SortedSet<ScoredThingDsc> ss = new TreeSet<ScoredThingDsc>(); 
		
		// Add ids to candidate list and get the maximum scored item 
		for (Integer itemId: scores.getIds()) {
			// don't include items already in the user's profile
			if (!userProfile.contains(itemId)) {
				c.add(itemId);
				ss.add(new ScoredThingDsc(scores.getValue(itemId), itemId));
			}
		}
		// add item with maximum score to recs and remove from c
		int maxScoreItem = (Integer) ss.first().thing;
		recs.add(maxScoreItem);
		c.remove(maxScoreItem);
		
		// Update novelty score for items in c relative to recs during each iteration
		HashMap<Integer,Double> novelties = new HashMap<>();
		
		// store the total score and total squared score for all items
		double totalScore = 0;
		double es = 0;
		// no need to iterate over all item scores again - can subtract selected item's score instead
		for (Integer candidateItem: c) {
			// sum total score and total squared score
			double itemScore =  scores.getValue(candidateItem);
			totalScore += itemScore;
			es += itemScore*itemScore;
		}
		
		// add all items to recs - the top N is taken at a later stage
		while (c.size() > 0) {
			
			// Reuse to access scores in order
			ss = new TreeSet<ScoredThingDsc>(); 
			
			// store the novelty and squared novelty for items in c - needs to be recalculated each iteration
			double totalNovelty = 0;
			double en = 0;
			
			for (Integer candidateItem: c) {
			
				// compute novelty (sum distance) of each item in c against all items in recs
				// each iteration only need to compute the novelty compared to last item inserted in recs
				int lastRecsItem = recs.get(recs.size()-1);	
				double itemNovelty = distanceMap.getSimilarity(candidateItem, lastRecsItem);
				// add the sum of already computed distances with other items in recs
				itemNovelty += novelties.getOrDefault(candidateItem, 0.0);
				novelties.put(candidateItem, itemNovelty);
				// sum total novelty and total squared novelty
				totalNovelty += itemNovelty;
				en += itemNovelty*itemNovelty;	
			}
			
			// calculate the mean score and novelty for items in c
			double meanScore = totalScore / c.size();
			double meanNovelty = totalNovelty /c.size();
			// calculate the variance for score and novelty in c
			double vs = es/c.size() - meanScore*meanScore;
			double vn = en/c.size() - meanNovelty*meanNovelty;
			
			// Calculate relevance-diversity score for each item
			for (Integer candidateItem: c) {
				
				// normalize score and novelty
				double normalizedScore = (scores.getValue(candidateItem) - meanScore) / Math.sqrt(vs);
				double normalizedNovelty = (novelties.get(candidateItem) - meanNovelty) / Math.sqrt(vn);
				
				// add tradeoff score to sorted set
				ss.add(new ScoredThingDsc((1-lambda)*normalizedScore + lambda*normalizedNovelty, candidateItem));
			
			}
			
			// get the highest scored item and add to recs
			maxScoreItem = (Integer) ss.first().thing;
			recs.add(maxScoreItem);
			c.remove(maxScoreItem);
			novelties.remove(maxScoreItem);
			
			// reduce totalScore
			double maxScore = ss.first().score;
			totalScore -= maxScore;
			es -= maxScore*maxScore;
			
		}
		
		return recs;
	}
}