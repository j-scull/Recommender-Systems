package alg;

/**
 * Interface for rating prediction algorithms
 */
public interface RatingPredictionAlg {
	
	/**
	 * Get a user's the predicted rating for an item
	 * @returns the predicted rating  or null if a prediction cannot be computed
	 * @param userId - the target user ID
	 * @param itemId - the target item ID
	 */
	public Double getPrediction(final Integer userId, final Integer itemId);
}
