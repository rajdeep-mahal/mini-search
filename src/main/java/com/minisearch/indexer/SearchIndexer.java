package com.minisearch.indexer;

import com.minisearch.model.WebPage;
import java.util.List;

/**
 * Interface for indexing web pages and building search indexes
 */
public interface SearchIndexer {
    
    /**
     * Index a single web page
     * @param webPage The web page to index
     */
    void indexPage(WebPage webPage);
    
    /**
     * Index multiple web pages
     * @param webPages List of web pages to index
     */
    void indexPages(List<WebPage> webPages);
    
    /**
     * Remove a page from the index
     * @param url The URL of the page to remove
     */
    void removePage(String url);
    
    /**
     * Update an existing indexed page
     * @param webPage The updated web page
     */
    void updatePage(WebPage webPage);
    
    /**
     * Get the total number of indexed pages
     * @return Number of indexed pages
     */
    int getIndexSize();
    
    /**
     * Check if a URL is already indexed
     * @param url The URL to check
     * @return true if indexed, false otherwise
     */
    boolean isIndexed(String url);
    
    /**
     * Get indexing statistics
     * @return IndexingStats object
     */
    IndexingStats getIndexingStats();
    
    /**
     * Optimize the index for better performance
     */
    void optimizeIndex();
    
    /**
     * Clear all indexed data
     */
    void clearIndex();
}

