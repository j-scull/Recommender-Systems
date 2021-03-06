package profile;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

/**
 * This class is used to store (non-exhaustive list):
 * (a) the ratings assigned to items by a particular user or 
 * (b) the ratings assigned by users to a particular item or
 * (c) the similarities between a user and other users or
 * (d) the similarities between an item and other items or
 * (e) the genome scores for a particular item
 */
public class Profile {
	
	private Integer id; // the numeric ID of the profile
	private Map<Integer,Double> dataMap; // stores either (a), (b), (c), (d) or (e) above
	
	/**
	 * Constructor
	 * @param id - the Profile's ID i.e. a user's ID or an item's ID
	 */
	public Profile(final Integer id) {
		this.id = id;
		this.dataMap = new HashMap<Integer,Double>();
	}

	/** @returns the profile ID	 */
	public Integer getId() {
		return id;
	}

	/** @returns the profile size */
	public int getSize() {
		return dataMap.size();
	}
	
	/** @returns true if the ID is in the profile */
	public boolean contains(final Integer id) {
		return dataMap.containsKey(id);
	}
	
	/** @returns the value for the ID (or null if ID is not in profile) */
	public Double getValue(final Integer id) {
		return dataMap.get(id);
	}

	/** @returns the mean value over all values in the profile */
	public double getMeanValue() {
		double total = 0;
		for(Double r: dataMap.values())
			total += r.doubleValue();
		return getSize() > 0 ? total / getSize() : 0;
	}

	/** @returns the norm of all values in the profile */
	public double getNorm()	{
		double sumsq = 0;
		for(Double r: dataMap.values())
			sumsq += Math.pow(r.doubleValue(), 2);
		return Math.sqrt(sumsq);
	}
	
	/** @returns the set of IDs in the profile */
	public Set<Integer> getIds() {
		return dataMap.keySet();
	}
	
	/**
	 * Get the users/items that two profiles have in common
	 * @returns a set of IDs that two profiles have in common
	 */
	public Set<Integer> getCommonIds(final Profile other) {
		Set<Integer> common = new HashSet<Integer>();
		for(Integer id: getIds())
			if(other.contains(id))
				common.add(id);
		return common;
	}

	/**
	 * Add a key-value pair to the profile
	 * @param the ID to be added to the profile
	 * @param the corresponding value
	 */
	public void addValue(final Integer id, final Double value) {
		dataMap.put(id, value);
	}
	
	/** String representation of the Profile */
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer();		
		for(Iterator<Map.Entry<Integer,Double>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<Integer,Double> entry = (Map.Entry<Integer,Double>)it.next();
			Integer id = entry.getKey();
			Double value = entry.getValue();
			buf.append(new String(getId() + " " + id + " " + value + "\n"));
		}
		return buf.toString();
	}
}