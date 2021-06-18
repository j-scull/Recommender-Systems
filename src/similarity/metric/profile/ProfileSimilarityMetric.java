package similarity.metric.profile;

import profile.Profile;

/** An interface to compute the similarity between profiles */
public interface ProfileSimilarityMetric {
	
	/**
	 * @returns the similarity between two profiles
	 * @param p1
	 * @param p2
	 */
	public double getSimilarity(final Profile p1, final Profile p2);
}
