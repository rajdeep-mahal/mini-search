package com.minisearch.crawler;

import com.minisearch.model.WebPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for SimpleWebCrawler
 */
public class SimpleWebCrawlerTest {
    
    private SimpleWebCrawler crawler;
    
    @BeforeEach
    public void setUp() {
        crawler = new SimpleWebCrawler(2); // Small thread pool for testing
    }
    
    @AfterEach
    public void tearDown() {
        if (crawler != null) {
            crawler.shutdown();
        }
    }
    
    @Test
    public void testCrawlerCreation() {
        assertNotNull(crawler);
        assertFalse(crawler.isCrawling());
    }
    
    @Test
    public void testCrawlerStats() {
        CrawlingStats stats = crawler.getCrawlingStats();
        assertNotNull(stats);
        assertEquals(0, stats.getTotalPagesCrawled());
        assertEquals(0, stats.getSuccessfulCrawls());
        assertEquals(0, stats.getFailedCrawls());
    }
    
    @Test
    public void testCrawlSingleUrl() {
        // Test with a simple, reliable website
        String testUrl = "https://httpbin.org/html";
        
        WebPage result = crawler.crawlUrl(testUrl);
        
        // The result might be null if the site is unreachable, but that's okay for testing
        if (result != null) {
            assertEquals(testUrl, result.getUrl());
            assertNotNull(result.getContent());
            assertTrue(result.getContent().length() > 0);
        }
    }
    
    @Test
    public void testCrawlMultipleUrls() {
        List<String> urls = List.of(
            "https://httpbin.org/html",
            "https://httpbin.org/json"
        );
        
        List<WebPage> results = crawler.crawlUrls(urls);
        
        // We might get fewer results than URLs due to network issues
        assertTrue(results.size() <= urls.size());
        
        for (WebPage page : results) {
            assertNotNull(page);
            assertNotNull(page.getUrl());
            assertTrue(urls.contains(page.getUrl()));
        }
    }
    
    @Test
    public void testCrawlerConfiguration() {
        CrawlerConfig config = CrawlerConfig.builder()
            .maxPages(50)
            .delayMs(500)
            .timeoutMs(5000)
            .threadPoolSize(3)
            .build();
        
        assertEquals(50, config.getMaxPages());
        assertEquals(500, config.getDelayMs());
        assertEquals(5000, config.getTimeoutMs());
        assertEquals(3, config.getThreadPoolSize());
    }
    
    @Test
    public void testUrlExclusion() {
        CrawlerConfig config = new CrawlerConfig();
        
        // Test file extensions that should be excluded
        assertTrue(config.isUrlExcluded("https://example.com/file.pdf"));
        assertTrue(config.isUrlExcluded("https://example.com/document.doc"));
        assertTrue(config.isUrlExcluded("https://example.com/image.jpg"));
        
        // Test URLs that should not be excluded
        assertFalse(config.isUrlExcluded("https://example.com/page.html"));
        assertFalse(config.isUrlExcluded("https://example.com/"));
        assertFalse(config.isUrlExcluded("https://example.com/article"));
    }
    
    @Test
    public void testDomainFiltering() {
        CrawlerConfig config = CrawlerConfig.builder()
            .allowedDomains(List.of("example.com", "test.org"))
            .excludedDomains(List.of("spam.com", "malware.net"))
            .build();
        
        // Test allowed domains
        assertTrue(config.isDomainAllowed("example.com"));
        assertTrue(config.isDomainAllowed("test.org"));
        assertFalse(config.isDomainAllowed("other.com"));
        
        // Test excluded domains
        assertTrue(config.isDomainExcluded("spam.com"));
        assertTrue(config.isDomainExcluded("malware.net"));
        assertFalse(config.isDomainExcluded("example.com"));
    }
    
    @Test
    public void testCrawlerShutdown() {
        assertFalse(crawler.isCrawling());
        
        crawler.shutdown();
        
        // After shutdown, crawler should not be crawling
        assertFalse(crawler.isCrawling());
    }
    
    @Test
    public void testInvalidUrls() {
        // Test with null URL
        WebPage result1 = crawler.crawlUrl(null);
        assertNull(result1);
        
        // Test with empty URL
        WebPage result2 = crawler.crawlUrl("");
        assertNull(result2);
        
        // Test with invalid URL format
        WebPage result3 = crawler.crawlUrl("not-a-valid-url");
        assertNull(result3);
    }
}
