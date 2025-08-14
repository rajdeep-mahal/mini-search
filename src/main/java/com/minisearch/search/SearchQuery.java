package com.minisearch.search;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents a search query with various parameters and options
 */
public class SearchQuery {
    
    private String queryText;
    private List<String> terms;
    private QueryType queryType;
    private int maxResults;
    private boolean includeSnippets;
    private boolean highlightTerms;
    private List<String> requiredTerms;
    private List<String> excludedTerms;
    private String language;
    private String sortBy;
    private boolean sortAscending;
    
    public enum QueryType {
        EXACT_MATCH,      // Exact phrase match
        ALL_TERMS,        // All terms must be present (AND)
        ANY_TERMS,        // Any term can be present (OR)
        FUZZY,           // Fuzzy matching with typos
        WILDCARD         // Wildcard pattern matching
    }
    
    public SearchQuery() {
        this.terms = new ArrayList<>();
        this.requiredTerms = new ArrayList<>();
        this.excludedTerms = new ArrayList<>();
        this.queryType = QueryType.ALL_TERMS;
        this.maxResults = 10;
        this.includeSnippets = true;
        this.highlightTerms = true;
        this.sortAscending = false;
    }
    
    public SearchQuery(String queryText) {
        this();
        this.queryText = queryText;
        parseQueryText();
    }
    
    /**
     * Parse the query text into individual terms
     */
    private void parseQueryText() {
        if (queryText == null || queryText.trim().isEmpty()) {
            return;
        }
        
        // Split by whitespace and clean terms
        String[] words = queryText.trim().split("\\s+");
        for (String word : words) {
            String cleanWord = word.trim();
            if (!cleanWord.isEmpty()) {
                if (cleanWord.startsWith("+")) {
                    // Required term
                    requiredTerms.add(cleanWord.substring(1));
                } else if (cleanWord.startsWith("-")) {
                    // Excluded term
                    excludedTerms.add(cleanWord.substring(1));
                } else {
                    // Regular term
                    terms.add(cleanWord);
                }
            }
        }
    }
    
    // Getters and Setters
    public String getQueryText() { return queryText; }
    public void setQueryText(String queryText) { 
        this.queryText = queryText; 
        parseQueryText();
    }
    
    public List<String> getTerms() { return Collections.unmodifiableList(terms); }
    public void setTerms(List<String> terms) { 
        this.terms = new ArrayList<>(terms); 
    }
    
    public QueryType getQueryType() { return queryType; }
    public void setQueryType(QueryType queryType) { this.queryType = queryType; }
    
    public int getMaxResults() { return maxResults; }
    public void setMaxResults(int maxResults) { this.maxResults = maxResults; }
    
    public boolean isIncludeSnippets() { return includeSnippets; }
    public void setIncludeSnippets(boolean includeSnippets) { this.includeSnippets = includeSnippets; }
    
    public boolean isHighlightTerms() { return highlightTerms; }
    public void setHighlightTerms(boolean highlightTerms) { this.highlightTerms = highlightTerms; }
    
    public List<String> getRequiredTerms() { return Collections.unmodifiableList(requiredTerms); }
    public void setRequiredTerms(List<String> requiredTerms) { this.requiredTerms = new ArrayList<>(requiredTerms); }
    
    public List<String> getExcludedTerms() { return Collections.unmodifiableList(excludedTerms); }
    public void setExcludedTerms(List<String> excludedTerms) { this.excludedTerms = new ArrayList<>(excludedTerms); }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    
    public boolean isSortAscending() { return sortAscending; }
    public void setSortAscending(boolean sortAscending) { this.sortAscending = sortAscending; }
    
    /**
     * Add a required term (must be present in results)
     */
    public void addRequiredTerm(String term) {
        if (term != null && !term.trim().isEmpty()) {
            requiredTerms.add(term.trim());
        }
    }
    
    /**
     * Add an excluded term (must not be present in results)
     */
    public void addExcludedTerm(String term) {
        if (term != null && !term.trim().isEmpty()) {
            excludedTerms.add(term.trim());
        }
    }
    
    /**
     * Add a regular search term
     */
    public void addTerm(String term) {
        if (term != null && !term.trim().isEmpty()) {
            terms.add(term.trim());
        }
    }
    
    /**
     * Get all search terms including required terms
     */
    public List<String> getAllSearchTerms() {
        List<String> allTerms = new ArrayList<>(terms);
        allTerms.addAll(requiredTerms);
        return allTerms;
    }
    
    /**
     * Check if the query has any terms
     */
    public boolean hasTerms() {
        return !terms.isEmpty() || !requiredTerms.isEmpty();
    }
    
    /**
     * Get the total number of search terms
     */
    public int getTermCount() {
        return terms.size() + requiredTerms.size();
    }
    
    /**
     * Clear all terms
     */
    public void clearTerms() {
        terms.clear();
        requiredTerms.clear();
        excludedTerms.clear();
    }
    
    /**
     * Create a query for exact phrase matching
     */
    public static SearchQuery exactPhrase(String phrase) {
        SearchQuery query = new SearchQuery();
        query.setQueryType(QueryType.EXACT_MATCH);
        query.setQueryText(phrase);
        return query;
    }
    
    /**
     * Create a query for all terms (AND search)
     */
    public static SearchQuery allTerms(String... terms) {
        SearchQuery query = new SearchQuery();
        query.setQueryType(QueryType.ALL_TERMS);
        for (String term : terms) {
            query.addTerm(term);
        }
        return query;
    }
    
    /**
     * Create a query for any terms (OR search)
     */
    public static SearchQuery anyTerms(String... terms) {
        SearchQuery query = new SearchQuery();
        query.setQueryType(QueryType.ANY_TERMS);
        for (String term : terms) {
            query.addTerm(term);
        }
        return query;
    }
    
    /**
     * Create a fuzzy search query
     */
    public static SearchQuery fuzzy(String queryText) {
        SearchQuery query = new SearchQuery(queryText);
        query.setQueryType(QueryType.FUZZY);
        return query;
    }
    
    @Override
    public String toString() {
        return "SearchQuery{" +
                "queryText='" + queryText + '\'' +
                ", terms=" + terms +
                ", queryType=" + queryType +
                ", maxResults=" + maxResults +
                ", requiredTerms=" + requiredTerms +
                ", excludedTerms=" + excludedTerms +
                '}';
    }
}
