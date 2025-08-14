package com.minisearch.model;

import java.util.List;

/**
 * Represents a search result returned by the search engine
 */
public class SearchResult {
    private List<WebPage> results;
    private int totalResults;
    private long searchTime;
    private String query;
    private List<String> suggestions;
    
    // Constructors
    public SearchResult() {}
    
    public SearchResult(String query, List<WebPage> results) {
        this.query = query;
        this.results = results;
        this.totalResults = results != null ? results.size() : 0;
    }
    
    // Getters and Setters
    public List<WebPage> getResults() { return results; }
    public void setResults(List<WebPage> results) { 
        this.results = results; 
        this.totalResults = results != null ? results.size() : 0;
    }
    
    public int getTotalResults() { return totalResults; }
    public void setTotalResults(int totalResults) { this.totalResults = totalResults; }
    
    public long getSearchTime() { return searchTime; }
    public void setSearchTime(long searchTime) { this.searchTime = searchTime; }
    
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    
    @Override
    public String toString() {
        return "SearchResult{" +
                "query='" + query + '\'' +
                ", totalResults=" + totalResults +
                ", searchTime=" + searchTime + "ms" +
                '}';
    }
}

