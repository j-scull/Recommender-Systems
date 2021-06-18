package util;

import profile.Profile;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an item that can be rated by users
 */
public class Item {
	
	private Integer id;           // the numeric ID of the item
	private String name;          // the name of the item
	private Set<String> genres;   // a hash set containing genres
	private Profile genomeScores; // a profile with genome scores
	
	/**
	 * Default Constructor
	 * @param id - the item's ID
	 * @param name - the item's name
	 */
	public Item(final Integer id, final String name) {
		this.id = id;
		this.name = name;
		this.genres = new HashSet<String>();
		this.genomeScores = new Profile(id);
	}

	/**
	 * Constructor with genre and genomeScore
	 * @param id - the item's ID
	 * @param name - the item's name
	 * @param genres - the genres relevant to the item
	 * @param genomeScores generated for the item - particular to the movie lens data set
	 */
	public Item(final Integer id, final String name, final Set<String> genres, final Profile genomeScores) {
		this.id = id;
		this.name = name;
		this.genres = genres;
		this.genomeScores = genomeScores;
	}
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the genres
	 */
	public Set<String> getGenres() {
		return genres;
	}

	/**
	 * @param genres the genres to set
	 */
	public void setGenres(Set<String> genres) {
		this.genres = genres;
	}

	/**
	 * @return the genomeScores
	 */
	public Profile getGenomeScores() {
		return genomeScores;
	}

	/**
	 * @param genomeScores the genomeScores to set
	 */
	public void setGenomeScores(Profile genomeScores) {
		this.genomeScores = genomeScores;
	}	
}
