package com.minisearch.crawler;

import com.minisearch.model.WebPage;
import java.util.List;

/**
 * Interface for web crawling functionality
 */
public interface WebCrawler {
    
    /**
     * Crawl a single URL and return the parsed web page
     * @param url The URL to crawl
     * @return The parsed WebPage object
     */
    WebPage crawlUrl(String url);
    
    /**
     * Crawl multiple URLs
     * @param urls List of URLs to crawl
     * @return List of parsed WebPage objects
     */
    List<WebPage> crawlUrls(List<String> urls);
    
    /**
     * Start the crawler with a seed URL
     * @param seedUrl The starting URL for crawling
     * @param maxPages Maximum number of pages to crawl
     */
    void startCrawling(String seedUrl, int maxPages);
    
    /**
     * Stop the crawler
     */
    void stopCrawling();
    
    /**
     * Check if the crawler is currently running
     * @return true if crawling, false otherwise
     */
    boolean isCrawling();
    
    /**
     * Get the current crawling statistics
     * @return CrawlingStats object
     */
    CrawlingStats getCrawlingStats();
}

