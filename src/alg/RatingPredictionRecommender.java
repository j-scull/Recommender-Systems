package alg;

import profile.Profile;
import util.reader.DatasetReader;

import java.util.List;
import java.util.Set;
import java.util.Map;


/**
 * Rating prediction algorithm - implements top-N recommendation.
 * Makes rating predictions for a given user for items, then recommends the items with the highest predicted rating. 
 */
public class RatingPredictionRecommender extends Recommender {
	
	private RatingPredictionAlg predictionAlg;
	private Set<Integer> candidates;
	private Map<Integer, Profile> userProfiles;
	
	/**
	 * Constructor
	 * @param reader - the data set reader
	 * @param predictionAlg - the algorithm that makes the rating predictions
	 * @param candidates - the set of items to be considered for recommendation
	 */
	public RatingPredictionRecommender(DatasetReader reader, RatingPredictionAlg predictionAlg, Set<Integer> candidates) {
		super(reader);
		this.predictionAlg = predictionAlg;
		this.candidates = candidates;
		this.userProfiles = reader.getUserProfiles();
	}
	
	/**
	 * Constructor - default uses all items in data set for recommendation
	 * @param reader - the data set reader
	 * @param predictionAlg - the algorithm that makes the rating predictions
	 */
	public RatingPredictionRecommender(DatasetReader reader, RatingPredictionAlg predictionAlg) {
		super(reader);
		this.predictionAlg = predictionAlg;
		this.candidates = reader.getItemIds();
		this.userProfiles = reader.getUserProfiles();
	}
	
	/**
	 * Gets predicted items rating score for a given user
	 * @param userId - a user's id
	 * @return a Profile of predicted item ratings for the user
	 */
	public Profile getRecommendationScores(Integer userId) {
		Profile scores = new Profile(0);
		
		// get a predicted rating for all candidate items
		for (Integer id : candidates) {
			Double r = predictionAlg.getPrediction(userId, id);
			if (r != null)
				scores.addValue(id, r);
		}
		return scores;
	}
	
	/**
	 * Gets recommended items for a given user sorted in order of predicted rating
	 * @param userId - a user's id
	 * @return a list of itemIds - the sorted recommendations
	 */
	public List<Integer>  getRecommendations(Integer userId) {
		Profile scores = getRecommendationScores(userId);
		Profile userProfile = userProfiles.get(userId);
		
		// method is defined in abstract Recommender class
		return getRecommendationsFromScores(userProfile,scores);   
	}
}
