package alg.ub.predictor;

import alg.ub.UBCFRatingPredictionAlg;

import java.util.Set;

/**
 * A user-based prediction algorithm
 * Predicts a user's rating for an item by averaging the ratings neighbours for the item
 */
public class SimpleAveragePredictor implements UBCFPredictor {
	
	/** Constructor */
	public SimpleAveragePredictor() {
	}

	/**
	 * Predicts a rating for a user
	 * @param alg - a user-based collaborative filtering algorithm
	 * @param userId - a user's ID
	 * @param itemId - an item's ID
	 * @returns the predicted user rating for the item or null if a prediction cannot be computed
	 */
	public Double getPrediction(final UBCFRatingPredictionAlg alg, final Integer userId, final Integer itemId) {
		
		double above = 0;
		int counter = 0;

		// Get the neighbours
		Set<Integer> neighbours = alg.getNeighbourhood().getNeighbours(userId);

		// return null if the user has no neighbours
		if (neighbours == null)
			return null;
		
		// Iterate through neighbours and compute the average rating
		for(Integer neighbour: neighbours) {
			Double rating = 
					alg.getReader().getUserProfiles().get(neighbour).getValue(itemId); // get the neighbour's rating for the target item
			if(rating != null) {
				above += rating.doubleValue();
				counter++;
			}
		}
		if(counter > 0)
			return above / counter;
		else
			return null;
	}
	
	
	public String toString() {
		return "SimpleAverage";
	}
	
}
