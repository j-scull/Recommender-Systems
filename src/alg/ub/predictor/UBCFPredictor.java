package alg.ub.predictor;

import alg.ub.UBCFRatingPredictionAlg;

/**
 * An interface for user-based collaborative filtering algorithms
 */
public interface UBCFPredictor {
	
	/**
	 * @param alg - a user-based CF rating prediction algorithm
	 * @param userId - a user's ID
	 * @param itemId - an item's ID
	 * @returns the predicted user rating for the item
	 */
	public Double getPrediction(final UBCFRatingPredictionAlg alg, final Integer userId, final Integer itemId);
}
