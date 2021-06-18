package alg.np;

import alg.RatingPredictionAlg;
import util.reader.DatasetReader;
import profile.Profile;

/**
 * Baseline recommender - simply predicts the average rating for an item
 */
public class ItemMeanPredictionAlg implements RatingPredictionAlg {
	
	private DatasetReader reader;
	private Profile meanItemScores;
	
	/**
	 * Constructor
	 * @param reader - the data set reader
	 */
	public ItemMeanPredictionAlg(final DatasetReader reader) {
		this.reader = reader;
		this.meanItemScores = new Profile(0);	
		for (Integer itemId : reader.getItemIds()) {
			Profile p = reader.getItemProfiles().get(itemId);
			meanItemScores.addValue(itemId, p.getMeanValue());			
		}
	}
	
	
	/**
	 * Predicts the average rating for the item in the dataset
	 * @param userId - a user's ID (this is a dummy parameter here)
	 * @param itemIs - the ID of the item to predict a rating for
	 */
	public Double getPrediction(final Integer userId, final Integer itemId) {
		return meanItemScores.getValue(itemId);
	}
}
