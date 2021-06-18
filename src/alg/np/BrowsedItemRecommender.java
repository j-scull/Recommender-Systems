package alg.np;

import alg.Recommender;
import alg.np.similarity.SimilarityMap;
import alg.np.similarity.metric.SimilarityMetric;
import profile.Profile;
import util.reader.DatasetReader;

import java.util.List;
import java.util.ArrayList;


/**
 * Content-based Recommender - recommends items similar an item already interacted with by a user
 */
public class BrowsedItemRecommender  extends Recommender {
	
	private SimilarityMap simMap; // similarity map - stores all item-item similarities
	private Integer itemId; 
	private Profile scores;

	/**
	 * Constructor with SimilarityMetric
	 * @param reader - the data set reader
	 * @param itemId - the browsed item's ID
	 * @param metric - the similarity metric used to compute item-item similarity
	 */
	public BrowsedItemRecommender(final DatasetReader reader, final Integer itemId, final SimilarityMetric metric) {
		super(reader);
		this.simMap = new SimilarityMap(reader, metric);
		if (this.simMap==null) {
			System.out.println("Null similarity map");
		}
		setBrowsedItem(itemId);
	}
	
	/**
	 * Constructor with SimilarityMap
	 * @param reader - the data set reader
	 * @param itemId - the browsed item's ID
	 * @param simMap - a matrix of item-item similarities
	 */
	public BrowsedItemRecommender(final DatasetReader reader, final Integer itemId,final SimilarityMap simMap) {
		super(reader);
		this.simMap = simMap;
		setBrowsedItem(itemId);
	}
	
	/**
	 * Resets the browsed items
	 * @param id - the browsed item's ID
	 */
	public void setBrowsedItem(Integer id){
		this.itemId = id;
		setScores();
	}
	
	/**
	 * Calculates the similarity scores for the browsed item
	 */
	private void setScores(){	
		scores = simMap.getSimilarities(itemId); 	
	}
	
	/**
	 * Get the predicted ratings for a given user for all items
	 * @param userId - a user's id
	 * @returns a Profile containing item ratings <itemId, rating>
	 */
	public Profile getRecommendationScores(final Integer userId){	
		return scores;
	}

	/**
	 * Gets recommended items for a given user sorted in order of predicted rating
	 * @param userId - a user's id
	 * @returns a list of itemIds - the sorted recommendations
	 */
	public List<Integer> getRecommendations(final Integer userId) {	
		Profile userProfile = reader.getUserProfiles().get(userId);
		if (scores==null || scores.getIds().size()==0)
			return new ArrayList<Integer>();
		return getRecommendationsFromScores(userProfile,scores);
	}
}
