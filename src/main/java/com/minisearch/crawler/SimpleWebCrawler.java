package com.minisearch.crawler;

import com.minisearch.model.WebPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * Simple implementation of the WebCrawler interface
 */
public class SimpleWebCrawler implements WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(SimpleWebCrawler.class);
    
    // Configuration
    private static final int DEFAULT_MAX_PAGES = 100;
    private static final int DEFAULT_DELAY_MS = 1000; // 1 second delay between requests
    private static final int DEFAULT_TIMEOUT_MS = 10000; // 10 second timeout
    private static final int DEFAULT_MAX_DEPTH = 3;
    
    // Crawling state
    private volatile boolean isCrawling = false;
    private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
    private final Queue<String> urlQueue = new ConcurrentLinkedQueue<>();
    private final CrawlingStats stats = new CrawlingStats();
    private final ExecutorService executorService;
    
    // URL patterns to exclude
    private static final Pattern EXCLUDE_PATTERNS = Pattern.compile(
        ".*\\.(pdf|doc|docx|ppt|pptx|xls|xlsx|zip|rar|exe|dmg|pkg|jpg|jpeg|png|gif|mp3|mp4|avi|mov)$"
    );
    
    public SimpleWebCrawler() {
        this.executorService = Executors.newFixedThreadPool(5);
    }
    
    public SimpleWebCrawler(int threadPoolSize) {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }
    
    @Override
    public WebPage crawlUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            logger.warn("Attempted to crawl null or empty URL");
            return null;
        }
        
        try {
            logger.debug("Crawling URL: {}", url);
            long startTime = System.currentTimeMillis();
            
            // Fetch the page
            Document doc = Jsoup.connect(url)
                    .timeout(DEFAULT_TIMEOUT_MS)
                    .userAgent("MiniSearch-Engine/1.0")
                    .get();
            
            // Parse the page
            WebPage webPage = parseDocument(doc, url);
            
            long crawlTime = System.currentTimeMillis() - startTime;
            stats.recordSuccessfulCrawl(crawlTime);
            
            logger.debug("Successfully crawled {} in {}ms", url, crawlTime);
            return webPage;
            
        } catch (IOException e) {
            logger.warn("Failed to crawl URL: {} - {}", url, e.getMessage());
            stats.recordFailedCrawl();
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error crawling URL: {} - {}", url, e.getMessage(), e);
            stats.recordFailedCrawl();
            return null;
        }
    }
    
    @Override
    public List<WebPage> crawlUrls(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<WebPage> results = new ArrayList<>();
        for (String url : urls) {
            WebPage page = crawlUrl(url);
            if (page != null) {
                results.add(page);
            }
        }
        
        return results;
    }
    
    @Override
    public void startCrawling(String seedUrl, int maxPages) {
        if (isCrawling) {
            logger.warn("Crawler is already running");
            return;
        }
        
        if (seedUrl == null || seedUrl.trim().isEmpty()) {
            logger.error("Seed URL cannot be null or empty");
            return;
        }
        
        logger.info("Starting crawler with seed URL: {} (max pages: {})", seedUrl, maxPages);
        
        isCrawling = true;
        visitedUrls.clear();
        urlQueue.clear();
        stats.setPagesInQueue(0);
        
        // Add seed URL to queue
        urlQueue.offer(seedUrl);
        stats.setPagesInQueue(1);
        
        // Start crawling in background
        executorService.submit(() -> crawlLoop(maxPages));
    }
    
    @Override
    public void stopCrawling() {
        if (!isCrawling) {
            return;
        }
        
        logger.info("Stopping crawler...");
        isCrawling = false;
        
        // Shutdown executor service
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    @Override
    public boolean isCrawling() {
        return isCrawling;
    }
    
    @Override
    public CrawlingStats getCrawlingStats() {
        return stats;
    }
    
    /**
     * Main crawling loop
     */
    private void crawlLoop(int maxPages) {
        int pagesCrawled = 0;
        
        while (isCrawling && pagesCrawled < maxPages && !urlQueue.isEmpty()) {
            String url = urlQueue.poll();
            if (url == null) {
                continue;
            }
            
            stats.setPagesInQueue(urlQueue.size());
            
            // Skip if already visited
            if (visitedUrls.contains(url)) {
                continue;
            }
            
            // Skip if URL should be excluded
            if (shouldExcludeUrl(url)) {
                continue;
            }
            
            // Crawl the URL
            WebPage page = crawlUrl(url);
            if (page != null) {
                visitedUrls.add(url);
                pagesCrawled++;
                
                // Extract and add new URLs to queue
                if (page.getLinks() != null) {
                    for (String link : page.getLinks()) {
                        if (!visitedUrls.contains(link) && !urlQueue.contains(link)) {
                            urlQueue.offer(link);
                        }
                    }
                    stats.setPagesInQueue(urlQueue.size());
                }
                
                logger.info("Crawled {}/{} pages: {}", pagesCrawled, maxPages, url);
            }
            
            // Respect rate limiting
            try {
                Thread.sleep(DEFAULT_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        isCrawling = false;
        logger.info("Crawling completed. Total pages crawled: {}", pagesCrawled);
    }
    
    /**
     * Parse a Jsoup Document into a WebPage object
     */
    private WebPage parseDocument(Document doc, String url) {
        WebPage webPage = new WebPage(url);
        
        // Extract title
        Element titleElement = doc.selectFirst("title");
        if (titleElement != null) {
            webPage.setTitle(titleElement.text().trim());
        }
        
        // Extract text content
        String textContent = doc.text();
        webPage.setContent(textContent);
        
        // Extract HTML content
        webPage.setHtmlContent(doc.html());
        
        // Extract links
        List<String> links = new ArrayList<>();
        Elements linkElements = doc.select("a[href]");
        for (Element link : linkElements) {
            String href = link.attr("abs:href");
            if (href != null && !href.isEmpty()) {
                links.add(href);
            }
        }
        webPage.setLinks(links);
        
        // Extract domain
        try {
            URL urlObj = new URL(url);
            webPage.setDomain(urlObj.getHost());
        } catch (Exception e) {
            logger.warn("Could not extract domain from URL: {}", url);
        }
        
        // Extract metadata
        Map<String, String> metadata = new HashMap<>();
        Elements metaElements = doc.select("meta");
        for (Element meta : metaElements) {
            String name = meta.attr("name");
            String content = meta.attr("content");
            if (name != null && !name.isEmpty() && content != null && !content.isEmpty()) {
                metadata.put(name, content);
            }
        }
        webPage.setMetadata(metadata);
        
        return webPage;
    }
    
    /**
     * Check if a URL should be excluded from crawling
     */
    private boolean shouldExcludeUrl(String url) {
        if (url == null) return true;
        
        // Check file extensions
        if (EXCLUDE_PATTERNS.matcher(url.toLowerCase()).matches()) {
            return true;
        }
        
        // Check for common non-HTML protocols
        if (url.startsWith("mailto:") || url.startsWith("tel:") || url.startsWith("javascript:")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Cleanup resources
     */
    public void shutdown() {
        stopCrawling();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
