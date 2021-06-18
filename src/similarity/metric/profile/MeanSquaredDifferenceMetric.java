package similarity.metric.profile;

import profile.Profile;

/**
 * Computes the Mean Squared Difference similarity between profiles.
 */
public class MeanSquaredDifferenceMetric implements ProfileSimilarityMetric{

	private double a;
	private double b;
	
	/**
	 * Constructor
	 * @param a the minimum rating in the dataset
	 * @param b the maximum rating in the dataset
	 */
	public MeanSquaredDifferenceMetric(double a, double b) {
		this.a = a;
		this.b = b;
	}
		
	/**
	 * Computes the similarity between profiles
	 * @param profile 1 - a Profile
	 * @param profile 2 - a Profile
	 */
	public double getSimilarity(final Profile p1, final Profile p2) {
		
		double denom = 0;
		double divis = 0;
	
		// Calculate the Mean Squared Difference
		for (Integer id: p1.getIds()) {
			if (p2.contains(id)) {
				denom += Math.pow(p1.getValue(id) - p2.getValue(id), 2);
				divis += 1;
			}
		}
		
		// if there is no item overlap return a similarity of 0
		if (divis == 0)
			return 0;
		
		double msd = denom / divis;
		// Return the similarity
        return 1 - (msd / Math.pow(b - a, 2));
	}
	
	/** String representaiont */
	public String toString() {
		return "MeanSquaredDifference";
	}
	
}
