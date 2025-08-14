package com.minisearch.search;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Statistics and metrics for search operations
 */
public class SearchStats {
    
    private AtomicLong totalSearches;
    private AtomicLong successfulSearches;
    private AtomicLong failedSearches;
    private AtomicLong totalResultsReturned;
    private AtomicLong totalSearchTime;
    private double averageSearchTime;
    private LocalDateTime lastSearchTime;
    private LocalDateTime firstSearchTime;
    private Map<String, Long> popularQueries;
    private Map<String, Long> queryExecutionTimes;
    private List<String> recentQueries;
    private int maxRecentQueries;
    
    public SearchStats() {
        this.totalSearches = new AtomicLong(0);
        this.successfulSearches = new AtomicLong(0);
        this.failedSearches = new AtomicLong(0);
        this.totalResultsReturned = new AtomicLong(0);
        this.totalSearchTime = new AtomicLong(0);
        this.averageSearchTime = 0.0;
        this.popularQueries = new HashMap<>();
        this.queryExecutionTimes = new HashMap<>();
        this.recentQueries = new ArrayList<>();
        this.maxRecentQueries = 100;
    }
    
    // Getters and Setters
    public long getTotalSearches() { return totalSearches.get(); }
    
    public long getSuccessfulSearches() { return successfulSearches.get(); }
    
    public long getFailedSearches() { return failedSearches.get(); }
    
    public long getTotalResultsReturned() { return totalResultsReturned.get(); }
    
    public long getTotalSearchTime() { return totalSearchTime.get(); }
    
    public double getAverageSearchTime() { return averageSearchTime; }
    
    public LocalDateTime getLastSearchTime() { return lastSearchTime; }
    
    public LocalDateTime getFirstSearchTime() { return firstSearchTime; }
    
    public Map<String, Long> getPopularQueries() { return new HashMap<>(popularQueries); }
    
    public Map<String, Long> getQueryExecutionTimes() { return new HashMap<>(queryExecutionTimes); }
    
    public List<String> getRecentQueries() { return new ArrayList<>(recentQueries); }
    
    public int getMaxRecentQueries() { return maxRecentQueries; }
    public void setMaxRecentQueries(int maxRecentQueries) { this.maxRecentQueries = maxRecentQueries; }
    
    /**
     * Record a successful search
     */
    public void recordSuccessfulSearch(String query, long executionTime, int resultCount) {
        totalSearches.incrementAndGet();
        successfulSearches.incrementAndGet();
        totalResultsReturned.addAndGet(resultCount);
        totalSearchTime.addAndGet(executionTime);
        
        // Update average search time
        long totalSearchesCount = totalSearches.get();
        averageSearchTime = (double) totalSearchTime.get() / totalSearchesCount;
        
        // Update timestamps
        LocalDateTime now = LocalDateTime.now();
        if (firstSearchTime == null) {
            firstSearchTime = now;
        }
        lastSearchTime = now;
        
        // Record popular queries
        recordPopularQuery(query);
        
        // Record execution time
        recordQueryExecutionTime(query, executionTime);
        
        // Add to recent queries
        addRecentQuery(query);
    }
    
    /**
     * Record a failed search
     */
    public void recordFailedSearch(String query, long executionTime) {
        totalSearches.incrementAndGet();
        failedSearches.incrementAndGet();
        totalSearchTime.addAndGet(executionTime);
        
        // Update average search time
        long totalSearchesCount = totalSearches.get();
        averageSearchTime = (double) totalSearchTime.get() / totalSearchesCount;
        
        // Update timestamps
        LocalDateTime now = LocalDateTime.now();
        if (firstSearchTime == null) {
            firstSearchTime = now;
        }
        lastSearchTime = now;
        
        // Add to recent queries
        addRecentQuery(query);
    }
    
    /**
     * Record a popular query
     */
    private void recordPopularQuery(String query) {
        if (query != null && !query.trim().isEmpty()) {
            popularQueries.merge(query.trim(), 1L, Long::sum);
        }
    }
    
    /**
     * Record query execution time
     */
    private void recordQueryExecutionTime(String query, long executionTime) {
        if (query != null && !query.trim().isEmpty()) {
            queryExecutionTimes.merge(query.trim(), executionTime, Long::sum);
        }
    }
    
    /**
     * Add query to recent queries list
     */
    private void addRecentQuery(String query) {
        if (query != null && !query.trim().isEmpty()) {
            String cleanQuery = query.trim();
            
            // Remove if already exists
            recentQueries.remove(cleanQuery);
            
            // Add to beginning
            recentQueries.add(0, cleanQuery);
            
            // Keep only the most recent queries
            if (recentQueries.size() > maxRecentQueries) {
                recentQueries = recentQueries.subList(0, maxRecentQueries);
            }
        }
    }
    
    /**
     * Get success rate percentage
     */
    public double getSuccessRate() {
        long total = totalSearches.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) successfulSearches.get() / total * 100.0;
    }
    
    /**
     * Get failure rate percentage
     */
    public double getFailureRate() {
        long total = totalSearches.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) failedSearches.get() / total * 100.0;
    }
    
    /**
     * Get average results per search
     */
    public double getAverageResultsPerSearch() {
        long total = totalSearches.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) totalResultsReturned.get() / total;
    }
    
    /**
     * Get the most popular queries
     */
    public List<Map.Entry<String, Long>> getMostPopularQueries(int limit) {
        return popularQueries.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(limit)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Get the slowest queries
     */
    public List<Map.Entry<String, Long>> getSlowestQueries(int limit) {
        return queryExecutionTimes.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(limit)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Get search statistics summary
     */
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalSearches", getTotalSearches());
        summary.put("successfulSearches", getSuccessfulSearches());
        summary.put("failedSearches", getFailedSearches());
        summary.put("successRate", String.format("%.2f%%", getSuccessRate()));
        summary.put("failureRate", String.format("%.2f%%", getFailureRate()));
        summary.put("totalResultsReturned", getTotalResultsReturned());
        summary.put("averageResultsPerSearch", String.format("%.2f", getAverageResultsPerSearch()));
        summary.put("averageSearchTime", String.format("%.2fms", getAverageSearchTime()));
        summary.put("firstSearchTime", firstSearchTime);
        summary.put("lastSearchTime", lastSearchTime);
        summary.put("popularQueries", getMostPopularQueries(10));
        summary.put("slowestQueries", getSlowestQueries(5));
        summary.put("recentQueries", getRecentQueries().subList(0, Math.min(10, getRecentQueries().size())));
        
        return summary;
    }
    
    /**
     * Reset all statistics
     */
    public void reset() {
        totalSearches.set(0);
        successfulSearches.set(0);
        failedSearches.set(0);
        totalResultsReturned.set(0);
        totalSearchTime.set(0);
        averageSearchTime = 0.0;
        firstSearchTime = null;
        lastSearchTime = null;
        popularQueries.clear();
        queryExecutionTimes.clear();
        recentQueries.clear();
    }
    
    /**
     * Get formatted statistics for display
     */
    public String getFormattedStats() {
        return String.format(
            "Search Statistics:\n" +
            "  Total Searches: %d\n" +
            "  Successful: %d (%.1f%%)\n" +
            "  Failed: %d (%.1f%%)\n" +
            "  Total Results: %d\n" +
            "  Avg Results/Search: %.2f\n" +
            "  Avg Search Time: %.2fms\n" +
            "  First Search: %s\n" +
            "  Last Search: %s",
            getTotalSearches(),
            getSuccessfulSearches(), getSuccessRate(),
            getFailedSearches(), getFailureRate(),
            getTotalResultsReturned(),
            getAverageResultsPerSearch(),
            getAverageSearchTime(),
            firstSearchTime != null ? firstSearchTime.toLocalTime() : "Never",
            lastSearchTime != null ? lastSearchTime.toLocalTime() : "Never"
        );
    }
    
    @Override
    public String toString() {
        return "SearchStats{" +
                "totalSearches=" + getTotalSearches() +
                ", successfulSearches=" + getSuccessfulSearches() +
                ", failedSearches=" + getFailedSearches() +
                ", successRate=" + String.format("%.1f%%", getSuccessRate()) +
                ", averageSearchTime=" + String.format("%.2fms", getAverageSearchTime()) +
                ", totalResults=" + getTotalResultsReturned() +
                '}';
    }
}

