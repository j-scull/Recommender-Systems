package alg.ub.predictor;

import alg.ub.UBCFRatingPredictionAlg;
import profile.Profile;

import java.util.Map;
import java.util.Set;


/**
 * A user-based prediction algorithm
 * An improvement upon the SimpleAveragePredictor
 * Predicts a user's rating for an item by averaging the weighted ratings neighbours for the item,
 * where neighbours are weighted by their similarity to the user.
 */
public class WeightedAveragePredictor implements UBCFPredictor {
	
	/** Constructor */
	public WeightedAveragePredictor() {
	}

	/**
	 * Predicts a rating for a user
	 * @param alg - a user-based collaborative filtering algorithm
	 * @param userId - a user's ID
	 * @param itemId - an item's ID
	 * @returns the predicted user rating for the item or null if a prediction cannot be computed
	 */
	public Double getPrediction(final UBCFRatingPredictionAlg alg, final Integer userId, final Integer itemId) {
		
		// get neighbours - these are already computed by UBCFRatingPredictionAlg alg 
		Set<Integer> neighbourhood = alg.getNeighbourhood().getNeighbours(userId);
		
		// check that user has neighbours
		if (neighbourhood == null)
			return null;
		
		// Get user-item ratings
		Map<Integer,Profile> profiles = alg.getReader().getUserProfiles();		
		double predictedRating = 0;
		int numRatings = 0;
		
		for (Integer neighbour: neighbourhood) {
				
			// get the neighbour's rating for the item
			Double itemRating = profiles.get(neighbour).getValue(itemId);
			
			// check that the item has been rated
			if (itemRating != null) {
			
				// get the user-neighbour similarity
				double sim = alg.getSimilarityMap().getSimilarity(userId, neighbour);
				// add the similarity weighted rating to the total ratings
				predictedRating += sim * itemRating;
				numRatings += 1;
			}
	
		}
		// check if any neighbours rated the item concerned
		if (numRatings == 0)
			return null;
		return predictedRating / numRatings;
	}
}
