package com.minisearch.search;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Paginated search results with navigation information
 */
public class PaginatedSearchResult {
    
    private List<SearchResult> results;
    private int currentPage;
    private int pageSize;
    private long totalResults;
    private int totalPages;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
    private int nextPage;
    private int previousPage;
    
    public PaginatedSearchResult() {
        this.results = new ArrayList<>();
        this.currentPage = 0;
        this.pageSize = 10;
        this.totalResults = 0;
        this.totalPages = 0;
        this.hasNextPage = false;
        this.hasPreviousPage = false;
        this.nextPage = 0;
        this.previousPage = 0;
    }
    
    public PaginatedSearchResult(List<SearchResult> results, int currentPage, int pageSize, long totalResults) {
        this.results = new ArrayList<>(results);
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalResults = totalResults;
        
        // Calculate pagination information
        calculatePagination();
    }
    
    /**
     * Calculate pagination information
     */
    private void calculatePagination() {
        // Calculate total pages
        totalPages = (int) Math.ceil((double) totalResults / pageSize);
        
        // Check if there are more pages
        hasNextPage = currentPage < totalPages - 1;
        hasPreviousPage = currentPage > 0;
        
        // Set next and previous page numbers
        nextPage = hasNextPage ? currentPage + 1 : currentPage;
        previousPage = hasPreviousPage ? currentPage - 1 : currentPage;
    }
    
    // Getters and Setters
    public List<SearchResult> getResults() { return new ArrayList<>(results); }
    public void setResults(List<SearchResult> results) { 
        this.results = new ArrayList<>(results); 
        calculatePagination();
    }
    
    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { 
        this.currentPage = currentPage; 
        calculatePagination();
    }
    
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { 
        this.pageSize = pageSize; 
        calculatePagination();
    }
    
    public long getTotalResults() { return totalResults; }
    public void setTotalResults(long totalResults) { 
        this.totalResults = totalResults; 
        calculatePagination();
    }
    
    public int getTotalPages() { return totalPages; }
    
    public boolean isHasNextPage() { return hasNextPage; }
    
    public boolean isHasPreviousPage() { return hasPreviousPage; }
    
    public int getNextPage() { return nextPage; }
    
    public int getPreviousPage() { return previousPage; }
    
    /**
     * Get the starting index of the current page
     */
    public int getStartIndex() {
        return currentPage * pageSize;
    }
    
    /**
     * Get the ending index of the current page
     */
    public int getEndIndex() {
        return Math.min(getStartIndex() + pageSize, (int) totalResults);
    }
    
    /**
     * Get the number of results on the current page
     */
    public int getCurrentPageResultCount() {
        return results.size();
    }
    
    /**
     * Check if the current page is empty
     */
    public boolean isCurrentPageEmpty() {
        return results.isEmpty();
    }
    
    /**
     * Check if this is the first page
     */
    public boolean isFirstPage() {
        return currentPage == 0;
    }
    
    /**
     * Check if this is the last page
     */
    public boolean isLastPage() {
        return currentPage == totalPages - 1;
    }
    
    /**
     * Get page information summary
     */
    public String getPageInfo() {
        if (totalResults == 0) {
            return "No results found";
        }
        
        int start = getStartIndex() + 1;
        int end = getEndIndex();
        
        return String.format("Showing %d-%d of %d results (Page %d of %d)", 
            start, end, totalResults, currentPage + 1, totalPages);
    }
    
    /**
     * Get navigation information
     */
    public String getNavigationInfo() {
        StringBuilder nav = new StringBuilder();
        
        if (hasPreviousPage) {
            nav.append("← Previous");
        }
        
        if (hasPreviousPage && hasNextPage) {
            nav.append(" | ");
        }
        
        if (hasNextPage) {
            nav.append("Next →");
        }
        
        return nav.toString();
    }
    
    /**
     * Get page numbers for navigation
     */
    public List<Integer> getPageNumbers(int maxVisiblePages) {
        List<Integer> pageNumbers = new ArrayList<>();
        
        if (totalPages <= maxVisiblePages) {
            // Show all pages
            for (int i = 0; i < totalPages; i++) {
                pageNumbers.add(i);
            }
        } else {
            // Show a window of pages around current page
            int start = Math.max(0, currentPage - maxVisiblePages / 2);
            int end = Math.min(totalPages, start + maxVisiblePages);
            
            // Adjust start if we're near the end
            if (end - start < maxVisiblePages) {
                start = Math.max(0, end - maxVisiblePages);
            }
            
            for (int i = start; i < end; i++) {
                pageNumbers.add(i);
            }
        }
        
        return pageNumbers;
    }
    
    /**
     * Create an empty result for a specific page
     */
    public static PaginatedSearchResult empty(int currentPage, int pageSize) {
        PaginatedSearchResult result = new PaginatedSearchResult();
        result.setCurrentPage(currentPage);
        result.setPageSize(pageSize);
        result.setTotalResults(0);
        return result;
    }
    
    /**
     * Create a result with a subset of results for pagination
     */
    public static PaginatedSearchResult fromResults(List<SearchResult> allResults, int currentPage, int pageSize) {
        if (allResults == null) {
            return empty(currentPage, pageSize);
        }
        
        long totalResults = allResults.size();
        int startIndex = currentPage * pageSize;
        int endIndex = Math.min(startIndex + pageSize, (int) totalResults);
        
        List<SearchResult> pageResults;
        if (startIndex >= totalResults) {
            pageResults = new ArrayList<>();
        } else {
            pageResults = allResults.subList(startIndex, endIndex);
        }
        
        return new PaginatedSearchResult(pageResults, currentPage, pageSize, totalResults);
    }
    
    /**
     * Get a summary of the paginated results
     */
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("currentPage", currentPage);
        summary.put("pageSize", pageSize);
        summary.put("totalResults", totalResults);
        summary.put("totalPages", totalPages);
        summary.put("hasNextPage", hasNextPage);
        summary.put("hasPreviousPage", hasPreviousPage);
        summary.put("nextPage", nextPage);
        summary.put("previousPage", previousPage);
        summary.put("startIndex", getStartIndex());
        summary.put("endIndex", getEndIndex());
        summary.put("currentPageResultCount", getCurrentPageResultCount());
        summary.put("pageInfo", getPageInfo());
        summary.put("navigationInfo", getNavigationInfo());
        
        return summary;
    }
    
    @Override
    public String toString() {
        return "PaginatedSearchResult{" +
                "currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", totalResults=" + totalResults +
                ", totalPages=" + totalPages +
                ", hasNextPage=" + hasNextPage +
                ", hasPreviousPage=" + hasPreviousPage +
                ", results=" + results.size() +
                '}';
    }
}
