package neighbourhood;

import alg.np.similarity.SimilarityMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An abstract base class to compute neighbourhood formations in UBCF recommenders
 */
public abstract class Neighbourhood {
	
	private Map<Integer,Set<Integer>> neighbourhoodMap; // stores the neighbourhood users for each user in a set
	
	/** Constructor	 */
	public Neighbourhood() {
		neighbourhoodMap = new HashMap<Integer,Set<Integer>>();
	}
	
	/**
	 * Gets the neighbours for a given user
	 * @param id - the user's ID
	 * @returns the neighbours for id
	 */
	public Set<Integer> getNeighbours(final Integer id) {
		return neighbourhoodMap.get(id);
	}
	
	/**
	 * Checks if two users are neighbours
	 * @param id1 - a user's ID
	 * @param id2 - a user's ID
	 * @returns true if id2 is a neighbour of id1
	 */
	public boolean isNeighbour(final Integer id1, final Integer id2) {
		if (neighbourhoodMap.containsKey(id1))
			return neighbourhoodMap.get(id1).contains(id2);
		else
			return false;
	}
	
	/**
	 * Add a user to another user's neighbourhood
	 * @param id1 - a user's ID
	 * @param id2 - a new neighbour's ID
	 */
	public void add(final Integer id1, final Integer id2) {
		Set<Integer> set = neighbourhoodMap.containsKey(id1) ? neighbourhoodMap.get(id1) : new HashSet<Integer>();
		set.add(id2);
		neighbourhoodMap.put(id1, set);
	}
	
	/**
	 * Computes neighbourhoods for all users and stores them in neighbourhood map - must be called before isNeighbour(Integer,Integer).
	 * @param simMap - a map containing user-user similarities
	 */
	public abstract void computeNeighbourhoods(final SimilarityMap simMap);
}
