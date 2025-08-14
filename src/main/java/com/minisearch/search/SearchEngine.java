package com.minisearch.search;

import com.minisearch.indexer.DocumentIndex;
import com.minisearch.search.SearchQuery;
import com.minisearch.search.SearchResult;
import com.minisearch.search.SearchFilters;

import java.util.List;

/**
 * Core interface for the search engine
 * Defines search operations and result handling
 */
public interface SearchEngine {
    
    /**
     * Search for documents matching the query
     * @param query The search query
     * @return List of search results
     */
    List<SearchResult> search(SearchQuery query);
    
    /**
     * Search for documents matching the query with filters
     * @param query The search query
     * @param filters Additional search filters
     * @return List of filtered search results
     */
    List<SearchResult> search(SearchQuery query, SearchFilters filters);
    
    /**
     * Search for documents containing specific terms
     * @param terms List of search terms
     * @return List of search results
     */
    List<SearchResult> search(List<String> terms);
    
    /**
     * Search for documents containing ANY of the terms (OR search)
     * @param terms List of search terms
     * @return List of search results
     */
    List<SearchResult> searchAny(List<String> terms);
    
    /**
     * Search for documents containing ALL of the terms (AND search)
     * @param terms List of search terms
     * @return List of search results
     */
    List<SearchResult> searchAll(List<String> terms);
    
    /**
     * Search for documents containing a phrase
     * @param phrase The exact phrase to search for
     * @return List of search results
     */
    List<SearchResult> searchPhrase(String phrase);
    
    /**
     * Get search suggestions for partial queries
     * @param partialQuery Partial search query
     * @return List of suggested search terms
     */
    List<String> getSuggestions(String partialQuery);
    
    /**
     * Get search statistics and metrics
     * @return Search statistics
     */
    SearchStats getSearchStats();
    
    /**
     * Get popular search terms
     * @param limit Maximum number of terms to return
     * @return List of popular search terms
     */
    List<String> getPopularSearchTerms(int limit);
    
    /**
     * Get related search terms for a query
     * @param query The search query
     * @param limit Maximum number of related terms
     * @return List of related search terms
     */
    List<String> getRelatedTerms(String query, int limit);
    
    /**
     * Perform a fuzzy search (handles typos and variations)
     * @param query The search query
     * @param maxDistance Maximum edit distance for fuzzy matching
     * @return List of search results
     */
    List<SearchResult> fuzzySearch(String query, int maxDistance);
    
    /**
     * Search with pagination support
     * @param query The search query
     * @param page Page number (0-based)
     * @param pageSize Number of results per page
     * @return Paginated search results
     */
    PaginatedSearchResult searchWithPagination(SearchQuery query, int page, int pageSize);
    
    /**
     * Get search result count for a query
     * @param query The search query
     * @return Total number of matching documents
     */
    long getResultCount(SearchQuery query);
    
    /**
     * Check if the search engine is ready
     * @return true if ready, false otherwise
     */
    boolean isReady();
    
    /**
     * Get the total number of indexed documents
     * @return Number of indexed documents
     */
    int getTotalDocuments();
    
    /**
     * Get the total number of indexed terms
     * @return Number of indexed terms
     */
    int getTotalTerms();
}

