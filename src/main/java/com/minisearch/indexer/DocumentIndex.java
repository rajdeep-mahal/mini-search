package com.minisearch.indexer;

import com.minisearch.model.WebPage;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents an indexed document in the search engine
 * Contains metadata and processed content for efficient searching
 */
public class DocumentIndex {
    private String url;
    private String title;
    private String domain;
    private String content;
    private List<String> links;
    private Map<String, Integer> termFrequencies; // word -> frequency
    private Map<String, List<Integer>> termPositions; // word -> list of positions
    private LocalDateTime indexedAt;
    private LocalDateTime lastUpdated;
    private int contentLength;
    private double relevanceScore;
    
    public DocumentIndex(WebPage webPage) {
        this.url = webPage.getUrl();
        this.title = webPage.getTitle();
        this.domain = webPage.getDomain();
        this.content = webPage.getContent();
        this.links = webPage.getLinks();
        this.contentLength = webPage.getContentLength();
        this.indexedAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.termFrequencies = new HashMap<>();
        this.termPositions = new HashMap<>();
        this.relevanceScore = 0.0;
    }
    
    // Getters and Setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public List<String> getLinks() { return links; }
    public void setLinks(List<String> links) { this.links = links; }
    
    public Map<String, Integer> getTermFrequencies() { return termFrequencies; }
    public void setTermFrequencies(Map<String, Integer> termFrequencies) { this.termFrequencies = termFrequencies; }
    
    public Map<String, List<Integer>> getTermPositions() { return termPositions; }
    public void setTermPositions(Map<String, List<Integer>> termPositions) { this.termPositions = termPositions; }
    
    public LocalDateTime getIndexedAt() { return indexedAt; }
    public void setIndexedAt(LocalDateTime indexedAt) { this.indexedAt = indexedAt; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public int getContentLength() { return contentLength; }
    public void setContentLength(int contentLength) { this.contentLength = contentLength; }
    
    public double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(double relevanceScore) { this.relevanceScore = relevanceScore; }
    
    /**
     * Add a term with its frequency and positions
     */
    public void addTerm(String term, int frequency, List<Integer> positions) {
        this.termFrequencies.put(term, frequency);
        this.termPositions.put(term, positions);
    }
    
    /**
     * Get the frequency of a specific term
     */
    public int getTermFrequency(String term) {
        return termFrequencies.getOrDefault(term, 0);
    }
    
    /**
     * Get the positions of a specific term
     */
    public List<Integer> getTermPositions(String term) {
        return termPositions.getOrDefault(term, new ArrayList<>());
    }
    
    /**
     * Check if this document contains a specific term
     */
    public boolean containsTerm(String term) {
        return termFrequencies.containsKey(term);
    }
    
    /**
     * Get all terms in this document
     */
    public List<String> getAllTerms() {
        return new ArrayList<>(termFrequencies.keySet());
    }
    
    /**
     * Update the document with new content
     */
    public void updateContent(String newContent) {
        this.content = newContent;
        this.contentLength = newContent.length();
        this.lastUpdated = LocalDateTime.now();
        // Clear old term data - will be reprocessed
        this.termFrequencies.clear();
        this.termPositions.clear();
    }
    
    /**
     * Calculate a simple relevance score based on content length and term diversity
     */
    public void calculateRelevanceScore() {
        // Simple scoring: more terms and longer content = higher score
        double termDiversity = termFrequencies.size();
        double contentScore = Math.min(contentLength / 1000.0, 10.0); // Cap at 10
        this.relevanceScore = termDiversity + contentScore;
    }
    
    @Override
    public String toString() {
        return "DocumentIndex{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", domain='" + domain + '\'' +
                ", terms=" + termFrequencies.size() +
                ", contentLength=" + contentLength +
                ", relevanceScore=" + String.format("%.2f", relevanceScore) +
                ", indexedAt=" + indexedAt +
                '}';
    }
}
