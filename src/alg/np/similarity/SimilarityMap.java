package alg.np.similarity;

import profile.Profile;
import alg.np.similarity.metric.SimilarityMetric;
import util.reader.DatasetReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Used to compute and store the pairwise similarities between all items
 */
public class SimilarityMap {
	
	private Map<Integer,Profile> simMap; 

	/**
	 * Constructor - creates an empty SimilarityMap
	 */
	public SimilarityMap() {
		simMap = new HashMap<Integer,Profile>();
	}

	/**
	 * Constructor - creates a new SimilarityMap and computes similarities
	 * @param reader - the dataset reader
	 * @param metric - similarity metric used to compute item-item similarity
	 */
	public SimilarityMap(final DatasetReader reader, final SimilarityMetric metric) {	
		
		simMap = new HashMap<Integer,Profile>();
		// get the set of item ids
		Set<Integer> itemIds = reader.getItems().keySet();
		
		// compute pairwise similarities between item profiles
		for(Integer id1: itemIds) {
			for(Integer id2: itemIds) {
				if (id1 != id2) {
					double sim = metric.getItemSimilarity(id1, id2);
					if (sim > 0) 
						setSimilarity(id1, id2, sim);
				}
			}
		}
		return;
	}

	/**
	 * Get the item IDS
	 * @returns a set of the numeric IDs of the profiles
	 */
	public Set<Integer> getIds() {
		return simMap.keySet();
	}

	/**
	 * Get the similarities given an ID
	 * @param id - the numeric ID of the profile
	 * @returns the similarity profile for the given id
	 */
	public Profile getSimilarities(Integer id) {
		return simMap.get(id);
	}

	/**
	 * Get the pairwise similarity between two profiles 
	 * @param the numeric ID of the first profile
	 * @param the numeric ID of the second profile
	 * @returns the similarity between the two profiles
	 */
	public double getSimilarity(final Integer id1, final Integer id2) {
		if(simMap.containsKey(id1))
			return (simMap.get(id1).contains(id2) ? simMap.get(id1).getValue(id2).doubleValue() : 0);
		else 
			return 0;
	}

	/**
	 * Adds the similarity between two profiles to the map
	 * @param the numeric ID of the first profile
	 * @param the numeric ID of the second profile
	 */
	public void setSimilarity(final Integer id1, final Integer id2, final double sim) {
		Profile profile = simMap.containsKey(id1) ? simMap.get(id1) : new Profile(id1);
		profile.addValue(id2, Double.valueOf(sim));
		simMap.put(id1, profile);
	}

	/**
	 * String representation of the SimilarityMap
	 * @returns a string representation of all similarity values
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();

		for(Integer id: simMap.keySet())
			buf.append(simMap.get(id).toString());

		return buf.toString();
	}
}