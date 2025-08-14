package com.minisearch.search;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Search filters for refining search results
 */
public class SearchFilters {
    
    private List<String> domains;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private Integer minContentLength;
    private Integer maxContentLength;
    private Double minRelevanceScore;
    private List<String> requiredTerms;
    private List<String> excludedTerms;
    private String language;
    private boolean includeArchived;
    private boolean includeDeleted;
    
    public SearchFilters() {
        this.domains = new ArrayList<>();
        this.requiredTerms = new ArrayList<>();
        this.excludedTerms = new ArrayList<>();
        this.includeArchived = false;
        this.includeDeleted = false;
    }
    
    // Getters and Setters
    public List<String> getDomains() { return Collections.unmodifiableList(domains); }
    public void setDomains(List<String> domains) { this.domains = new ArrayList<>(domains); }
    
    public LocalDateTime getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDateTime dateFrom) { this.dateFrom = dateFrom; }
    
    public LocalDateTime getDateTo() { return dateTo; }
    public void setDateTo(LocalDateTime dateTo) { this.dateTo = dateTo; }
    
    public Integer getMinContentLength() { return minContentLength; }
    public void setMinContentLength(Integer minContentLength) { this.minContentLength = minContentLength; }
    
    public Integer getMaxContentLength() { return maxContentLength; }
    public void setMaxContentLength(Integer maxContentLength) { this.maxContentLength = maxContentLength; }
    
    public Double getMinRelevanceScore() { return minRelevanceScore; }
    public void setMinRelevanceScore(Double minRelevanceScore) { this.minRelevanceScore = minRelevanceScore; }
    
    public List<String> getRequiredTerms() { return Collections.unmodifiableList(requiredTerms); }
    public void setRequiredTerms(List<String> requiredTerms) { this.requiredTerms = new ArrayList<>(requiredTerms); }
    
    public List<String> getExcludedTerms() { return Collections.unmodifiableList(excludedTerms); }
    public void setExcludedTerms(List<String> excludedTerms) { this.excludedTerms = new ArrayList<>(excludedTerms); }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public boolean isIncludeArchived() { return includeArchived; }
    public void setIncludeArchived(boolean includeArchived) { this.includeArchived = includeArchived; }
    
    public boolean isIncludeDeleted() { return includeDeleted; }
    public void setIncludeDeleted(boolean includeDeleted) { this.includeDeleted = includeDeleted; }
    
    /**
     * Add a domain filter
     */
    public void addDomain(String domain) {
        if (domain != null && !domain.trim().isEmpty()) {
            domains.add(domain.trim());
        }
    }
    
    /**
     * Add a required term filter
     */
    public void addRequiredTerm(String term) {
        if (term != null && !term.trim().isEmpty()) {
            requiredTerms.add(term.trim());
        }
    }
    
    /**
     * Add an excluded term filter
     */
    public void addExcludedTerm(String term) {
        if (term != null && !term.trim().isEmpty()) {
            excludedTerms.add(term.trim());
        }
    }
    
    /**
     * Check if filters are empty (no filtering applied)
     */
    public boolean isEmpty() {
        return domains.isEmpty() && 
               dateFrom == null && 
               dateTo == null && 
               minContentLength == null && 
               maxContentLength == null && 
               minRelevanceScore == null && 
               requiredTerms.isEmpty() && 
               excludedTerms.isEmpty() && 
               language == null;
    }
    
    /**
     * Check if a specific domain is allowed
     */
    public boolean isDomainAllowed(String domain) {
        if (domains.isEmpty()) {
            return true; // No domain restrictions
        }
        return domains.contains(domain);
    }
    
    /**
     * Check if content length is within allowed range
     */
    public boolean isContentLengthAllowed(int contentLength) {
        if (minContentLength != null && contentLength < minContentLength) {
            return false;
        }
        if (maxContentLength != null && contentLength > maxContentLength) {
            return false;
        }
        return true;
    }
    
    /**
     * Check if date is within allowed range
     */
    public boolean isDateAllowed(LocalDateTime date) {
        if (date == null) {
            return true;
        }
        if (dateFrom != null && date.isBefore(dateFrom)) {
            return false;
        }
        if (dateTo != null && date.isAfter(dateTo)) {
            return false;
        }
        return true;
    }
    
    /**
     * Check if relevance score meets minimum threshold
     */
    public boolean isRelevanceScoreAllowed(double relevanceScore) {
        if (minRelevanceScore == null) {
            return true;
        }
        return relevanceScore >= minRelevanceScore;
    }
    
    /**
     * Check if a document meets all filter criteria
     */
    public boolean isDocumentAllowed(String domain, int contentLength, LocalDateTime date, double relevanceScore) {
        return isDomainAllowed(domain) && 
               isContentLengthAllowed(contentLength) && 
               isDateAllowed(date) && 
               isRelevanceScoreAllowed(relevanceScore);
    }
    
    /**
     * Create a copy of these filters
     */
    public SearchFilters copy() {
        SearchFilters copy = new SearchFilters();
        copy.setDomains(domains);
        copy.setDateFrom(dateFrom);
        copy.setDateTo(dateTo);
        copy.setMinContentLength(minContentLength);
        copy.setMaxContentLength(maxContentLength);
        copy.setMinRelevanceScore(minRelevanceScore);
        copy.setRequiredTerms(requiredTerms);
        copy.setExcludedTerms(excludedTerms);
        copy.setLanguage(language);
        copy.setIncludeArchived(includeArchived);
        copy.setIncludeDeleted(includeDeleted);
        return copy;
    }
    
    /**
     * Clear all filters
     */
    public void clear() {
        domains.clear();
        dateFrom = null;
        dateTo = null;
        minContentLength = null;
        maxContentLength = null;
        minRelevanceScore = null;
        requiredTerms.clear();
        excludedTerms.clear();
        language = null;
        includeArchived = false;
        includeDeleted = false;
    }
    
    /**
     * Get a summary of active filters
     */
    public String getFilterSummary() {
        if (isEmpty()) {
            return "No filters applied";
        }
        
        List<String> activeFilters = new ArrayList<>();
        
        if (!domains.isEmpty()) {
            activeFilters.add("Domains: " + String.join(", ", domains));
        }
        if (dateFrom != null || dateTo != null) {
            String dateFilter = "Date: ";
            if (dateFrom != null) dateFilter += "from " + dateFrom.toLocalDate();
            if (dateTo != null) dateFilter += " to " + dateTo.toLocalDate();
            activeFilters.add(dateFilter);
        }
        if (minContentLength != null || maxContentLength != null) {
            String lengthFilter = "Content length: ";
            if (minContentLength != null) lengthFilter += "min " + minContentLength;
            if (maxContentLength != null) lengthFilter += "max " + maxContentLength;
            activeFilters.add(lengthFilter);
        }
        if (minRelevanceScore != null) {
            activeFilters.add("Min relevance: " + String.format("%.2f", minRelevanceScore));
        }
        if (!requiredTerms.isEmpty()) {
            activeFilters.add("Required terms: " + String.join(", ", requiredTerms));
        }
        if (!excludedTerms.isEmpty()) {
            activeFilters.add("Excluded terms: " + String.join(", ", excludedTerms));
        }
        if (language != null) {
            activeFilters.add("Language: " + language);
        }
        
        return String.join("; ", activeFilters);
    }
    
    @Override
    public String toString() {
        return "SearchFilters{" +
                "domains=" + domains +
                ", dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                ", minContentLength=" + minContentLength +
                ", maxContentLength=" + maxContentLength +
                ", minRelevanceScore=" + minRelevanceScore +
                ", requiredTerms=" + requiredTerms +
                ", excludedTerms=" + excludedTerms +
                ", language='" + language + '\'' +
                ", includeArchived=" + includeArchived +
                ", includeDeleted=" + includeDeleted +
                '}';
    }
}

