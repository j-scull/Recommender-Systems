package alg.mf;

/**
 * Interface for Factorisation-based recommender algorithms
 */
public interface ModelBasedAlg {
	
	/**
	 * Fits the model to the training data by optimisation with performing Gradient Descent 
	 */
	public void fit();

}
