package com.minisearch.indexer;

import java.time.LocalDateTime;

/**
 * Statistics and metrics for indexing operations
 */
public class IndexingStats {
    private int totalPagesIndexed;
    private int pagesIndexedToday;
    private int pagesUpdated;
    private int pagesRemoved;
    private LocalDateTime lastIndexTime;
    private long totalIndexingTime;
    private double averageIndexingTime;
    private int indexSize;
    
    public IndexingStats() {
        this.lastIndexTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getTotalPagesIndexed() { return totalPagesIndexed; }
    public void setTotalPagesIndexed(int totalPagesIndexed) { this.totalPagesIndexed = totalPagesIndexed; }
    
    public int getPagesIndexedToday() { return pagesIndexedToday; }
    public void setPagesIndexedToday(int pagesIndexedToday) { this.pagesIndexedToday = pagesIndexedToday; }
    
    public int getPagesUpdated() { return pagesUpdated; }
    public void setPagesUpdated(int pagesUpdated) { this.pagesUpdated = pagesUpdated; }
    
    public int getPagesRemoved() { return pagesRemoved; }
    public void setPagesRemoved(int pagesRemoved) { this.pagesRemoved = pagesRemoved; }
    
    public LocalDateTime getLastIndexTime() { return lastIndexTime; }
    public void setLastIndexTime(LocalDateTime lastIndexTime) { this.lastIndexTime = lastIndexTime; }
    
    public long getTotalIndexingTime() { return totalIndexingTime; }
    public void setTotalIndexingTime(long totalIndexingTime) { this.totalIndexingTime = totalIndexingTime; }
    
    public double getAverageIndexingTime() { return averageIndexingTime; }
    public void setAverageIndexingTime(double averageIndexingTime) { this.averageIndexingTime = averageIndexingTime; }
    
    public int getIndexSize() { return indexSize; }
    public void setIndexSize(int indexSize) { this.indexSize = indexSize; }
    
    /**
     * Record a successful page indexing
     */
    public void recordPageIndexed(long indexingTime) {
        this.totalPagesIndexed++;
        this.pagesIndexedToday++;
        this.lastIndexTime = LocalDateTime.now();
        this.totalIndexingTime += indexingTime;
        this.averageIndexingTime = (double) this.totalIndexingTime / this.totalPagesIndexed;
    }
    
    /**
     * Record a page update
     */
    public void recordPageUpdated() {
        this.pagesUpdated++;
        this.lastIndexTime = LocalDateTime.now();
    }
    
    /**
     * Record a page removal
     */
    public void recordPageRemoved() {
        this.pagesRemoved++;
        this.lastIndexTime = LocalDateTime.now();
    }
    
    /**
     * Reset daily counters
     */
    public void resetDailyCounters() {
        this.pagesIndexedToday = 0;
    }
    
    @Override
    public String toString() {
        return "IndexingStats{" +
                "totalPagesIndexed=" + totalPagesIndexed +
                ", pagesIndexedToday=" + pagesIndexedToday +
                ", pagesUpdated=" + pagesUpdated +
                ", pagesRemoved=" + pagesRemoved +
                ", indexSize=" + indexSize +
                ", averageIndexingTime=" + String.format("%.2f", averageIndexingTime) + "ms" +
                '}';
    }
}

