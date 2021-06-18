package alg;

import alg.rerank.Reranker;
import profile.Profile;
import alg.Recommender;
import util.reader.DatasetReader;

import java.util.List;


/**
 * Re-ranks a baseline set of recommended items according to a criteria other than predicted rating
 * i.e. recommendation set diversity
 */
public class RerankingRecommender implements RecAlg {
	
	private DatasetReader reader;
	private Reranker reranker;
	private Recommender baselineRecommender;
	
	/**
	 * Constructor
	 * @param reader - the data set reader
	 * @param baselineRecommender - a recommender algorithm
	 * @param reranker - the algorithm that re-ranks the recommended set of items
	 */
	public RerankingRecommender(DatasetReader reader, Recommender baselineRecommender, Reranker reranker) {
		this.reader = reader;
		this.reranker = reranker;
		this.baselineRecommender = baselineRecommender;
	}
	
	/**
	 * Get recommendations using the re-ranking algorithm
	 * @param userId - a user's id
	 * @return a list of recommended items ordered by the baseline recommender and re-ranker algorithm
	 */
	public List<Integer> getRecommendations(final Integer userId) {	
		Profile userProfile = reader.getUserProfiles().get(userId);
		return reranker.rerank(userProfile, baselineRecommender.getRecommendationScores(userId));	
	}
	
}
