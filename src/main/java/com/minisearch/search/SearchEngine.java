package com.minisearch.search;

import com.minisearch.model.SearchResult;
import com.minisearch.model.WebPage;
import java.util.List;

/**
 * Interface for the search engine functionality
 */
public interface SearchEngine {
    
    /**
     * Search for pages matching the query
     * @param query The search query
     * @return SearchResult containing matching pages
     */
    SearchResult search(String query);
    
    /**
     * Search with additional parameters
     * @param query The search query
     * @param maxResults Maximum number of results to return
     * @param offset Offset for pagination
     * @return SearchResult containing matching pages
     */
    SearchResult search(String query, int maxResults, int offset);
    
    /**
     * Search with filters
     * @param query The search query
     * @param filters Search filters (domain, date range, etc.)
     * @return SearchResult containing matching pages
     */
    SearchResult search(String query, SearchFilters filters);
    
    /**
     * Get search suggestions for auto-complete
     * @param partialQuery Partial query string
     * @return List of suggested queries
     */
    List<String> getSuggestions(String partialQuery);
    
    /**
     * Get related searches for a query
     * @param query The search query
     * @return List of related queries
     */
    List<String> getRelatedSearches(String query);
    
    /**
     * Get search statistics
     * @return SearchStats object
     */
    SearchStats getSearchStats();
    
    /**
     * Check if the search engine is ready
     * @return true if ready, false otherwise
     */
    boolean isReady();
    
    /**
     * Get the total number of indexed pages
     * @return Number of indexed pages
     */
    int getTotalIndexedPages();
}

