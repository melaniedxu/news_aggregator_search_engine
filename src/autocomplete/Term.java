package autocomplete;

/**
 * @author danmeixu
 *
 */
public class Term implements ITerm {
	
	private String term;
	private long weight;
	
	/**
	 * Initializes a term with the given query string and weight.
	 * @param query query stirng 
	 * @param weight weight of string
	 */
	public Term(String query, long weight) {
		if (query == null || weight < 0) {
			throw new IllegalArgumentException();
		}
		this.setTerm(query);
		this.setWeight(weight);
	}

	@Override
	public int compareTo(ITerm that) {
		// TODO Auto-generated method stub
		return this.term.compareTo(((Term)that).term);
	}
	
	@Override
	public String toString() {
		return weight + "	" + term;
	}

	/**
	 * @return string term
	 */
	public String getTerm() {
		return term;
	}

	/**
	 * @param term string term to be set
	 */
	public void setTerm(String term) {
		this.term = term;
	}

	/**
	 * @return long weight
	 */
	public long getWeight() {
		return weight;
	}

	/**
	 * @param weight long weight to be set
	 */
	public void setWeight(long weight) {
		this.weight = weight;
	}

}
