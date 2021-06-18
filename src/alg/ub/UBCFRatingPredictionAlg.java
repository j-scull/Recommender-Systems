package alg.ub;

import alg.RatingPredictionAlg;
import alg.ub.predictor.UBCFPredictor;
import alg.np.similarity.*;
import neighbourhood.Neighbourhood;
import similarity.metric.profile.ProfileSimilarityMetric;
import util.reader.DatasetReader;

/**
 * Implements user-based collaborative filtering algorithms
 */
public class UBCFRatingPredictionAlg implements RatingPredictionAlg {
	
	private UBCFPredictor predictor; // the predictor technique  
	private Neighbourhood neighbourhood; // the neighbourhood technique
	private DatasetReader reader; // dataset reader
	private SimilarityMap simMap; // similarity map - stores all user-user similarities
	
	/**
	 * Constructor with similarity metric
	 * @param predictor - the prediction algorithm
	 * @param neighbourhood - the neighbourhood formation algorithm
	 * @param metric - the user similarity metric
	 * @param reader - the dataset reader
	 */
	public UBCFRatingPredictionAlg(final UBCFPredictor predictor, final Neighbourhood neighbourhood, final ProfileSimilarityMetric metric, final DatasetReader reader) {
		this.predictor = predictor;
		this.neighbourhood = neighbourhood;
		this.reader = reader;
		this.simMap = new ProfileSimilarityMap(reader.getUserProfiles(), metric); // compute all user-user similarities
		this.neighbourhood.computeNeighbourhoods(simMap); // compute the neighbourhoods for all users
	}
	
	/**
	 * Constructor with simMap
	 * @param predictor - the prediction algorithm
	 * @param neighbourhood - the neighbourhood formation algorithm
	 * @param simMap - a matrix of pairwise similarities
	 * @param reader - the dataset reader
	 */
	public UBCFRatingPredictionAlg(final UBCFPredictor predictor, final Neighbourhood neighbourhood, final SimilarityMap simMap, final DatasetReader reader) {
		this.predictor = predictor;
		this.neighbourhood = neighbourhood;
		this.reader = reader;
		this.simMap = simMap; // compute all user-user similarities
		this.neighbourhood.computeNeighbourhoods(simMap); // compute the neighbourhoods for all users
	}
	
	/**
	 * @param userId - a user's ID
	 * @param itemId - an item's ID
	 * @returns the predicted user rating for the item
	 */
	public Double getPrediction(final Integer userId, final Integer itemId) {	
		return predictor.getPrediction(this, userId, itemId);
	}

	
	/*========================Getters==============================*/
	
	public Neighbourhood getNeighbourhood() { return neighbourhood; }
	
	public SimilarityMap getSimilarityMap() { return simMap; }
	
	public DatasetReader getReader() {	return reader;	}
	

}