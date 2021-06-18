package alg.np.similarity.metric;

/**
 * An interface for computing the similarity between two items
 */
public interface SimilarityMetric {
	
	/**
	 * Compute the similarity between items
	 * @param X - an item's ID 
	 * @param Y - an item's ID
	 */
	public double getItemSimilarity(final Integer X, final Integer Y);
}
