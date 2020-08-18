package test;

import static org.junit.Assert.*;
import java.util.*;
import java.util.Map.Entry;

import org.junit.Test;
import indexing.*;


/**
 * @author ericfouh
 */
public class TestIndexBuilder
{
    /**
     * Test that:
	-  map has the correct number of files
	-  map contains the names of the documents (URLs/keys)
	-  map contains the correct number of terms in the lists (values)
     */
    @Test
    public void testIndexBuilder() {
    	IIndexBuilder ib = new IndexBuilder();
    	List<String> feeds = new ArrayList<>();
    	feeds.add("http://localhost:8090/sample_rss_feed.xml");
    	Map<String, List<String>> results = ib.parseFeed(feeds);
    	assertEquals(results.size(), 5);
    	assertTrue(results.containsKey("http://localhost:8090/page1.html"));
    	assertTrue(results.containsKey("http://localhost:8090/page2.html"));
    	assertTrue(results.containsKey("http://localhost:8090/page3.html"));
    	assertTrue(results.containsKey("http://localhost:8090/page4.html"));
    	assertTrue(results.containsKey("http://localhost:8090/page5.html"));
    	assertEquals(results.get("http://localhost:8090/page1.html").size(), 10);
    	assertEquals(results.get("http://localhost:8090/page2.html").size(), 55);
    	assertEquals(results.get("http://localhost:8090/page3.html").size(), 33);
    	assertEquals(results.get("http://localhost:8090/page4.html").size(), 22);
    	assertEquals(results.get("http://localhost:8090/page5.html").size(), 18);

    }
    
    /**
     * Test that:
	-  map contains the names of the documents (URLs/keys)
	-  map contains the correct value for each term in document (values)
     */
    @Test
    public void testBuildIndex() {
    	IIndexBuilder ib = new IndexBuilder();
    	List<String> feeds  = new ArrayList<>();
    	feeds.add("http://localhost:8090/sample_rss_feed.xml");
    	Map<String, List<String>> results = ib.parseFeed(feeds);
    	Map<String, Map<String, Double>> fwdIndex = ib.buildIndex(results);
    	Map<String, Double> page1 = fwdIndex.get("http://localhost:8090/page1.html");
    	assertEquals(page1.get("structures"), 0.183, 0.001);
    	Map<String, Double> page5 = fwdIndex.get("http://localhost:8090/page5.html");
    	assertEquals(page5.get("completely"), 0.089, 0.001);
    }
    
    /**
     * Test that:
	-  map is of the correct type (of Map)
	-  map associates the correct files to a term
	-  map stores the documents in the correct order
     */
    @Test
    public void testBuildInvertedIndex() {
    	IIndexBuilder ib = new IndexBuilder();
    	List<String> feeds  = new ArrayList<>();
    	feeds.add("http://localhost:8090/sample_rss_feed.xml");
    	Map<String, List<String>> results = ib.parseFeed(feeds);
    	Map<String, Map<String, Double>> fwdIndex = ib.buildIndex(results);
    	Map<?, ?> invIndex = ib.buildInvertedIndex(fwdIndex);
    	assertEquals(invIndex.size(), 92);
    	@SuppressWarnings("unchecked")
		SortedSet<Map.Entry<String, Double>> set = (SortedSet<Entry<String, Double>>) invIndex.get("structures");
    	assertEquals(set.first().getKey(), "http://localhost:8090/page1.html");
    	assertEquals(set.first().getValue(), 0.183, 0.001);
    	assertEquals(set.last().getKey(), "http://localhost:8090/page2.html");
    	assertEquals(set.last().getValue(), 0.066, 0.001);
    }
    
    /**
     * Test that:
	-  Collection is of the correct type
	-  collection stores the entries are in the correct order
     */
    @Test
    public void testBuildHomePage() {
    	IIndexBuilder ib = new IndexBuilder();
    	List<String> feeds  = new ArrayList<>();
    	feeds.add("http://localhost:8090/sample_rss_feed.xml");
    	Map<String, List<String>> results = ib.parseFeed(feeds);
    	Map<String, Map<String, Double>> fwdIndex = ib.buildIndex(results);
    	Map<?, ?> invIndex = ib.buildInvertedIndex(fwdIndex);
    	SortedSet<Entry<String, List<String>>> homepage = (SortedSet<Entry<String, List<String>>>) ib.buildHomePage(invIndex);
    	assertEquals(homepage.first().getKey(), "data");
    	assertEquals(homepage.first().getValue().size(), 3);
    	assertEquals(homepage.last().getKey(), "allows");
    	assertEquals(homepage.last().getValue().size(), 1);
    }
    
    /**
     * Test that:
	-  list contains the correct number of articles
	-  list contains the correct of articles
     */
    @Test
    public void testSearchArticles() {
    	IIndexBuilder ib = new IndexBuilder();
    	List<String> feeds  = new ArrayList<>();
    	feeds.add("http://localhost:8090/sample_rss_feed.xml");
    	Map<String, List<String>> results = ib.parseFeed(feeds);
    	Map<String, Map<String, Double>> fwdIndex = ib.buildIndex(results);
    	Map<?, ?> invIndex = ib.buildInvertedIndex(fwdIndex);
    	List<String> articles = ib.searchArticles("structures", invIndex);
    	assertEquals(articles.size(), 2);
    	assertEquals(articles.get(0), "http://localhost:8090/page1.html");
    	assertEquals(articles.get(1), "http://localhost:8090/page2.html");
    }
    
    /**
     * Test that:
	-  collection is of the correct type
	-  collection contains the correct number of words
     */
    @Test
    public void testCreateAutocompleteFiles() {
    	IIndexBuilder ib = new IndexBuilder();
    	List<String> feeds  = new ArrayList<>();
    	feeds.add("http://localhost:8090/sample_rss_feed.xml");
    	Map<String, List<String>> results = ib.parseFeed(feeds);
    	Map<String, Map<String, Double>> fwdIndex = ib.buildIndex(results);
    	Map<?, ?> invIndex = ib.buildInvertedIndex(fwdIndex);
    	Collection<Entry<String, List<String>>> homepage = ib.buildHomePage(invIndex);
    	Collection<?> file = ib.createAutocompleteFile(homepage);
    }
    
    
}
