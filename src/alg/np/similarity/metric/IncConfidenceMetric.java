package alg.np.similarity.metric;

import util.reader.DatasetReader;
import profile.Profile;

import java.util.Map;

/**
 * Calculates similarity using association rule mining. 
 * Given a data set of user ratings for items, and a threshold at which an rating is to be considered positive, 
 * confidence estimates the probability that users who liked item X will also like item Y
 */
public class IncConfidenceMetric implements SimilarityMetric {
	
	private static double RATING_THRESHOLD = 4.0; // the default threshold rating at which to consider a rating positive
	private DatasetReader reader; 

	/**
	 * Constructor
	 * @param reader - the dataset reader
	 */
	public IncConfidenceMetric(final DatasetReader reader) {
		this.reader = reader;
	}

	/**
	 * computes the similarity between items
	 * @param X - the id of the first item 
	 * @param Y - the id of the second item
	 */
	public double getItemSimilarity(final Integer X, final Integer Y) {
		
		double nX = 0;          // number of occurrences of X
		double nXY = 0;         // number of co-occurrences of X and Y
		double likeX = 0;       // number of users that liked X
		double likeXAndY = 0;   // number of users that liked X and Y
		double likeYNotX = 0;   // number of users that liked Y but did not like X
		
		// Iterate through user Profiles
		Map<Integer,Profile> userProfiles = reader.getUserProfiles();
		for (Profile user : userProfiles.values()) {
			
			// Check if X or Y have been rated
			if (user.getValue(X) != null) {
				nX += 1;
				nXY += 1;
				if (user.getValue(Y) != null) {
					if (user.getValue(X) >= RATING_THRESHOLD && user.getValue(Y) >= RATING_THRESHOLD) 
						likeXAndY += 1;
				}
				if (user.getValue(X) >= RATING_THRESHOLD) 
					likeX += 1;	
				else if (( user.getValue(Y) != null) && (user.getValue(Y) >= RATING_THRESHOLD)) { 
						likeYNotX += 1;
				}
			} else if (user.getValue(Y) != null) {
				nXY += 1;
			}
		}
		
		// Calculate the confidence - output zero where division by zero occurs
		double suppX = (nX > 0) ? likeX / nX : 0;                   
		double suppXandY = (nXY > 0) ? likeXAndY / nXY : 0;   	    
		double confXY = (suppX > 0) ? suppXandY / suppX : 0;       
		
		double suppNotX = (nX > 0) ? (nX - likeX) / nX : 0;         
		double suppNotXAndY = (nXY > 0) ? (likeYNotX) / nXY : 0;    
		double confNotXAndY = (suppNotX > 0) ? suppNotXAndY / suppNotX : 0; 
		
		// return similarity using conf(X => Y) / conf(!X => Y)
		return (confNotXAndY > 0) ? confXY / confNotXAndY : 0;      
	
	}
	

	
	public String toString() {
		return "IncConfidence";
	}
}
