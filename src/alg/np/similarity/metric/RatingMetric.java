package alg.np.similarity.metric;

import util.reader.DatasetReader;

/**
 * Compute the similarity between two items
 */ 
public class RatingMetric implements SimilarityMetric {
	
	private DatasetReader reader; // dataset reader

	/**
	 * Constructor
	 * @param reader - data set reader
	 */
	public RatingMetric(final DatasetReader reader) {
		this.reader = reader;
	}

	/**
	 * Computes the similarity between items
	 * @param X - an item's ID
	 * @param Y - an item's ID
	 */
	public double getItemSimilarity(final Integer X, final Integer Y) {
		return 0;
	}
}
