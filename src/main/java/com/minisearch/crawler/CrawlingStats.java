package com.minisearch.crawler;

import java.time.LocalDateTime;

/**
 * Statistics and metrics for web crawling operations
 */
public class CrawlingStats {
    private int totalPagesCrawled;
    private int successfulCrawls;
    private int failedCrawls;
    private LocalDateTime startTime;
    private LocalDateTime lastCrawlTime;
    private long totalCrawlTime;
    private double averageCrawlTime;
    private int pagesInQueue;
    
    public CrawlingStats() {
        this.startTime = LocalDateTime.now();
        this.lastCrawlTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getTotalPagesCrawled() { return totalPagesCrawled; }
    public void setTotalPagesCrawled(int totalPagesCrawled) { this.totalPagesCrawled = totalPagesCrawled; }
    
    public int getSuccessfulCrawls() { return successfulCrawls; }
    public void setSuccessfulCrawls(int successfulCrawls) { this.successfulCrawls = successfulCrawls; }
    
    public int getFailedCrawls() { return failedCrawls; }
    public void setFailedCrawls(int failedCrawls) { this.failedCrawls = failedCrawls; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getLastCrawlTime() { return lastCrawlTime; }
    public void setLastCrawlTime(LocalDateTime lastCrawlTime) { this.lastCrawlTime = lastCrawlTime; }
    
    public long getTotalCrawlTime() { return totalCrawlTime; }
    public void setTotalCrawlTime(long totalCrawlTime) { this.totalCrawlTime = totalCrawlTime; }
    
    public double getAverageCrawlTime() { return averageCrawlTime; }
    public void setAverageCrawlTime(double averageCrawlTime) { this.averageCrawlTime = averageCrawlTime; }
    
    public int getPagesInQueue() { return pagesInQueue; }
    public void setPagesInQueue(int pagesInQueue) { this.pagesInQueue = pagesInQueue; }
    
    /**
     * Update statistics after a successful crawl
     */
    public void recordSuccessfulCrawl(long crawlTime) {
        this.successfulCrawls++;
        this.totalPagesCrawled++;
        this.lastCrawlTime = LocalDateTime.now();
        this.totalCrawlTime += crawlTime;
        this.averageCrawlTime = (double) this.totalCrawlTime / this.totalPagesCrawled;
    }
    
    /**
     * Update statistics after a failed crawl
     */
    public void recordFailedCrawl() {
        this.failedCrawls++;
        this.totalPagesCrawled++;
        this.lastCrawlTime = LocalDateTime.now();
    }
    
    /**
     * Get success rate as a percentage
     */
    public double getSuccessRate() {
        if (totalPagesCrawled == 0) return 0.0;
        return (double) successfulCrawls / totalPagesCrawled * 100;
    }
    
    @Override
    public String toString() {
        return "CrawlingStats{" +
                "totalPagesCrawled=" + totalPagesCrawled +
                ", successfulCrawls=" + successfulCrawls +
                ", failedCrawls=" + failedCrawls +
                ", successRate=" + String.format("%.2f", getSuccessRate()) + "%" +
                ", averageCrawlTime=" + String.format("%.2f", averageCrawlTime) + "ms" +
                ", pagesInQueue=" + pagesInQueue +
                '}';
    }
}

