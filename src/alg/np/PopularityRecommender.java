package alg.np;

import alg.Recommender;
import profile.Profile;
import util.reader.DatasetReader;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Popularity recommender - uses the Wilson score to recommend items according to their popularity
 */
public class PopularityRecommender extends Recommender {
	
	// threshold above which a rating is considered to be an upvote
	private Double ratingThreshold; 
	// significance level for Wilson score
	private Double significanceLevel;
	// a profile of scores used to sort the items for recommendation
	private Profile scores;

	/**
	 * Constructor
	 * @param reader - the dataset reader
	 * @param ratingThreshold 
	 * @param significanceLevel
	 */
	public PopularityRecommender(final DatasetReader reader, double ratingThreshold, double significanceLevel) {
		super(reader);
		this.ratingThreshold = ratingThreshold;
		this.significanceLevel = significanceLevel;	
		setScores();
	}
	
	/**
	 * Constructor
	 * @param reader - the data set reader
	 * @param ratingThreshold - the threshold at which to consider a rating as positive
	 */
	public PopularityRecommender(final DatasetReader reader, double ratingThreshold) {
		super(reader);
		this.ratingThreshold = ratingThreshold;
		this.significanceLevel = 1.0;
		setScores();	
	}
	
	/**
	 * Resets the significance level and recalculates scores
	 * @param level - the new significance level
	 */
	public void setSignificanceLevel(double level) {
		this.significanceLevel = level;
		setScores();
	}
	
	/**
	 * Resets the rating threshold and recalculates scores
	 * @param threshold - the threshold at which to consider a rating as positive
	 */
	public void setRatingThreshold(double threshold) {
		this.ratingThreshold = threshold;
		setScores();
	}
	
	/**
	 * Scores all items using ratings from users
	 * Uses the Wilson Score to give a lower bound score for items
	 */
	private void setScores() {
		
		scores = new Profile(0);
		
		// Get all the items in the dataset
		Set<Integer> itemIds = reader.getItems().keySet();
		Map<Integer, Profile> itemProfiles = reader.getItemProfiles();
		
		// Calculate z given a significance level
		NormalDistribution nd = new NormalDistribution();
		double z = - nd.inverseCumulativeProbability(significanceLevel/2);
		double N, phat, wilson_score;
		Profile p;
		int ups;
		
		// Iterate through each item in the system
		for (Integer id: itemIds) {
			p = itemProfiles.get(id);
			N = p.getSize();
			ups = 0;
			
			// Iterate through all scores for each item - get the number of positive ratings
			for (Integer userId: p.getIds()) 
				if (p.getValue(userId) >= ratingThreshold) ups++;
			
			// calculate phat - the observed fraction of positive ratings
			phat = ups / N;
			// calculate the wilson score
			wilson_score = (phat + z*z / (2*N) - z * Math.sqrt((phat *(1 - phat) + z*z / (4*N)) / N )) / (1 + z*z / N);
			// add an item and its wilson score to the scores profile
			scores.addValue(id, wilson_score);
		}
	}
	
	/**
	 * Get the recommendation scores
	 * @param userId - a user's ID
	 * @return a Profile containing item ratings <itemId, rating>
	 */
	public Profile getRecommendationScores(Integer userId) {
		return scores;
	}

	/**
	 * Gets recommended items for a given user sorted in order of predicted rating
	 * @param userId - a user's id
	 * @return a list of itemIds - the sorted recommendations
	 */
	public List<Integer> getRecommendations(Integer userId) {		
		Profile userProfile = reader.getUserProfiles().get(userId);
		return getRecommendationsFromScores(userProfile,scores);
	}
	
	
	
}


