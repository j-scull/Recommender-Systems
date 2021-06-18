package util.np.evaluator;

import profile.Profile;

import java.util.List;

/**
 * Interface for prediction evaluation metrics
 */
public interface TestPerfInterface {
	
	/**
	 * 
	 * @param userId
	 * @param testProfile
	 * @param recs
	 * @param k
	 * @return
	 */
	public Double testperf(Integer userId, Profile testProfile, List<Integer> recs, Integer k);
}
