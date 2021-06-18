package alg;

import java.util.List;

/**
 * Interface for recommender algorithms
 */
public interface RecAlg {

	/**
	 * Gets recommendations for a user
	 * Recommendations are in descending order and omit any items already in the users profile 
	 * @param userId - the user the recommendations
	 * @return a list of itemIds - the recommendations in descending order
	 */
	List<Integer>  getRecommendations(Integer userId);
}