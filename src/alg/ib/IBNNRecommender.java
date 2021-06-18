package alg.ib;

import alg.Recommender;
import alg.np.similarity.SimilarityMap;
import profile.Profile;
import util.reader.DatasetReader;
import neighbourhood.Neighbourhood;

import java.util.List;
import java.util.Set;

/**
 * Item-based Nearest Neighbour algorithm
 * Recommends items that are similar to items already in a user's profile
 */
public class IBNNRecommender extends Recommender {
	
	private Neighbourhood neighbourhood; 
	private SimilarityMap simMap;

	/**
	 * Constructor
	 * @param reader - the data set reader
	 * @param neighbourhood - the neighbourhood formation algorithm to be used
	 * @param simMap - the pairwise similarities between all itemss
	 */
	public IBNNRecommender(final DatasetReader reader, final Neighbourhood neighbourhood, final SimilarityMap simMap) {
		super(reader);
		this.neighbourhood = neighbourhood;
		this.simMap = simMap;
		this.neighbourhood.computeNeighbourhoods(simMap);
			
	}
	
	/**
	 * Scores all items for the given user based on their similarity to items in the user's profile 
	 * An items score is the sum of similarities to all items in the user's profile
	 * @param userId - a user's id
	 * @return a Profile of scores <itemId, score>
	 */
	public Profile getRecommendationScores(final Integer userId) {
		
		Profile userProfile = reader.getUserProfiles().get(userId);
		Set<Integer> itemSet = reader.getItemIds();
		Profile scores = new Profile(userId);
		
		for (Integer simId : itemSet) {
			double s = 0.0;
			for (Integer profId: userProfile.getIds()){
				if (neighbourhood.isNeighbour(simId,profId)) {
					Double sim = simMap.getSimilarity(simId, profId);
					if (sim != null)
						s += sim;
				}
			}
			scores.addValue(simId,s);
		}
		return scores;
	}

	/**
	 * Gets a list of recommended items for the given user - ordered by score, descending order
	 * @param userId - a user's id
	 * @return a list of itemIds - the recommendations
	 */
	public List<Integer> getRecommendations(final Integer userId){	
		
		Profile userProfile = reader.getUserProfiles().get(userId);
		Profile scores = getRecommendationScores(userId);
		
		return getRecommendationsFromScores(userProfile,scores);	
	}
}
