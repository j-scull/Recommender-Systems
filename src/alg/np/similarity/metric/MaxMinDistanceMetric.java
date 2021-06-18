package alg.np.similarity.metric;

import alg.np.similarity.SimilarityMap;
import profile.Profile;

/**
 * Converts similarity scores to distance scores in range [0,1] using min-max normalisation
 */
public class MaxMinDistanceMetric implements SimilarityMetric {
	
	private SimilarityMetric simMetric;
	private Double minSim;
	private Double maxSim;
	
	/**
	 * Default constructor - assumes min and max similarities are 0 and 1 respectively
	 * @param simMetric - the similarity metric used to compute similarities
	 */
	public MaxMinDistanceMetric(SimilarityMetric simMetric) {
		this.simMetric = simMetric;
		this.minSim = 0.0;
		this.maxSim = 1.0;
	}
	
	/**
	 * Constructor with input min and max similarities
	 * @param simMetric - the similarity metric used to compute similarities
	 * @param max - the maximum possible similarity score
	 * @param min - the minimum possible similarity score
	 */
	public MaxMinDistanceMetric(SimilarityMetric simMetric, double max, double min) {
		this.simMetric = simMetric;
		this.minSim = min;
		this.maxSim = max;
	}
	
	/**
	 * Constructor that computes minSim and maxSim given a similarity map
	 * @param simMetric - the similarity metric used to compute similarities
	 * @param simMap - a matrix of pairwise similarities
	 */
	public MaxMinDistanceMetric(SimilarityMetric simMetric, SimilarityMap simMap) {
		this.simMetric = simMetric;
		computeMaxMin(simMap);
	}
	
	/**
	 * Converts a similarity score to distance in range [0,1] using min-max normalisation
	 */
	public double getItemSimilarity(final Integer X, final Integer Y) {
		if (maxSim == minSim)
			return 0;
		double s = simMetric.getItemSimilarity(X, Y);
		s = (maxSim - s) / (maxSim - minSim);
		return s;
	}


	/**
	 * Computes the minSim and maxSim given a similarity map 
	 * @param simMap - a matrix of pairwise similarities
	 */
	private void computeMaxMin(SimilarityMap simMap) {
		minSim = Double.POSITIVE_INFINITY;
		maxSim = Double.NEGATIVE_INFINITY;
		int numSims=0;
		for (Integer id: simMap.getIds()) {
			Profile p = simMap.getSimilarities(id);
			numSims++;
			for (Integer id2 : p.getIds()) {
				Double s = p.getValue(id2);
				if (s > maxSim)
					maxSim = s;
				if (s < minSim)
					minSim = s;
			}
		}
		int n = simMap.getIds().size();
		if (numSims < n * (n-1))
			minSim = 0.0;
	}

	
}
