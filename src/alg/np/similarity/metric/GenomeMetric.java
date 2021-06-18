package alg.np.similarity.metric;

import util.reader.DatasetReader;
import profile.Profile;

/**
 * Measures similarity based genomes - machine learning generated semantic content descriptors for the MovieLens dataset. 
 * All items a scored by how much they relate to each genome, with scores in range [0, 1]. There are 1128 genomes in total. 
 * For two items with associated sets genome scores A and B, the genome similarity is given by the weighted Jaccard index:
 */
public class GenomeMetric implements SimilarityMetric {
	
	private DatasetReader reader; // dataset reader
	
	/**
	 * Constructor
	 * @param reader - the dataset reader
	 */
	public GenomeMetric(final DatasetReader reader) {
		this.reader = reader;
	}
	
	/**
	 * Computes the similarity between items
	 * @param X - the id of the first item 
	 * @param Y - the id of the second item
	 */
	public double getItemSimilarity(final Integer X, final Integer Y) {
		
		// Get the genome scores for the two items
		Profile x = reader.getItem(X).getGenomeScores();
		Profile y = reader.getItem(Y).getGenomeScores();
		
		// calculate similarity using weighted Jaccard
		double denom = 0;
		double divis = 0;
		for (Integer i : x.getIds()) {
			double xi = x.getValue(i);
			double yi = y.getValue(i);
			if (xi <= yi) {
				denom += xi;
				divis += yi;
			} else {
				denom += yi;
				divis += xi;
			}
		}
		return denom / divis;
	}
	
	public String toString() {
		return "Genome";
	}
}
