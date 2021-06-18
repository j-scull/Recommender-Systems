package neighbourhood;

import alg.np.similarity.*;
import util.ScoredThingDsc;
import profile.Profile;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Forms neighbourhoods for UBCF algorithms using a k-NN approach.
 */
public class NearestNeighbourhood extends Neighbourhood {
	
	private final int k; // the number of neighbours in the neighbourhood
	
	/**
	 * Constructor
	 * @param k - the number of neighbours in the neighbourhood
	 */
	public NearestNeighbourhood(final int k) {
		super();
		this.k = k;
	}
	
	/**
	 * Computes neighbourhoods for all users and stores them in neighbourhood map - must be called before isNeighbour()
	 * @param simMap - a map containing user-user similarities
	 */
	public void computeNeighbourhoods(final SimilarityMap simMap) {
		
		for (Integer simId: simMap.getIds()) { // iterate over all ids
			
			// for the current id, store all similarities in order of descending similarity in a sorted set
			SortedSet<ScoredThingDsc> ss = new TreeSet<ScoredThingDsc>();
			
			Profile profile = simMap.getSimilarities(simId); // get the similarity profile
			if (profile != null) {
				for (Integer id: profile.getIds()) { // iterate over each id in the profile
					double sim = profile.getValue(id);
					if (sim > 0)
						ss.add(new ScoredThingDsc(sim, id));
				}
			}
			
			// get the k most similar users (neighbours)
			int counter = 0;
			for (Iterator<ScoredThingDsc> iter = ss.iterator(); iter.hasNext() && counter < k; ) {
				ScoredThingDsc st = iter.next();
				Integer id = (Integer)st.thing;
				this.add(simId, id);
				counter++;
			}
		}	
	}
	
	/** String representation of the class */
	public String toString() {
		return "NearestNeighbourhood";
	}
}
