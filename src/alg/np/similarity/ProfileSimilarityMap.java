package alg.np.similarity;

import profile.Profile;
import similarity.metric.profile.ProfileSimilarityMetric;

import java.util.Map;

/**
 * Computes the pairwise similarities between Profiles  
 *
 */
public class ProfileSimilarityMap extends SimilarityMap {
	
	/** Constructor - creates a new SimilarityMap object */
	public ProfileSimilarityMap() {
		super();
	}
	
	/**
	 * Constructor
	 * @param profileMap
	 * @param metric
	 */
	public ProfileSimilarityMap(final Map<Integer,Profile> profileMap, final ProfileSimilarityMetric metric) {
		super();
		
		// compute pairwise similarities between profiles
		for(Integer id1: profileMap.keySet()) {
			//System.out.println(id1);
			for(Integer id2: profileMap.keySet()) {
				if(id2 < id1)
				{
					//System.out.println(id1 + " : " + id2);
					double sim = metric.getSimilarity(profileMap.get(id1), profileMap.get(id2));
					if(sim > 0) {
						setSimilarity(id1, id2, sim);
						setSimilarity(id2, id1, sim);
					}
				}
			}
		}
	}
}
