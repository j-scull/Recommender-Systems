package alg.ub.predictor;

import alg.ub.UBCFRatingPredictionAlg;
import profile.Profile;

import java.util.Map;
import java.util.Set;

/**
 * Computes the target users predicted rating using the deviation from user-mean approach
 * i.e. Resnick's algorithm
 */
public class DeviationFromUserMeanPredictor implements UBCFPredictor {
	
	/** Constructor */	
	public DeviationFromUserMeanPredictor() {	
	}

	/**
	 * Predicts a user's rating for an item - Resnick's algorithm
	 * @param alg - a user-based collaborative filtering algorithm
	 * @param userId - a user's ID
	 * @param itemId - an item's ID
	 * @returns the predicted user rating for the item or null if a prediction cannot be computed
	 */
	public Double getPrediction(final UBCFRatingPredictionAlg alg, final Integer userId, final Integer itemId){
		
		// get neighbours - these are already computed by UBCFRatingPredictionAlg alg 
		Set<Integer> neighbourhood = alg.getNeighbourhood().getNeighbours(userId);
		//System.out.println(neighbourhood);
		
		// check that user has neighbours
		if (neighbourhood == null)
			return null;
		
		// Get user item ratings
		Map<Integer,Profile> profiles = alg.getReader().getUserProfiles();		
		
		// get the user's average rating
		double userMeanRating = profiles.get(userId).getMeanValue();
		double predictedRating = 0;
		double sumOfSimilarities = 0;
		
		// Get the user's neighbours' ratings
		for (Integer neighbour: neighbourhood) {
				
			// get the neighbour's rating for the item
			Double itemRating = profiles.get(neighbour).getValue(itemId);
				
			// check that the item has been rated
			if (itemRating != null) {
				
				// get the neighbour's mean rating for items
				double neighbourMeanRating = profiles.get(neighbour).getMeanValue();
				
				// get the user-neighbour similarity
				double sim = alg.getSimilarityMap().getSimilarity(userId, neighbour);
				
				predictedRating += sim * (itemRating - neighbourMeanRating);
				sumOfSimilarities += Math.abs(sim);
			}
		}
		// check if any neighbours rated the item concerned
		if (sumOfSimilarities == 0)
			return null;
		else 
			return userMeanRating + predictedRating / sumOfSimilarities;
	}
	
	public String toString() {
		return "DeviationFromUserMean";
	}
	
	
	
	
}
