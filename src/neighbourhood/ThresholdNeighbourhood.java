package neighbourhood;

import alg.np.similarity.SimilarityMap;
import profile.Profile;

/**
 * Forms neighbourhoods for UBCF algorithms using a threshold approach.
 */
public class ThresholdNeighbourhood extends Neighbourhood {
	
	private final double threshold; // the similarity user-user threshold for the neighbourhood
	
	/**
	 * Constructor
	 * @param threshold - the similarity threshold at which to consider two users as neighbours
	 */
	public ThresholdNeighbourhood(final double threshold) {
		super();
		this.threshold = threshold;
	}
	
	/**
	 * Computes neighbourhoods for all users and stores them in neighbourhood map - must be called before isNeighbour()
	 * @param simMap - a map containing user-user similarities
	 */
	public void computeNeighbourhoods(final SimilarityMap simMap) {
		for (Integer user: simMap.getIds()) {
			
			Profile similarities = simMap.getSimilarities(user);
			if (similarities != null) {
				for (Integer otherUser: similarities.getIds()) {
					if (similarities.getValue(otherUser) > threshold) {
						this.add(user, otherUser);
					}		
				}	
			}
		}
	}
	
	/** String representation of the class */
	public String toString() {
		return "ThresholdNeighbourhood";
	}
	
}
