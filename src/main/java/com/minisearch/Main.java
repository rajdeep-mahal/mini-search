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
}
