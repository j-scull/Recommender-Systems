package alg.mf;

import profile.Profile;
import util.reader.DatasetReader;

import java.util.Random;

/**
 * Implements Matrix Factorisation rating prediction - using Stochastic Gradient Descent
 */
public class MFSGDRatingPredictionAlg extends MatrixFactorisationRatingPrediction{

	// Training data is placed in an array in order to be accessed randomly
	private TrainingTriple[] trainingData;
	private Random numGen ;	
	
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
	 * @param k = the number of latent space dimensions to use
	 */
	public MFSGDRatingPredictionAlg(DatasetReader reader, int k) {
		
		// Creates matrices P and Q, and biases 
		super(reader,k);
		setDefaultHyperParams();
		numGen = new Random();
		
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
				trainingData[ntrans] = 	new TrainingTriple(userId,itemId, pu.getValue(itemId)); 
				ntrans += 1;
			}
		}	
	}

	/**
	 * Sets the default parameters specific to the SGD algorithm
	 */
	protected void setDefaultHyperParams() {
		learningRate = 0.01;
		numberPasses = 200;
		regWeightP = 0.5;           
		regWeightQ = 0.5;           
		regWeightItemBias = 0.5;    
		regWeightUserBias = 0.5;    
		numReports = 10;
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
	 * Fits P, Q and biases to the training data
	 * Performs Stochastic Gradient Descent 
	 */
	public void fit() {
		
		// Initialise matrices and biases
		initialise(P);
		initialise(Q);
		initialise(itemBias);
		initialise(userBias);
		globalBias = numGen.nextDouble();

		int reportfreq = numReports > 0 ? (int) Math.ceil(numberPasses * 1.0 / numReports) : 0;
		int ntrans = trainingData.length;

		// Perform numberPasses iterations and updates
		for (int iter = 0; iter < numberPasses; iter++) {

			double L = 0.0;
			
			// Iterate over all samples in trainingData
			for (int s = ntrans; s > 0; s--) {
				
				// Draw a random sample from the training data - each sample gets chosen once
				int draw = numGen.nextInt(s);		
				TrainingTriple sample = trainingData[draw];
				// swap selected sample with sample currently at the end of the selection range
				trainingData[draw] = trainingData[s-1];
				trainingData[s-1] = sample;
				
				// Get the userId, itemId and rating from the sample
				Integer userId = sample.user;
				Integer itemId = sample.item;
				Double rui = sample.rating;

				// Get the corresponding rows of P and Q
				int u = userRow.get(userId);
				int i = itemRow.get(itemId);
				
				// Predict the rating
				Double rhatui = getPrediction(userId,itemId);

				// Compute the loss
				L = L + (rhatui-rui)*(rhatui-rui);
				
				// Calculate the gradient for P, Q, itemBias, userBias and globalBias
				// SGD updates P, Q, itemBias, userBias and globalBias directly at each iteration
				for (int k=0;k<K;k++) {				
					
					P[u][k] = P[u][k] 
							- learningRate *
							( (rhatui - rui)*Q[i][k] + regWeightP*P[u][k]);
					
					Q[i][k] = Q[i][k] 
							- learningRate *
							( (rhatui - rui)*P[u][k] + regWeightQ*Q[i][k]);

				}
				itemBias[i] = itemBias[i]
						-learningRate *
						((rhatui - rui) + regWeightItemBias*itemBias[i]);

				userBias[u] = userBias[u]
						-learningRate *
						((rhatui - rui) + regWeightUserBias*userBias[u]);

				globalBias = globalBias - learningRate*(rhatui-rui);
			}
			
			// Print the Loss
			if (reportfreq > 0 && iter % reportfreq == 0)
				System.out.printf("Iter=%d \tRMSE=%f\n", iter, Math.sqrt(L/ntrans));
			
		}
		return;	
	}
}

