package alg.mf;

import profile.Profile;
import util.reader.DatasetReader;

import java.util.Random;

/**
 * Implements Matrix Factorisation rating prediction - uses Batch Gradient Descent
 */
public class MFGradientDescentRatingPredictionAlg extends MatrixFactorisationRatingPrediction	{

	private Random numGen ;

	/**
	 * Constructor
	 * @param reader - the data set reader
	 * @param k - the number of latent space dimensions
	 */
	public MFGradientDescentRatingPredictionAlg(DatasetReader reader, int k) {
		super(reader,k);
		numGen = new Random();
	}
	
	
	/**
	 * Use default hyper-parameter settings
	 */
	protected void setDefaultHyperParams() {
		learningRate = 0.01;
		numberPasses = 200;
		regWeightP = 0.5;
		regWeightQ = 0.5;
		regWeightItemBias = 0.5;
		regWeightUserBias = 0.5;
		numReports = 100;
	}
	
	
	/**
	 * Sets each value in a matrix to a random double
	 * Used to initialise P, Q 
	 * @param Mat - the matrix being initialised
	 */
	private void initialise(Double[][] Mat){
		for (int i = 0; i < Mat.length; i++)	
			for (int j = 0; j < Mat[0].length; j++) 
				Mat[i][j] = numGen.nextDouble();	
	}
	
	
	/**
	 * Sets each value in a vector to a random double
	 * Used to initialise user and item biases
	 * @param Vec - the vector being initialised
	 */
	private void initialise(Double[] Vec){
		for (int i = 0; i < Vec.length; i++)
			Vec[i] =  numGen.nextDouble();
	}
	
	
	/**
	 * Fits P, Q and biases to the training data - performs Gradient Descent 
	 */
	public void fit() {   
		
		int nitems = Q.length;
		int nusers = P.length;
		
		// Updated parameter values will be stored in new variables 
		Double [][] Pnew = new Double[nusers][K];
		Double [][] Qnew = new Double[nitems][K];
		Double [] itemBiasnew = new Double[nitems];
		Double [] userBiasnew = new Double[nusers];   // should this be nusers???
		Double globalBiasnew = 0.0;
		
		// Set all values randomly
		initialise(P);
		initialise(Q);
		initialise(itemBias);
		initialise(userBias);
		globalBias = numGen.nextDouble();
		
		// Set evaluation reporting frequency
		int reportfreq = numReports > 0 ? (int) Math.ceil(numberPasses * 1.0 / numReports) : 0;
		
		Integer [] degu = new Integer[nusers]; // degu[u] = size of user u profile
		Integer [] degi = new Integer[nitems]; // degi[i] = size of item i profile
		Double ntrans = 0.0;                   // will store the total number of user-item interactions
		
		for (Integer userId : reader.getUserIds() ) {
			int u = userRow.get(userId);
			Profile pu = reader.getUserProfiles().get(userId);
			degu[u] = pu.getIds().size();
			ntrans += pu.getIds().size();
		}
		
		for (Integer itemId : reader.getItemIds() ) {
			int i = itemRow.get(itemId);
			Profile iu = reader.getItemProfiles().get(itemId);
			degi[i] = iu.getIds().size();
		}
		
		// Copy values from P, Q, itemBias, userBias and globalBias into Pnew, Qnew, itemBiasnew, userBiasnew and globalBiasnew 
		for (int u = 0; u < nusers; u++) {
			for (int k = 0; k < K; k++) {
				Pnew[u][k] = P[u][k];
			}
			userBiasnew[u] = userBias[u];
		}
		for (int i = 0; i < nitems; i++) {
			for (int k = 0; k < K; k++) {
				Qnew[i][k] = Q[i][k];
			}
			itemBiasnew[i] = itemBias[i];
		}
		globalBiasnew = globalBias;
		
		// Perform numberPasses iterations and updates
		for (int iter = 0; iter < numberPasses; iter++) {
			
			double L = 0.0;
			
			// Iterate through all users
			for (Integer userId : reader.getUserIds() ) {
			
				Profile pu = reader.getUserProfiles().get(userId);
				
				// Iterate through all items
				for (Integer itemId : pu.getIds()) {
					
					// Get the row of P and Q corresponding to userId, itemId
					int u = userRow.get(userId);
					int i = itemRow.get(itemId);	
					
					// Predict the rating given by userId for itemId
					Double rhatui = getPrediction(userId,itemId);
					
					// Get the actual rating given by userId for itemId
					Double rui = pu.getValue(itemId);
					
					// Compute the loss
					L = L+(rhatui-rui)*(rhatui-rui);

					// Compute the gradient for gradient for P, Q, itemBias, userBias and globalBias
					// Add the gradient to Pnew, Qnew, itemBiasnew,userBiasnew and globalBiasnew
					for (int k = 0; k < K; k++) {
					
						Pnew[u][k] = Pnew[u][k] 
								- learningRate/degu[u] *
								( (rhatui - rui)*Q[i][k] + regWeightP*P[u][k]);

						
						Qnew[i][k] = Qnew[i][k] 
								- learningRate/degi[i] *
								( (rhatui - rui)*P[u][k] + regWeightQ*Q[i][k]);
						
					}
					itemBiasnew[i] = itemBiasnew[i]
						-learningRate/degi[i] *
						((rhatui - rui) + regWeightItemBias*itemBias[i]);
					
					userBiasnew[u] = userBiasnew[u]
							-learningRate/degu[u] *
							((rhatui - rui) + regWeightUserBias*userBias[u]);
					
					globalBiasnew = globalBiasnew
							- learningRate/(degi[i]*degu[u])*(rhatui-rui);
					
				}
				
			}
			// Print the Loss
			if (reportfreq > 0 && iter % reportfreq == 0)
				System.out.printf("Iter=%d \tRMSE=%f\n", iter, Math.sqrt(L/ntrans));
			
			// After each iteration copy Pnew, Qnew, itemBiasnew, userBiasnew and globalBiasnew into
			// into P, Q, itemBias, userBias and globalBias
			for (int u = 0; u < nusers; u++) {
				for (int k = 0; k < K; k++) {				
					P[u][k] = Pnew[u][k];
				}
				userBias[u] = userBiasnew[u];
			}			
			for (int i = 0; i < nitems; i++) {
				for (int k = 0; k < K; k++) {
					Q[i][k] = Qnew[i][k];
				}
				itemBias[i] = itemBiasnew[i];
			}
			globalBias = globalBiasnew;
			
		}
		return;
	}

}
