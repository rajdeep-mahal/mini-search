package com.minisearch.search;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Search filters for refining search results
 */
public class SearchFilters {
    private String domain;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private List<String> contentTypes;
    private int minContentLength;
    private int maxContentLength;
    private boolean includeArchived;
    
    public SearchFilters() {}
    
    // Getters and Setters
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    
    public LocalDateTime getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDateTime dateFrom) { this.dateFrom = dateFrom; }
    
    public LocalDateTime getDateTo() { return dateTo; }
    public void setDateTo(LocalDateTime dateTo) { this.dateTo = dateTo; }
    
    public List<String> getContentTypes() { return contentTypes; }
    public void setContentTypes(List<String> contentTypes) { this.contentTypes = contentTypes; }
    
    public int getMinContentLength() { return minContentLength; }
    public void setMinContentLength(int minContentLength) { this.minContentLength = minContentLength; }
    
    public int getMaxContentLength() { return maxContentLength; }
    public void setMaxContentLength(int maxContentLength) { this.maxContentLength = maxContentLength; }
    
    public boolean isIncludeArchived() { return includeArchived; }
    public void setIncludeArchived(boolean includeArchived) { this.includeArchived = includeArchived; }
    
    /**
     * Check if any filters are set
     */
    public boolean hasFilters() {
        return domain != null || dateFrom != null || dateTo != null || 
               (contentTypes != null && !contentTypes.isEmpty()) ||
               minContentLength > 0 || maxContentLength > 0;
    }
    
    @Override
    public String toString() {
        return "SearchFilters{" +
                "domain='" + domain + '\'' +
                ", dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                ", contentTypes=" + contentTypes +
                ", minContentLength=" + minContentLength +
                ", maxContentLength=" + maxContentLength +
                ", includeArchived=" + includeArchived +
                '}';
    }
}

