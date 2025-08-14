package com.minisearch.indexer;

import com.minisearch.model.WebPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SimpleSearchIndexer implementation
 */
@DisplayName("SimpleSearchIndexer Tests")
public class SimpleSearchIndexerTest {
    
    private SimpleSearchIndexer indexer;
    private WebPage testPage1;
    private WebPage testPage2;
    
    @BeforeEach
    void setUp() {
        indexer = new SimpleSearchIndexer();
        
        // Create test web pages
        testPage1 = new WebPage();
        testPage1.setUrl("https://example.com/page1");
        testPage1.setTitle("Java Programming Guide");
        testPage1.setContent("Java is a popular programming language. Java is used for web development and Android apps.");
        testPage1.setDomain("example.com");
        
        testPage2 = new WebPage();
        testPage2.setUrl("https://example.com/page2");
        testPage2.setTitle("Python Programming Tutorial");
        testPage2.setContent("Python is another programming language. Python is great for data science and machine learning.");
        testPage2.setDomain("example.com");
    }
    
    @Test
    @DisplayName("Should index a single web page")
    void testIndexSinglePage() {
        // Index the page
        indexer.indexPage(testPage1);
        
        // Verify indexing
        assertTrue(indexer.isIndexed(testPage1.getUrl()));
        assertEquals(1, indexer.getIndexSize());
        
        // Check statistics
        IndexingStats stats = indexer.getIndexingStats();
        assertEquals(1, stats.getTotalPagesIndexed());
        assertEquals(1, stats.getIndexSize());
    }
    
    @Test
    @DisplayName("Should index multiple web pages")
    void testIndexMultiplePages() {
        List<WebPage> pages = Arrays.asList(testPage1, testPage2);
        
        // Index multiple pages
        indexer.indexPages(pages);
        
        // Verify indexing
        assertTrue(indexer.isIndexed(testPage1.getUrl()));
        assertTrue(indexer.isIndexed(testPage2.getUrl()));
        assertEquals(2, indexer.getIndexSize());
        
        // Check statistics
        IndexingStats stats = indexer.getIndexingStats();
        assertEquals(2, stats.getTotalPagesIndexed());
        assertEquals(2, stats.getIndexSize());
    }
    
    @Test
    @DisplayName("Should search for documents containing specific terms")
    void testSearchFunctionality() {
        // Index both pages
        indexer.indexPage(testPage1);
        indexer.indexPage(testPage2);
        
        // Search for "Java"
        List<DocumentIndex> javaResults = indexer.search(Arrays.asList("java"));
        assertEquals(1, javaResults.size());
        assertEquals(testPage1.getUrl(), javaResults.get(0).getUrl());
        
        // Search for "programming"
        List<DocumentIndex> programmingResults = indexer.search(Arrays.asList("programming"));
        assertEquals(2, programmingResults.size());
        
        // Search for "Java" AND "programming" (both terms)
        List<DocumentIndex> bothTermsResults = indexer.search(Arrays.asList("java", "programming"));
        assertEquals(1, bothTermsResults.size());
        assertEquals(testPage1.getUrl(), bothTermsResults.get(0).getUrl());
    }
    
    @Test
    @DisplayName("Should handle search with no results")
    void testSearchNoResults() {
        // Index a page
        indexer.indexPage(testPage1);
        
        // Search for non-existent term
        List<DocumentIndex> results = indexer.search(Arrays.asList("nonexistent"));
        assertTrue(results.isEmpty());
    }
    
    @Test
    @DisplayName("Should remove indexed pages")
    void testRemovePage() {
        // Index a page
        indexer.indexPage(testPage1);
        assertEquals(1, indexer.getIndexSize());
        
        // Remove the page
        indexer.removePage(testPage1.getUrl());
        assertEquals(0, indexer.getIndexSize());
        assertFalse(indexer.isIndexed(testPage1.getUrl()));
        
        // Check statistics
        IndexingStats stats = indexer.getIndexingStats();
        assertEquals(1, stats.getPagesRemoved());
    }
    
    @Test
    @DisplayName("Should update existing indexed pages")
    void testUpdatePage() {
        // Index a page
        indexer.indexPage(testPage1);
        
        // Update the page content
        testPage1.setContent("Updated Java content with new information.");
        
        // Update the indexed page
        indexer.updatePage(testPage1);
        
        // Verify update
        assertTrue(indexer.isIndexed(testPage1.getUrl()));
        assertEquals(1, indexer.getIndexSize());
        
        // Check statistics
        IndexingStats stats = indexer.getIndexingStats();
        assertEquals(1, stats.getPagesUpdated());
    }
    
    @Test
    @DisplayName("Should handle null and invalid inputs gracefully")
    void testInvalidInputs() {
        // Test null web page
        indexer.indexPage(null);
        assertEquals(0, indexer.getIndexSize());
        
        // Test null URL
        WebPage invalidPage = new WebPage();
        invalidPage.setUrl(null);
        invalidPage.setContent("Some content");
        indexer.indexPage(invalidPage);
        assertEquals(0, indexer.getIndexSize());
        
        // Test empty URL
        invalidPage.setUrl("");
        indexer.indexPage(invalidPage);
        assertEquals(0, indexer.getIndexSize());
    }
    
    @Test
    @DisplayName("Should clear entire index")
    void testClearIndex() {
        // Index some pages
        indexer.indexPage(testPage1);
        indexer.indexPage(testPage2);
        assertEquals(2, indexer.getIndexSize());
        
        // Clear the index
        indexer.clearIndex();
        assertEquals(0, indexer.getIndexSize());
        assertFalse(indexer.isIndexed(testPage1.getUrl()));
        assertFalse(indexer.isIndexed(testPage2.getUrl()));
    }
    
    @Test
    @DisplayName("Should get index statistics")
    void testIndexStatistics() {
        // Index some pages
        indexer.indexPage(testPage1);
        indexer.indexPage(testPage2);
        
        // Get statistics
        var stats = indexer.getIndexStats();
        
        assertNotNull(stats);
        assertEquals(2, stats.get("totalDocuments"));
        assertTrue((Integer) stats.get("totalTerms") > 0);
        assertTrue((Integer) stats.get("uniqueTerms") > 0);
        
        // Check most common terms
        @SuppressWarnings("unchecked")
        List<java.util.Map.Entry<String, Integer>> commonTerms = (List<java.util.Map.Entry<String, Integer>>) stats.get("mostCommonTerms");
        assertNotNull(commonTerms);
        assertFalse(commonTerms.isEmpty());
    }
    
    @Test
    @DisplayName("Should process text correctly")
    void testTextProcessing() {
        // Index a page
        indexer.indexPage(testPage1);
        
        // Get all indexed terms
        List<String> terms = indexer.getAllIndexedTerms();
        assertFalse(terms.isEmpty());
        
        // Verify that stop words are filtered out
        assertFalse(terms.contains("a"));
        assertFalse(terms.contains("is"));
        assertFalse(terms.contains("for"));
        
        // Verify that meaningful terms are included
        assertTrue(terms.contains("java"));
        assertTrue(terms.contains("programming"));
        assertTrue(terms.contains("language"));
    }
    
    @Test
    @DisplayName("Should handle empty content gracefully")
    void testEmptyContent() {
        WebPage emptyPage = new WebPage();
        emptyPage.setUrl("https://example.com/empty");
        emptyPage.setTitle("Empty Page");
        emptyPage.setContent("");
        emptyPage.setDomain("example.com");
        
        // Index the empty page
        indexer.indexPage(emptyPage);
        
        // Should still be indexed
        assertTrue(indexer.isIndexed(emptyPage.getUrl()));
        assertEquals(1, indexer.getIndexSize());
        
        // But should have no terms
        List<String> terms = indexer.getAllIndexedTerms();
        assertEquals(0, terms.size());
    }
}
