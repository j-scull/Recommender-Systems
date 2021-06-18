package alg.rerank;

import java.util.List;

import profile.Profile;

/**
 * An interface for recommendation re-ranking algorithms
 */
public interface Reranker {
	
	public List<Integer> rerank(Profile userProfile, Profile scores);

}
