package similarity.metric.profile;

import profile.Profile;

import java.util.Set;

/**
 * Compute the Cosine similarity between profiles.
 */
public class CosineMetric implements ProfileSimilarityMetric {
	
	/** Constructor - creates a new CosineMetric object */
	public CosineMetric() {
	}
	
	/**
	 * Computes the similarity between profiles
	 * @param profile 1 - a Profile 
	 * @param profile 2 - a Profile
	 * @return the similarity score for the two profiles
	 */
	public double getSimilarity(final Profile p1, final Profile p2)	{
        double dotProduct = 0;
        
        Set<Integer> common = p1.getCommonIds(p2);
		for(Integer id: common)	{
			double r1 = p1.getValue(id).doubleValue();
			double r2 = p2.getValue(id).doubleValue();
			dotProduct += r1 * r2;
		}

		double n1 = p1.getNorm();
		double n2 = p2.getNorm();
		return (n1 > 0 && n2 > 0) ? dotProduct / (n1 * n2) : 0;
	}
	
	/** String representation */
	public String toString() {
		return "Cosine";
	}
	
	
}
