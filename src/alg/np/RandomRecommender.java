package alg.np;

import alg.Recommender;
import profile.Profile;
import util.reader.DatasetReader;

import java.util.List;
import java.util.Set;
import java.util.Random;


/**
 * Baseline recommender - recommends items at random
 * NOT A FUNCTIONING RECOMMENDER!
 */
public class RandomRecommender extends Recommender {
	
	private long seed;
	private Random numGen ;
	
	/**
	 * Constructor - with default seed
	 * @param reader - the data set reader
	 */
	public RandomRecommender(final DatasetReader reader) {
		super(reader);
		this.seed = System.nanoTime();
		this.numGen = new Random(seed);
	}
	
	
	/**
	 * Constructor with user determined seed
	 * @param reader - the data set reader
	 * @param seed - seed for the random generator
	 */
	public RandomRecommender(final DatasetReader reader, final long seed) {
		super(reader);
		this.seed = seed;
		this.numGen = new Random(seed);
	}

	
	/**
	 * Makes random rating predictions for a given user for all items
	 * @param userId - a user's id
	 * @return a Profile containing item ratings <itemId, rating>
	 */
	public Profile getRecommendationScores(Integer userId) {
		Profile scores = new Profile(userId);
		Set<Integer> itemIds = reader.getItemIds();
		
		// Generate a set of random scores 
		for (Integer item : itemIds) {		
			scores.addValue(item, numGen.nextDouble());			
		}
		return scores;
	}

	
	/**
	 * Get random item ratings for a user
	 * @returns the recommendations based on random scores
	 * @param userId - a user's ID
	 * @return a Profile of random item ratings for the user
	 */
	public List<Integer> getRecommendations(Integer userId)
	{	
		Profile scores = new Profile(userId);
		Set<Integer> itemIds = reader.getItemIds();
		
		// Generate a set of random scores 
		for (Integer item : itemIds) {		
			scores.addValue(item, numGen.nextDouble());			
		}
		
		// Generate recommendations from the scores
		Profile userProfile = reader.getUserProfiles().get(userId);
		
		List<Integer> recs = getRecommendationsFromScores(userProfile,scores);
		return recs;
	}
}


