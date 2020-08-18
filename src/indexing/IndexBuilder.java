package indexing;

import java.io.*;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

/**
 * @author danmeixu
 *
 */
public class IndexBuilder implements IIndexBuilder {
 
	@Override
	public Map<String, List<String>> parseFeed(List<String> feeds) {
		Map<String, List<String>> map = new HashMap<>();
		for (String s: feeds) {
			try {
				Document doc = Jsoup.connect(s).get();
				Elements links = doc.getElementsByTag("link");
				for (Element link : links) {
					String linkText = link.text();
					Document web = Jsoup.connect(linkText).get();
					Elements body = web.getElementsByTag("body");
					String bodyText = body.text();
					bodyText = bodyText.toLowerCase();
					String[] results = bodyText.split(" ");
					for (int i = 0; i < results.length; i++) {
						results[i] = results[i].replaceAll("[^a-z0-9]", "");
					}
					List<String> value = Arrays.asList(results);
					map.put(linkText, value);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return map;
	}

	@Override
	public Map<String, Map<String, Double>> buildIndex(Map<String, List<String>> docs) {
		if (docs == null) {
			return null;
		}
		Map<String, Map<String, Double>> fwdIndex = new HashMap<>();
		Map<String, Map<String, Double>> interIndex = new HashMap<>();
		Map<String, Integer> documentsPerWord = new HashMap<>();
		for (Map.Entry<String, List<String>> entry : docs.entrySet()) {
			List<String> words = entry.getValue();
			Map<String, Integer> freqTable = new HashMap<>();
			for (String w : words) {
				if (freqTable.containsKey(w)) {
					freqTable.put(w, freqTable.get(w) + 1);
				}
				else {
					freqTable.put(w, 1);
					if (documentsPerWord.containsKey(w)) {
						documentsPerWord.put(w, documentsPerWord.get(w) + 1);
					}
					else {
						documentsPerWord.put(w, 1);
					}
				}
			}
			Map<String, Double> interValue = new HashMap<>();
			for (Map.Entry<String, Integer> freqEntry : freqTable.entrySet()) {	
				double TFval = (double)freqEntry.getValue() / (double)words.size();
				interValue.put(freqEntry.getKey(), TFval);
			}
			interIndex.put(entry.getKey(), interValue);
		}

		for (Map.Entry<String, Map<String, Double>> interEntry : interIndex.entrySet()){
			Map<String, Double> fwdValue = new TreeMap<String, Double>();
			for (Map.Entry<String, Double> e : interEntry.getValue().entrySet()) {
				String term = e.getKey();
				double IDFval = Math.log((double)docs.size() / (double)documentsPerWord.get(term));
				fwdValue.put(term, e.getValue() * IDFval);
			}
			fwdIndex.put(interEntry.getKey(), fwdValue);
		}
		
		return fwdIndex;
	}

	@Override
	public Map<?, ?> buildInvertedIndex(Map<String, Map<String, Double>> index) {
		// TODO Auto-generated method stub
		Map<String, Set<Map.Entry<String, Double>>> invIndex = new HashMap<>();
		for (Map.Entry<String, Map<String, Double>> indexEntry : index.entrySet()) {
			String pageName = indexEntry.getKey();
			Map<String, Double> termTFIDF = indexEntry.getValue();
			for (Map.Entry<String, Double> entry : termTFIDF.entrySet()) {
				String term = entry.getKey();
				Double tfidf = entry.getValue();
				if (!invIndex.containsKey(term)) {
					Set<Map.Entry<String, Double>> set = new TreeSet<Map.Entry<String, Double>>(new Comparator<Map.Entry<String, Double>>() {
						@Override
						public int compare(Map.Entry<String, Double> entry1, Map.Entry<String, Double> entry2) {
							return entry2.getValue().compareTo(entry1.getValue());
						}
					});
					set.add(new AbstractMap.SimpleEntry<String, Double>(pageName, tfidf));
					invIndex.put(term, set);
				}
				else {
					Set<Map.Entry<String, Double>> set = invIndex.get(term);
					set.add(new AbstractMap.SimpleEntry<String, Double>(pageName, tfidf));
				}
			}
		}
		return invIndex;
	}

	@Override
	public Collection<Entry<String, List<String>>> buildHomePage(Map<?, ?> invertedIndex) {
		Collection<Entry<String, List<String>>> results = new TreeSet<Entry<String, List<String>>>(new Comparator<Entry<String, List<String>>>() {
			@Override
			public int compare(Entry<String, List<String>> entry1, Entry<String, List<String>> entry2) {
				int articleNum = entry2.getValue().size() - entry1.getValue().size();
				if (articleNum == 0) {
					return entry2.getKey().compareTo(entry1.getKey());
				}
				return articleNum;
			}
		});
		for (Map.Entry<?, ?> entry : invertedIndex.entrySet()) {
			String term = (String) entry.getKey();
			if (IIndexBuilder.STOPWORDS.contains(term)) {
				continue;
			}
			@SuppressWarnings("unchecked")
			Set<Map.Entry<String, Double>> set = (Set<Entry<String, Double>>) entry.getValue();
			List<String> articleList = new ArrayList<>();
			for (Entry<String, Double> e : set) {
				articleList.add(e.getKey());
			}
			results.add(new AbstractMap.SimpleEntry<String, List<String>>(term, articleList));
		}
		return results;
	}

	@Override
	public Collection<?> createAutocompleteFile(Collection<Entry<String, List<String>>> homepage) {
		List<String> ret = new ArrayList<>();
		for (Entry<String, List<String>> entry : homepage) {
			String term = entry.getKey();
			ret.add(term);
		}
		File file = new File("/Users/danmeixu/OneDrive/Documents Surface/Courses/CIT 594/Hw6 - Search Engine/autocomplete.txt");
		try {
			PrintWriter writer = new PrintWriter(file);
			writer.println(homepage.size());
			for (Entry<String, List<String>> entry : homepage) {
				String term = entry.getKey();
				writer.println("0" + " " + term);
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collections.sort(ret);
		return ret;
	}

	@Override
	public List<String> searchArticles(String queryTerm, Map<?, ?> invertedIndex) {
		List<String> ret = new ArrayList<>();
		@SuppressWarnings("unchecked")
		Set<Map.Entry<String, Double>> set = (Set<Entry<String, Double>>) invertedIndex.get(queryTerm);
		for (Entry<String, Double> entry : set) {
			ret.add(entry.getKey());
		}
		return ret;
	}

}
