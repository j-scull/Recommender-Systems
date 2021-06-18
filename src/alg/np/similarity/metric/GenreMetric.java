package alg.np.similarity.metric;

import util.reader.DatasetReader;

import java.util.Set;

/**
 * Measures the similarity between two items  by how many genres they have in common 
 * over the total number genres across both items. This uses the Jaccard Index.
 */
public class GenreMetric implements SimilarityMetric {	
	
	private DatasetReader reader; // dataset reader
	
	/**
	 * Constructor
	 * @param reader - the dataset reader
	 */
	public GenreMetric(final DatasetReader reader) {
		this.reader = reader;
	}
	
	/**
	 * Computes the similarity between items
	 * @param X - the id of the first item 
	 * @param Y - the id of the second item
	 */
	public double getItemSimilarity(final Integer X, final Integer Y) {
		
		// Get the genres for both items
		Set<String> genresX = reader.getItem(X).getGenres();
		Set<String> genresY = reader.getItem(Y).getGenres();
		
		// Get the number of common genres
		int count = 0;
		for (String s: genresX)
			if (genresY.contains(s))
				count++;
		
		// Calculate the Jaccard index - return 0 if division by zero occurs 
		int denom = Math.min(genresX.size(), genresY.size());
		return (denom > 0 ) ? count * 1.0 / denom : 0;
	}
	
	
	public String toString() {
		return "Genre";
	}
}
