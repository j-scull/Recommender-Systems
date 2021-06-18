package alg;

import profile.Profile;
import util.ScoredThingDsc;
import util.reader.DatasetReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * Base class for Recommender algorithms
 */
public abstract class Recommender implements RecAlg {
	
	protected DatasetReader reader;
	
	/** Constructor	*/
	public Recommender(DatasetReader reader) {
		this.reader = reader;
	}
		
	/**
	 * Get the predicted ratings for a given user for all items
	 * @param userId - a user's id
	 * @return a Profile containing item ratings <itemId, rating>
	 */
	public abstract Profile getRecommendationScores(Integer userId);

	
	/**
	 * Gets a list of recommended items for the user given the user profile and predicted ratings for the user 
	 * Recommended items are in descending order according to predicted rating score 
	 * @param userProfile - the Profile of items rated by the user
	 * @param score - the Profile of predicted ratings for the user
	 * @return a list of recommended items
	 */
	public List<Integer> getRecommendationsFromScores(Profile userProfile, Profile scores) {
		
		// create a list to store recommendations
		List<Integer> recs = new ArrayList<Integer>();
		
		if (scores==null)
			return recs;

		// store all scores in descending order in a sorted set
		SortedSet<ScoredThingDsc> ss = new TreeSet<ScoredThingDsc>(); 
		for(Integer id: scores.getIds()) {
			double s = scores.getValue(id);
			if (s > 0)
				ss.add(new ScoredThingDsc(s, id));
		}
		
		// save all recommended items in descending order of similarity in the list
		// but leaving out items that are already in the user's profile
		for(Iterator<ScoredThingDsc> iter = ss.iterator(); iter.hasNext();) {
			ScoredThingDsc st = iter.next();
			Integer id = (Integer)st.thing;
			if (st.score > 0.0 && !userProfile.contains(id))
				recs.add(id);
		}
		
		return recs;
	}
}