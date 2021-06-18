package alg.mf;

import profile.Profile;
import util.reader.DatasetReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Weighted Matrix Factorisation with Stochastic Gradient Descent and Negative Sampling
 * Takes an absence of a rating as being weakly negative negative feedback 
 */
public class WMFSGDRatingPredictionAlg extends MatrixFactorisationRatingPrediction	{

	// Training data is placed in an array in order to be accessed randomly
	private TrainingTriple[] trainingData;
	private Random numGen ;
	// parameter for confidence value
	private Double alpha;
	// negative sampling rate parameter
	private Integer h;
	
	/** Private class - used to store training data during SGD */
	private class TrainingTriple {

	    public final Integer user;
	    public final Integer item;
	    public final Double rating;

	    public TrainingTriple(Integer user, Integer item, Double rating) {
	        this.user = user;
	        this.item = item;
	        this.rating = rating;
	    }
	}


	/**
	 * Constructor
	 * @param reader - the data set reader
	 * @param k - the number of latent space dimensions
	 */
	public WMFSGDRatingPredictionAlg(DatasetReader reader, int k) {
		
		// Creates matrices P and Q, and biases 
		super(reader,k);
		numGen = new Random();
		setDefaultHyperParams();
		
		// ntrans determines the length of the training data for SGD
		int ntrans = 0;
		for (Integer userId : reader.getUserIds() ) {
			Profile pu = reader.getUserProfiles().get(userId);
			ntrans += pu.getSize();
		}
		
		// Initialise the training data array
		trainingData = new TrainingTriple[ntrans];
		
		// Add all user-item interactions (userId, itemId, rating) to trainingData
		ntrans = 0;
		for (Integer userId : reader.getUserIds() ) {
			Profile pu = reader.getUserProfiles().get(userId);
			for (Integer itemId : pu.getIds()) {
				// add confidence = 1 + alpha*rui
				trainingData[ntrans] = new TrainingTriple(userId, itemId, (1 + alpha*pu.getValue(itemId)));
				ntrans += 1;
			}
		}
	}

	
	/**
	 * Sets the default parameters specific to the WMF algorithm
	 */
	protected void setDefaultHyperParams() {
		h = 1;
		alpha = 2.0;
		learningRate = 0.0001;  // smaller learning rate for WMF
		numberPasses = 100;
		regWeightP = 0.5;
		regWeightQ = 0.5;
		regWeightItemBias = 0.5;
		regWeightUserBias = 0.5;
		numReports = 100;
	}
	
	
	/**
	 * Setter for the negative sampling rate
	 * @param h - the negative sampling rate
	 */
	public void setNegativeSamplingRate(Integer h) {
		this.h = h;
	}
	
	
	/**
	 * Setter for the confidence value
	 * @param alpha - the confidence value
	 */
	public void setAlpha(Double alpha) {
		this.alpha = alpha;
	}	

	
	/**
	 * Used to initialises the matrices P and Q
	 * @param Mat - a matrix
	 */
	private void initialise(Double[][] Mat)	{
		for (int i=0; i<Mat.length;i++)	
			for (int j=0; j<Mat[0].length;j++) 
				Mat[i][j] = numGen.nextDouble()/Math.sqrt(K);	
	}

	
	/**
	 * Used to initialise the bias vectors userBias and itemBias
	 * @param Vec - a bias vector
	 */
	private void initialise(Double[] Vec) {
		for (int i=0; i<Vec.length;i++)	
			Vec[i] =  numGen.nextDouble()/Math.sqrt(K);
	}

	
	/**
	 * Fits the data using Weighted Confidence,  Stochastic Gradient and Negative sampling
	 */
	public void fit() {   
		
		// Initialise matrices and biases
		initialise(P);
		initialise(Q);
		initialise(itemBias);
		initialise(userBias);
		globalBias = numGen.nextDouble();

		int reportfreq = (numReports > 0) ? (int) Math.ceil(numberPasses * 1.0 / numReports) : 0;

		// Perform numberPasses iterations and updates
		for (int iter = 0; iter < numberPasses; iter++) {
			
			// Add negative samples
			TrainingTriple[] augmentedTrainingData = addNegativeSamples(trainingData);
			int ntrans = augmentedTrainingData.length;
			double L = 0.0;
			
			// Iterate over all samples in augmentedTrainingData
			for (int s = ntrans; s > 0; s--) {
				
				// Draw a random sample from the training data - each sample gets chosen once
				int draw = numGen.nextInt(s);
				TrainingTriple sample = augmentedTrainingData[draw];
				// swap selected sample with sample currently at the end of the selection range
				augmentedTrainingData[draw] = augmentedTrainingData[s-1];
				augmentedTrainingData[s-1] = sample;
				
				//Get the userId and itemId
				Integer userId = sample.user;
				Integer itemId = sample.item;
								
				// confidence cui is already stored in the triple 
				Double cui = sample.rating;
				
				// rbinui is set to 1 if rui > 0, otherwise set to 0
				// Since cui = 1 + alpha * rui, and alpha is positive we get the following:
				int rbinui = cui > 1 ? 1 : 0;
				
				// Get the corresponding rows of P and Q
				int u = userRow.get(userId);
				int i = itemRow.get(itemId);
				
				// Predict the rating
				Double rhatui = getPrediction(userId,itemId);     //trying to predict 1 for positive
				
				// Compute the Loss with the confidence term
				L = L + cui * (rhatui-rbinui)*(rhatui-rbinui);
				
				// Calculate the gradient for P, Q, itemBias, userBias and globalBias
				// SGD updates P, Q, itemBias, userBias and globalBias directly at each iteration
				// The weights are updated using the confidence value
				for (int k = 0; k < K; k++) {
					
					P[u][k] = P[u][k]
							- learningRate *
							(cui * (rhatui - rbinui) * Q[i][k] + regWeightP*P[u][k]);
					
					Q[i][k] = Q[i][k] 
							- learningRate *
							(cui * (rhatui - rbinui)*P[u][k] + regWeightQ*Q[i][k]);
					
				}
				
				// The biases are updated using the confidence value
				itemBias[i] = itemBias[i]
						- learningRate *
						(cui * (rhatui - rbinui) + regWeightItemBias*itemBias[i]);

				userBias[u] = userBias[u]
						- learningRate *
						(cui * (rhatui - rbinui) + regWeightUserBias*userBias[u]);

				globalBias = globalBias - learningRate*(cui * (rhatui - rbinui));
			}
			
			// Print the loss
			if (reportfreq>0 && iter % reportfreq == 0)
				System.out.printf("Iter=%d \tRMSE=%f\n",iter,Math.sqrt(L/ntrans));
		}
		return;	
	}
	
	
	/**
	 * Performs negative sampling
	 * For every rating in the original training set, h negative samples are added 
	 * These are given a rating of 0, so cui = 1 + alpha*0 = 1
	 * If h exceeds the number of negative ratings for a user then all available negative samples are added
	 * @param trainingSet - the training data
	 * @return augmentedTrainingData - traingSet with added negative samples
	 */
	private TrainingTriple[] addNegativeSamples(TrainingTriple[] trainingSet) {
		
		Set<TrainingTriple> negSamples = new HashSet<TrainingTriple>();
		Map<Integer, Profile> userProfiles = reader.getUserProfiles();
		
		// iterate trough users
		for (Integer userId: reader.getUserIds()) {
			
			ArrayList<Integer> negRated = new ArrayList<Integer>();
			
			// get items the user has rated - these all have positive ratings
			Set<Integer> ratedItems = userProfiles.get(userId).getIds();
				
			// iterate through all items in the dataset
			for (Integer itemId: reader.getItemIds()) {
				
				// if an item hasn't been rated add to negRated
				if (!ratedItems.contains(itemId))
					negRated.add(itemId);		
			}

			// if negRated is less than h*ratedItems.size() sample all of negRated
			if (negRated.size() < (h * ratedItems.size())){
				for (Integer itemId: negRated) 
					negSamples.add(new TrainingTriple(userId, itemId, 1.0));  // 1 + alpha*0 = 1				
			} else {
				
				// otherwise, take h*ratedItems.size() random samples without replacement, 
				
				// shuffle negRated to ensure sampling is random
				for (int i = 0; i < negRated.size(); i++) {
					int r = numGen.nextInt(negRated.size());
					Integer temp = negRated.get(r);
					negRated.set(r, negRated.get(i));
					negRated.set(i, temp);				
				}
				
				// take h*ratedItems.size() samples
				for (int j = 0; j < h * ratedItems.size(); j++) 
					negSamples.add(new TrainingTriple(userId, negRated.get(j), 1.0)); // 1 + alpha*0 = 1
			}
		}
		
		// combine original trainingData with negative samples
		TrainingTriple[] augmentedTrainingData = new TrainingTriple[trainingData.length + negSamples.size()];
		for (int i = 0; i < trainingData.length; i ++)
			augmentedTrainingData[i] = trainingData[i];
		int j = trainingData.length;
		for (TrainingTriple sample: negSamples) 
			augmentedTrainingData[j++] = sample;

		return augmentedTrainingData;
	}
	
}