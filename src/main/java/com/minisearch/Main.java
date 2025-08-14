package com.minisearch;

import com.minisearch.crawler.SimpleWebCrawler;
import com.minisearch.crawler.CrawlerConfig;
import com.minisearch.model.WebPage;
import com.minisearch.indexer.SimpleSearchIndexer;
import com.minisearch.indexer.DocumentIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

/**
 * Main class for the Mini Search Engine
 * This is the entry point for the application
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting Mini Search Engine...");
        
        try {
            // Demonstrate the crawler functionality
            demonstrateCrawler();
            
            // Demonstrate the indexing functionality
            demonstrateIndexer();
            
            logger.info("Mini Search Engine demo completed!");
            
        } catch (Exception e) {
            logger.error("Error in Mini Search Engine", e);
            System.exit(1);
        }
    }
    
    /**
     * Demonstrate the web crawler functionality
     */
    private static void demonstrateCrawler() {
        logger.info("=== Web Crawler Demo ===");
        
        // Create crawler configuration
        CrawlerConfig config = CrawlerConfig.builder()
            .maxPages(5)
            .delayMs(2000) // 2 seconds between requests
            .timeoutMs(10000)
            .threadPoolSize(2)
            .userAgent("MiniSearch-Engine-Demo/1.0")
            .build();
        
        logger.info("Crawler configuration: {}", config);
        
        // Create crawler instance
        SimpleWebCrawler crawler = new SimpleWebCrawler(config.getThreadPoolSize());
        
        try {
            // Demo 1: Crawl a single URL
            logger.info("\n--- Demo 1: Crawl Single URL ---");
            String testUrl = "https://httpbin.org/html";
            logger.info("Crawling single URL: {}", testUrl);
            
            WebPage page = crawler.crawlUrl(testUrl);
            if (page != null) {
                logger.info("Successfully crawled: {}", page.getUrl());
                logger.info("Title: {}", page.getTitle());
                logger.info("Content length: {} characters", page.getContentLength());
                logger.info("Links found: {}", page.getLinks() != null ? page.getLinks().size() : 0);
            } else {
                logger.warn("Failed to crawl URL: {}", testUrl);
            }
            
            // Demo 2: Crawl multiple URLs
            logger.info("\n--- Demo 2: Crawl Multiple URLs ---");
            List<String> urls = List.of(
                "https://httpbin.org/html",
                "https://httpbin.org/json",
                "https://httpbin.org/xml"
            );
            
            logger.info("Crawling multiple URLs: {}", urls);
            List<WebPage> pages = crawler.crawlUrls(urls);
            logger.info("Successfully crawled {} out of {} URLs", pages.size(), urls.size());
            
            // Demo 3: Show crawler statistics
            logger.info("\n--- Demo 3: Crawler Statistics ---");
            var stats = crawler.getCrawlingStats();
            logger.info("Crawling statistics: {}", stats);
            
            // Demo 4: Interactive crawling
            logger.info("\n--- Demo 4: Interactive Crawling ---");
            interactiveCrawling(crawler);
            
        } finally {
            // Cleanup
            crawler.shutdown();
            logger.info("Crawler shutdown completed");
        }
    }
    
    /**
     * Interactive crawling demo
     */
    private static void interactiveCrawling(SimpleWebCrawler crawler) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            logger.info("Enter a URL to crawl (or 'quit' to exit):");
            String input = scanner.nextLine().trim();
            
            if ("quit".equalsIgnoreCase(input)) {
                logger.info("Interactive demo ended");
                return;
            }
            
            if (!input.isEmpty()) {
                logger.info("Crawling URL: {}", input);
                WebPage page = crawler.crawlUrl(input);
                
                if (page != null) {
                    logger.info("✓ Successfully crawled: {}", page.getUrl());
                    logger.info("  Title: {}", page.getTitle() != null ? page.getTitle() : "No title");
                    logger.info("  Content: {} characters", page.getContentLength());
                    logger.info("  Domain: {}", page.getDomain() != null ? page.getDomain() : "Unknown");
                    
                    if (page.getLinks() != null && !page.getLinks().isEmpty()) {
                        logger.info("  Links found: {}", page.getLinks().size());
                        // Show first few links
                        page.getLinks().stream()
                            .limit(3)
                            .forEach(link -> logger.info("    - {}", link));
                        if (page.getLinks().size() > 3) {
                            logger.info("    ... and {} more", page.getLinks().size() - 3);
                        }
                    }
                } else {
                    logger.warn("✗ Failed to crawl URL: {}", input);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error during interactive crawling", e);
        }
    }
    
    /**
     * Demonstrate the search indexer functionality
     */
    private static void demonstrateIndexer() {
        logger.info("\n=== Search Indexer Demo ===");
        
        // Create indexer instance
        SimpleSearchIndexer indexer = new SimpleSearchIndexer();
        
        try {
            // Demo 1: Index sample web pages
            logger.info("\n--- Demo 1: Index Sample Pages ---");
            
            WebPage samplePage1 = new WebPage("https://example.com/java");
            samplePage1.setTitle("Java Programming Guide");
            samplePage1.setContent("Java is a popular programming language used for web development, Android apps, and enterprise software. Java provides object-oriented programming features and platform independence.");
            samplePage1.setDomain("example.com");
            
            WebPage samplePage2 = new WebPage("https://example.com/python");
            samplePage2.setTitle("Python Tutorial");
            samplePage2.setContent("Python is a versatile programming language great for data science, machine learning, web development, and automation. Python has simple syntax and extensive libraries.");
            samplePage2.setDomain("example.com");
            
            WebPage samplePage3 = new WebPage("https://example.com/javascript");
            samplePage3.setTitle("JavaScript Basics");
            samplePage3.setContent("JavaScript is the language of the web. It runs in browsers and enables interactive web pages. JavaScript is essential for modern web development.");
            samplePage3.setDomain("example.com");
            
            // Index the pages
            indexer.indexPage(samplePage1);
            indexer.indexPage(samplePage2);
            indexer.indexPage(samplePage3);
            
            logger.info("Indexed {} pages", indexer.getIndexSize());
            
            // Demo 2: Search functionality
            logger.info("\n--- Demo 2: Search Functionality ---");
            
            // Search for "programming"
            List<DocumentIndex> programmingResults = indexer.search(Arrays.asList("programming"));
            logger.info("Search for 'programming': {} results", programmingResults.size());
            for (DocumentIndex doc : programmingResults) {
                logger.info("  - {}: {} (score: {})", doc.getTitle(), doc.getUrl(), String.format("%.2f", doc.getRelevanceScore()));
            }
            
            // Search for "Java" AND "development"
            List<DocumentIndex> javaDevResults = indexer.search(Arrays.asList("java", "development"));
            logger.info("Search for 'Java' AND 'development': {} results", javaDevResults.size());
            for (DocumentIndex doc : javaDevResults) {
                logger.info("  - {}: {} (score: {})", doc.getTitle(), doc.getUrl(), String.format("%.2f", doc.getRelevanceScore()));
            }
            
            // Search for "web" OR "data"
            List<DocumentIndex> webOrDataResults = indexer.searchAny(Arrays.asList("web", "data"));
            logger.info("Search for 'web' OR 'data': {} results", webOrDataResults.size());
            for (DocumentIndex doc : webOrDataResults) {
                logger.info("  - {}: {} (score: {})", doc.getTitle(), doc.getUrl(), String.format("%.2f", doc.getRelevanceScore()));
            }
            
            // Demo 3: Index statistics
            logger.info("\n--- Demo 3: Index Statistics ---");
            var indexStats = indexer.getIndexStats();
            logger.info("Index statistics: {}", indexStats);
            
            var indexingStats = indexer.getIndexingStats();
            logger.info("Indexing statistics: {}", indexingStats);
            
            // Demo 4: Term analysis
            logger.info("\n--- Demo 4: Term Analysis ---");
            List<String> allTerms = indexer.getAllIndexedTerms();
            logger.info("Total unique terms indexed: {}", allTerms.size());
            
            // Show first 10 terms
            allTerms.stream()
                .limit(10)
                .forEach(term -> {
                    int docCount = indexer.getDocumentsForTerm(term).size();
                    logger.info("  - '{}': found in {} documents", term, docCount);
                });
            
            // Demo 5: Interactive search
            logger.info("\n--- Demo 5: Interactive Search ---");
            interactiveSearch(indexer);
            
        } catch (Exception e) {
            logger.error("Error during indexer demo", e);
        }
    }
    
    /**
     * Interactive search demo
     */
    private static void interactiveSearch(SimpleSearchIndexer indexer) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            logger.info("Enter search terms (space-separated, or 'quit' to exit):");
            String input = scanner.nextLine().trim();
            
            if ("quit".equalsIgnoreCase(input)) {
                logger.info("Interactive search demo ended");
                return;
            }
            
            if (!input.isEmpty()) {
                String[] terms = input.split("\\s+");
                List<String> searchTerms = Arrays.asList(terms);
                
                logger.info("Searching for: {}", searchTerms);
                
                // Perform search
                List<DocumentIndex> results = indexer.search(searchTerms);
                
                if (results.isEmpty()) {
                    logger.info("No results found for: {}", searchTerms);
                } else {
                    logger.info("Found {} results:", results.size());
                    for (int i = 0; i < Math.min(results.size(), 5); i++) {
                        DocumentIndex doc = results.get(i);
                        logger.info("  {}. {} (score: {})", i + 1, doc.getTitle(), String.format("%.2f", doc.getRelevanceScore()));
                        logger.info("     URL: {}", doc.getUrl());
                        logger.info("     Content: {} characters", doc.getContentLength());
                    }
                    if (results.size() > 5) {
                        logger.info("     ... and {} more results", results.size() - 5);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error during interactive search", e);
        }
    }
}
