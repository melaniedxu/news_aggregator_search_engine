package autocomplete;

/**
 * ==== Attributes ====
 * - words: number of words
 * - term: the ITerm object
 * - prefixes: number of prefixes 
 * - references: Array of references to next/children Nodes
 * 
 * ==== Constructor ====
 * Node(String word, long weight)
 * 
 * @author Your_Name
 */
public class Node
{
	private int words;
	private Term term;
	private int prefixes;
	private Node[] references;
	/**
	 * Initializes a Node with the given query string and 
	 * weight (calls the Term constructor).
	 * @param query query string
	 * @param weight weight
	 */
	public Node(String query, long weight) {
		if (query == null || weight < 0) {
			throw new IllegalArgumentException();
		}
		term = new Term(query, weight);
		words = 0;
		prefixes = 0;
		references = new Node[26];
	}
	
	/**
	 * default constructor that takes no argument
	 */
	public Node() {
//		term = new Term("", 0);
		words = 0;
		prefixes = 0;
		references = new Node[26];
	}
	
	/**
	 * @return number of words
	 */
	public int getWords() {
		return words;
	}
	/**
	 * @param words: set number of words
	 */
	public void setWords(int words) {
		this.words = words;
	}
	/**
	 * @return the ITerm object
	 */
	public Term getTerm() {
		return this.term;
	}
	/**
	 * @param term set the ITerm object
	 */
	public void setTerm(ITerm term) {
		this.term = (Term)term;
	}
	
	public long getWeight() { 
		return this.term.getWeight();
	}
	
	public void setWeight(long weight) {
		if (this.term != null) {
			this.term.setWeight(weight);
		}
	}
	
	/**
	 * @return number of prefixes 
	 */
	public int getPrefixes() {
		return prefixes;
	}
	/**
	 * @param prefixes set number of prefixes 
	 */
	public void setPrefixes(int prefixes) {
		this.prefixes = prefixes;
	}
	/**
	 * @return Array of references to next/children Nodes
	 */
	public Node[] getReferences() {
		return references;
	}
	/**
	 * @param references set Array of references to next/children Nodes
	 */
	public void setReferences(Node[] references) {
		this.references = references;
	}

}
