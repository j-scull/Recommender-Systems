package alg.mf;

import alg.RatingPredictionAlg;
import util.reader.DatasetReader;

import java.util.Map;
import java.util.HashMap;


/**
 * Base class for Matrix Factorisation algorithms
 */
public abstract class MatrixFactorisationRatingPrediction implements RatingPredictionAlg, ModelBasedAlg {
	
	protected Double[][] P;
	protected Double[][] Q;
	protected Double[] itemBias;
	protected Double[] userBias;
	protected Map<Integer,Integer> userRow;  // maps userIds to index numbers in matrices R and P
	protected Map<Integer,Integer> itemRow;  // maps itemIds to index numbers in matrices R and Q
	protected Double globalBias;
	protected int K;                         // latent dimensions
	protected DatasetReader reader;
	
	// Hyper-parameters for Gradient Descent
	protected Double learningRate;
	protected Integer numberPasses;
	protected Double regWeightP;
	protected Double regWeightQ;
	protected Double regWeightItemBias;
	protected Double regWeightUserBias;
	protected Integer numReports;
	
	
	/**
	 * Constructor
	 * @param reader - the dataset reader
	 * @param k - the number of latent space dimensions to use
	 */
	MatrixFactorisationRatingPrediction(DatasetReader reader, Integer k) {
		
		this.reader = reader;
		
		// userRow maps userIds to index numbers in matrices R and P
		userRow = new HashMap<Integer, Integer>();
		int nusers = 0;
		for (Integer userId : reader.getUserIds()) 
			userRow.put(userId, nusers++);
		
		// itemRow maps itemIds to index numbers in matrices R and Q
		itemRow = new HashMap<Integer, Integer>();
		int nitems = 0;
		for (Integer itemId : reader.getItemIds())
			itemRow.put(itemId, nitems++);
			
		globalBias = 0.0;
		setLatentSpaceDim(k);
		setDefaultHyperParams();
	}
	
	
	/**
	 * Creates the matrices P and Q using the given number of latent space dimension
	 * Sets the user and item biases to 0.
	 * @param dim - the number of latent space dimensions to use in the model
	 */
	public void setLatentSpaceDim(int dim) {
		
		this.K = dim;
		int nitems = reader.getItemIds().size();
		int nusers = reader.getUserIds().size();

		// Create matrices
		P = new Double[nusers][dim];
		Q = new Double[nitems][dim];
		
		// Create biases
		itemBias = new Double[nitems];
		userBias = new Double[nusers];
	}
	
	
	/**==========Hyper-Parameter Setters============*/
	
	public void setLearningRate(Double learningRate) {
		this.learningRate = learningRate;
	}
	public void setNumberPasses(int numberPasses) {
		this.numberPasses = numberPasses;
	}
	public void setRegularisationWeights(Double weight) {
		this.regWeightP = weight;
		this.regWeightQ = weight;
		this.regWeightItemBias = weight;
		this.regWeightUserBias = weight;
	}
	public void setRegWeightP(Double weight) {
		this.regWeightP = weight;
	}
	public void setRegWeightQ(Double weight) {
		this.regWeightQ = weight;
	}
	public void setRegWeightItemBias(Double weight) {
		this.regWeightItemBias = weight;
	}
	public void setRegWeightUserBias(Double weight) {
		this.regWeightUserBias = weight;
	}
	
	/**
	 * Sets the frequency of evaluation reports during Gradient Descent
	 * @param numReports the number of reports to generate during training
	 */
	public void setNumReports(int numReports){
		this.numReports = numReports;
	}

	/**
	 * Fits P, Q and biases to the training data by performing Gradient Descent 
	 */
	abstract public void fit();
	
	/**
	 * Sets default hyper-parameters - specific to concrete algorithms
	 */
	abstract protected void setDefaultHyperParams();
	
	/**
	 * Gets the predicted item rating for a user
	 * Performs vector multiplication where R[user][item] = P[user] * Q[item]
	 * @param userId - a user's id
	 * @param itemId - an item's id
	 * @return predicted item rating for a user - R[user][item]
	 */
	public Double getPrediction(final Integer userId, final Integer itemId) {
		Integer user = userRow.get(userId);
		Integer item = itemRow.get(itemId);
		Double rhat = userBias[user] + itemBias[item] + globalBias;   // add user, item and global biases
		for (int j = 0; j < K; j++)
			rhat += P[user][j] * Q[item][j];                          // vector multiplication R[user][item] = P[user] * Q[item]
		return rhat;                                                  // return R[user][item]
	}
}
