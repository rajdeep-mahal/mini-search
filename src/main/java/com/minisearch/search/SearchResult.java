package com.minisearch.search;

import com.minisearch.indexer.DocumentIndex;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents a single search result with metadata and content snippets
 */
public class SearchResult {
    
    private String url;
    private String title;
    private String domain;
    private String snippet;
    private double relevanceScore;
    private LocalDateTime indexedAt;
    private int contentLength;
    private Map<String, List<Integer>> termPositions;
    private List<String> matchedTerms;
    private Map<String, Object> metadata;
    
    public SearchResult() {
        this.termPositions = new HashMap<>();
        this.matchedTerms = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    public SearchResult(DocumentIndex document) {
        this();
        this.url = document.getUrl();
        this.title = document.getTitle();
        this.domain = document.getDomain();
        this.indexedAt = document.getIndexedAt();
        this.contentLength = document.getContentLength();
        this.relevanceScore = document.getRelevanceScore();
    }
    
    // Getters and Setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    
    public String getSnippet() { return snippet; }
    public void setSnippet(String snippet) { this.snippet = snippet; }
    
    public double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(double relevanceScore) { this.relevanceScore = relevanceScore; }
    
    public LocalDateTime getIndexedAt() { return indexedAt; }
    public void setIndexedAt(LocalDateTime indexedAt) { this.indexedAt = indexedAt; }
    
    public int getContentLength() { return contentLength; }
    public void setContentLength(int contentLength) { this.contentLength = contentLength; }
    
    public Map<String, List<Integer>> getTermPositions() { return termPositions; }
    public void setTermPositions(Map<String, List<Integer>> termPositions) { this.termPositions = termPositions; }
    
    public List<String> getMatchedTerms() { return matchedTerms; }
    public void setMatchedTerms(List<String> matchedTerms) { this.matchedTerms = matchedTerms; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    /**
     * Add a matched term with its positions
     */
    public void addMatchedTerm(String term, List<Integer> positions) {
        if (term != null && !term.trim().isEmpty()) {
            matchedTerms.add(term);
            if (positions != null && !positions.isEmpty()) {
                termPositions.put(term, new ArrayList<>(positions));
            }
        }
    }
    
    /**
     * Add metadata
     */
    public void addMetadata(String key, Object value) {
        if (key != null && value != null) {
            metadata.put(key, value);
        }
    }
    
    /**
     * Get metadata value
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    
    /**
     * Get metadata value with default
     */
    public Object getMetadata(String key, Object defaultValue) {
        return metadata.getOrDefault(key, defaultValue);
    }
    
    /**
     * Check if this result has a specific matched term
     */
    public boolean hasMatchedTerm(String term) {
        return matchedTerms.contains(term);
    }
    
    /**
     * Get the number of matched terms
     */
    public int getMatchedTermCount() {
        return matchedTerms.size();
    }
    
    /**
     * Get the total number of term matches (including duplicates)
     */
    public int getTotalTermMatches() {
        int total = 0;
        for (List<Integer> positions : termPositions.values()) {
            total += positions.size();
        }
        return total;
    }
    
    /**
     * Generate a content snippet highlighting matched terms
     */
    public String generateHighlightedSnippet(String content, int maxLength) {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }
        
        if (matchedTerms.isEmpty()) {
            // No terms to highlight, return truncated content
            return content.length() > maxLength ? 
                content.substring(0, maxLength) + "..." : content;
        }
        
        // Find the best position to start the snippet
        int startPos = findBestSnippetStart(content, maxLength);
        int endPos = Math.min(startPos + maxLength, content.length());
        
        String snippet = content.substring(startPos, endPos);
        
        // Add ellipsis if truncated
        if (endPos < content.length()) {
            snippet += "...";
        }
        if (startPos > 0) {
            snippet = "..." + snippet;
        }
        
        // Highlight matched terms
        for (String term : matchedTerms) {
            snippet = snippet.replaceAll(
                "(?i)(" + java.util.regex.Pattern.quote(term) + ")", 
                "<strong>$1</strong>"
            );
        }
        
        return snippet;
    }
    
    /**
     * Find the best starting position for a snippet
     */
    private int findBestSnippetStart(String content, int maxLength) {
        if (content.length() <= maxLength) {
            return 0;
        }
        
        // Try to find a position with many matched terms
        int bestPos = 0;
        int bestScore = 0;
        
        for (int i = 0; i <= content.length() - maxLength; i += 100) {
            int score = countMatchedTermsInRange(content, i, i + maxLength);
            if (score > bestScore) {
                bestScore = score;
                bestPos = i;
            }
        }
        
        return bestPos;
    }
    
    /**
     * Count matched terms in a specific range
     */
    private int countMatchedTermsInRange(String content, int start, int end) {
        int count = 0;
        String range = content.substring(start, end).toLowerCase();
        
        for (String term : matchedTerms) {
            if (range.contains(term.toLowerCase())) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Calculate a relevance score based on matched terms
     */
    public void calculateRelevanceScore() {
        double score = 0.0;
        
        // Base score for each matched term
        score += matchedTerms.size() * 1.0;
        
        // Bonus for term frequency
        score += getTotalTermMatches() * 0.5;
        
        // Bonus for title matches
        if (title != null) {
            for (String term : matchedTerms) {
                if (title.toLowerCase().contains(term.toLowerCase())) {
                    score += 2.0;
                }
            }
        }
        
        // Bonus for domain relevance (if available)
        if (domain != null && !domain.isEmpty()) {
            score += 0.5;
        }
        
        // Normalize by content length (shorter content gets slight bonus)
        if (contentLength > 0) {
            score += Math.min(1000.0 / contentLength, 1.0);
        }
        
        this.relevanceScore = score;
    }
    
    /**
     * Get a formatted relevance score
     */
    public String getFormattedRelevanceScore() {
        return String.format("%.2f", relevanceScore);
    }
    
    /**
     * Get a human-readable content length
     */
    public String getFormattedContentLength() {
        if (contentLength < 1000) {
            return contentLength + " chars";
        } else if (contentLength < 1000000) {
            return String.format("%.1fK chars", contentLength / 1000.0);
        } else {
            return String.format("%.1fM chars", contentLength / 1000000.0);
        }
    }
    
    @Override
    public String toString() {
        return "SearchResult{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", domain='" + domain + '\'' +
                ", relevanceScore=" + String.format("%.2f", relevanceScore) +
                ", matchedTerms=" + matchedTerms.size() +
                ", contentLength=" + contentLength +
                '}';
    }
}
