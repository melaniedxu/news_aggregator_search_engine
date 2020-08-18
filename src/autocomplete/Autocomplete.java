package autocomplete;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author danmeixu
 *
 */
public class Autocomplete implements IAutocomplete {
	
	private Node root;
	private int max;
	
	/**
	 * default constructor
	 */
	public Autocomplete() {
		this.max = 0;
		this.root = new Node();
	}

	@Override
	public void addWord(String word, long weight) {
		// TODO Auto-generated method stub
		if (word == null || word.length() == 0) {
			return;
		}
		if (!word.matches("^[a-z]*$")) {
			return;
		}
		String str = word.toLowerCase();
		Node cur = root;
		cur.setPrefixes(cur.getPrefixes() + 1);
		int i = 0;
		for (; i < word.length() - 1; i++) {
			Character c = str.charAt(i);
			if (cur.getReferences()[c - 'a'] == null) {
				cur.getReferences()[c - 'a'] = new Node("", 0);
			}
			cur = cur.getReferences()[c - 'a'];
			cur.setPrefixes(cur.getPrefixes() + 1);
		}
		Character c = str.charAt(i);
		if (cur.getReferences()[c - 'a'] == null) {
			cur.getReferences()[c - 'a'] = new Node(str, weight);
			cur = cur.getReferences()[c - 'a'];
			cur.setPrefixes(cur.getPrefixes() + 1);
			cur.setWords(cur.getWords() + 1);
		}
		else {
			cur.getReferences()[c - 'a'].setTerm(new Term(str, weight));
			cur = cur.getReferences()[c - 'a'];
			cur.setWords(cur.getWords() + 1);
			cur.setPrefixes(cur.getPrefixes() + 1);
		}
	}

	@Override
	public Node buildTrie(String filename, int k) {
		// TODO Auto-generated method stub
		this.max = k;
		try {
			FileReader file = new FileReader(filename);
			BufferedReader br = new BufferedReader(file);
			br.readLine();
			String input = br.readLine();
			while (input != null) {
//				String[] strings = input.split("	");
				String[] strings = input.split(" ");
				for (int i = 0; i < strings.length; i++) {
					strings[i] = strings[i].replaceAll("\\s", "");
				}
				if (strings.length >= 2) {
					long weight = Long.parseLong(strings[0]);
					String word = strings[1].toLowerCase();
					if (word.matches("^[a-z]*$")) {
						addWord(word, weight);
					}
				}
				input = br.readLine();
			}
			br.close();			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return root;
	} 
 
	@Override
	public int numberSuggestions() {
		// TODO Auto-generated method stub
		return this.max;
	}

	@Override
	public Node getSubTrie(String prefix) {
		// TODO Auto-generated method stub
		if (prefix == null) {
			return null;
		}
		if (!prefix.matches("^[a-z]*$")) {
			return null;
		}
		Node cur = root;
		prefix = prefix.toLowerCase();
		for (int i = 0; i < prefix.length(); i++) {
			Character c = prefix.charAt(i);
			cur = cur.getReferences()[c - 'a'];
			if (cur == null) {
				return null;
			}
		}
		return cur;
	}

	@Override
	public int countPrefixes(String prefix) {
		// TODO Auto-generated method stub
		if (prefix == null) {
			return 0;
		}
		if (!prefix.matches("^[a-z]*$")) {
			return 0;
		}
		Node cur = getSubTrie(prefix.toLowerCase());
		if (cur == null) {
			return 0;
		}
		return cur.getPrefixes();
	}
  
	@Override
	public List<ITerm> getSuggestions(String prefix) {
		// TODO Auto-generated method stub
		List<ITerm> res = new ArrayList<>();
		int count = countPrefixes(prefix);
		if (count == 0) {
			return res;
		}
		Node subtrie = getSubTrie(prefix);
		searchHelper(subtrie, res);
		return res;
	}
	
	// helper function to DFS every child node of the prefix subtrie
	// so that every word with the prefix is found in the trie
	private void searchHelper(Node cur, List<ITerm> results) {
		if (cur == null) {
			return;
		}
		
		if (cur.getWords() != 0) {
			results.add(cur.getTerm());
		}
		
		Node[] ref = cur.getReferences();
		for (int i = 0; i < 26; i++) {
			if (ref[i] != null) {
				searchHelper(ref[i], results);
			}
		}
	}

}
