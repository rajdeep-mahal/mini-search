package com.minisearch.search;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Statistics and metrics for search operations
 */
public class SearchStats {
    private int totalSearches;
    private int successfulSearches;
    private int failedSearches;
    private long totalSearchTime;
    private double averageSearchTime;
    private LocalDateTime lastSearchTime;
    private Map<String, Integer> popularQueries;
    private int totalResultsReturned;
    
    public SearchStats() {
        this.lastSearchTime = LocalDateTime.now();
        this.popularQueries = new HashMap<>();
    }
    
    // Getters and Setters
    public int getTotalSearches() { return totalSearches; }
    public void setTotalSearches(int totalSearches) { this.totalSearches = totalSearches; }
    
    public int getSuccessfulSearches() { return successfulSearches; }
    public void setSuccessfulSearches(int successfulSearches) { this.successfulSearches = successfulSearches; }
    
    public int getFailedSearches() { return failedSearches; }
    public void setFailedSearches(int failedSearches) { this.failedSearches = failedSearches; }
    
    public long getTotalSearchTime() { return totalSearchTime; }
    public void setTotalSearchTime(long totalSearchTime) { this.totalSearchTime = totalSearchTime; }
    
    public double getAverageSearchTime() { return averageSearchTime; }
    public void setAverageSearchTime(double averageSearchTime) { this.averageSearchTime = averageSearchTime; }
    
    public LocalDateTime getLastSearchTime() { return lastSearchTime; }
    public void setLastSearchTime(LocalDateTime lastSearchTime) { this.lastSearchTime = lastSearchTime; }
    
    public Map<String, Integer> getPopularQueries() { return popularQueries; }
    public void setPopularQueries(Map<String, Integer> popularQueries) { this.popularQueries = popularQueries; }
    
    public int getTotalResultsReturned() { return totalResultsReturned; }
    public void setTotalResultsReturned(int totalResultsReturned) { this.totalResultsReturned = totalResultsReturned; }
    
    /**
     * Record a successful search
     */
    public void recordSuccessfulSearch(String query, long searchTime, int resultsCount) {
        this.successfulSearches++;
        this.totalSearches++;
        this.lastSearchTime = LocalDateTime.now();
        this.totalSearchTime += searchTime;
        this.averageSearchTime = (double) this.totalSearchTime / this.totalSearches;
        this.totalResultsReturned += resultsCount;
        
        // Track popular queries
        popularQueries.put(query, popularQueries.getOrDefault(query, 0) + 1);
    }
    
    /**
     * Record a failed search
     */
    public void recordFailedSearch() {
        this.failedSearches++;
        this.totalSearches++;
        this.lastSearchTime = LocalDateTime.now();
    }
    
    /**
     * Get success rate as a percentage
     */
    public double getSuccessRate() {
        if (totalSearches == 0) return 0.0;
        return (double) successfulSearches / totalSearches * 100;
    }
    
    /**
     * Get average results per search
     */
    public double getAverageResultsPerSearch() {
        if (successfulSearches == 0) return 0.0;
        return (double) totalResultsReturned / successfulSearches;
    }
    
    /**
     * Get top popular queries
     */
    public Map<String, Integer> getTopQueries(int limit) {
        return popularQueries.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);
    }
    
    @Override
    public String toString() {
        return "SearchStats{" +
                "totalSearches=" + totalSearches +
                ", successfulSearches=" + successfulSearches +
                ", failedSearches=" + failedSearches +
                ", successRate=" + String.format("%.2f", getSuccessRate()) + "%" +
                ", averageSearchTime=" + String.format("%.2f", averageSearchTime) + "ms" +
                ", averageResultsPerSearch=" + String.format("%.2f", getAverageResultsPerSearch()) +
                '}';
    }
}

