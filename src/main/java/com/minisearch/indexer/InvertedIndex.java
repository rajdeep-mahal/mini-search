package com.minisearch.indexer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Core data structure for the search engine
 * Maps terms to documents containing those terms for fast retrieval
 * This is the "inverted index" that makes search engines fast
 */
public class InvertedIndex {
    
    // Main index: term -> list of document IDs with metadata
    private final Map<String, Map<String, DocumentIndex>> index;
    
    // Document storage: URL -> DocumentIndex
    private final Map<String, DocumentIndex> documents;
    
    // Statistics
    private int totalTerms;
    private int totalDocuments;
    
    public InvertedIndex() {
        this.index = new ConcurrentHashMap<>();
        this.documents = new ConcurrentHashMap<>();
        this.totalTerms = 0;
        this.totalDocuments = 0;
    }
    
    /**
     * Add a document to the index
     * @param document The document to index
     */
    public void addDocument(DocumentIndex document) {
        String url = document.getUrl();
        
        // Store the document
        documents.put(url, document);
        totalDocuments++;
        
        // Index each term in the document
        Map<String, Integer> termFrequencies = document.getTermFrequencies();
        Map<String, List<Integer>> termPositions = document.getTermPositions();
        
        for (Map.Entry<String, Integer> entry : termFrequencies.entrySet()) {
            String term = entry.getKey();
            Integer frequency = entry.getValue();
            List<Integer> positions = termPositions.get(term);
            
            // Add term to main index
            index.computeIfAbsent(term, k -> new HashMap<>()).put(url, document);
            totalTerms++;
        }
    }
    
    /**
     * Remove a document from the index
     * @param url The URL of the document to remove
     */
    public void removeDocument(String url) {
        DocumentIndex document = documents.remove(url);
        if (document != null) {
            totalDocuments--;
            
            // Remove from all term mappings
            for (String term : document.getAllTerms()) {
                Map<String, DocumentIndex> termDocs = index.get(term);
                if (termDocs != null) {
                    termDocs.remove(url);
                    
                    // Remove term if no documents left
                    if (termDocs.isEmpty()) {
                        index.remove(term);
                        totalTerms--;
                    }
                }
            }
        }
    }
    
    /**
     * Update an existing document in the index
     * @param document The updated document
     */
    public void updateDocument(DocumentIndex document) {
        // Remove old version first
        removeDocument(document.getUrl());
        
        // Add updated version
        addDocument(document);
    }
    
    /**
     * Search for documents containing specific terms
     * @param terms List of search terms
     * @return List of matching documents
     */
    public List<DocumentIndex> search(List<String> terms) {
        if (terms == null || terms.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Find documents containing ALL terms (AND search)
        Set<String> commonDocUrls = null;
        
        for (String term : terms) {
            Set<String> termDocUrls = getDocumentUrlsForTerm(term);
            
            if (commonDocUrls == null) {
                commonDocUrls = new HashSet<>(termDocUrls);
            } else {
                commonDocUrls.retainAll(termDocUrls);
            }
            
            if (commonDocUrls.isEmpty()) {
                break; // No documents match all terms
            }
        }
        
        // Convert URLs to documents and calculate relevance scores
        List<DocumentIndex> results = new ArrayList<>();
        if (commonDocUrls != null) {
            for (String url : commonDocUrls) {
                DocumentIndex doc = documents.get(url);
                if (doc != null) {
                    calculateSearchRelevance(doc, terms);
                    results.add(doc);
                }
            }
        }
        
        // Sort by relevance score (highest first)
        results.sort((a, b) -> Double.compare(b.getRelevanceScore(), a.getRelevanceScore()));
        
        return results;
    }
    
    /**
     * Search for documents containing ANY of the terms (OR search)
     * @param terms List of search terms
     * @return List of matching documents
     */
    public List<DocumentIndex> searchAny(List<String> terms) {
        if (terms == null || terms.isEmpty()) {
            return new ArrayList<>();
        }
        
        Set<String> allDocUrls = new HashSet<>();
        
        for (String term : terms) {
            allDocUrls.addAll(getDocumentUrlsForTerm(term));
        }
        
        // Convert URLs to documents and calculate relevance scores
        List<DocumentIndex> results = new ArrayList<>();
        for (String url : allDocUrls) {
            DocumentIndex doc = documents.get(url);
            if (doc != null) {
                calculateSearchRelevance(doc, terms);
                results.add(doc);
            }
        }
        
        // Sort by relevance score (highest first)
        results.sort((a, b) -> Double.compare(b.getRelevanceScore(), a.getRelevanceScore()));
        
        return results;
    }
    
    /**
     * Get all documents containing a specific term
     * @param term The search term
     * @return List of documents containing the term
     */
    public List<DocumentIndex> getDocumentsForTerm(String term) {
        Map<String, DocumentIndex> termDocs = index.get(term);
        if (termDocs == null) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(termDocs.values());
    }
    
    /**
     * Get document URLs for a specific term
     * @param term The search term
     * @return Set of document URLs containing the term
     */
    public Set<String> getDocumentUrlsForTerm(String term) {
        Map<String, DocumentIndex> termDocs = index.get(term);
        if (termDocs == null) {
            return new HashSet<>();
        }
        
        return termDocs.keySet();
    }
    
    /**
     * Get all terms in the index
     * @return Set of all indexed terms
     */
    public Set<String> getAllTerms() {
        return new HashSet<>(index.keySet());
    }
    
    /**
     * Get all documents in the index
     * @return Collection of all indexed documents
     */
    public Collection<DocumentIndex> getAllDocuments() {
        return documents.values();
    }
    
    /**
     * Get a specific document by URL
     * @param url The document URL
     * @return The document or null if not found
     */
    public DocumentIndex getDocument(String url) {
        return documents.get(url);
    }
    
    /**
     * Check if a document is indexed
     * @param url The document URL
     * @return true if indexed, false otherwise
     */
    public boolean isDocumentIndexed(String url) {
        return documents.containsKey(url);
    }
    
    /**
     * Check if a term exists in the index
     * @param term The term to check
     * @return true if term exists, false otherwise
     */
    public boolean containsTerm(String term) {
        return index.containsKey(term);
    }
    
    /**
     * Get the number of documents containing a specific term
     * @param term The search term
     * @return Number of documents containing the term
     */
    public int getDocumentFrequency(String term) {
        Map<String, DocumentIndex> termDocs = index.get(term);
        return termDocs != null ? termDocs.size() : 0;
    }
    
    /**
     * Get index statistics
     * @return Map containing index statistics
     */
    public Map<String, Object> getIndexStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTerms", totalTerms);
        stats.put("totalDocuments", totalDocuments);
        stats.put("uniqueTerms", index.size());
        stats.put("averageTermsPerDocument", totalDocuments > 0 ? (double) totalTerms / totalDocuments : 0.0);
        
        // Most common terms
        List<Map.Entry<String, Integer>> commonTerms = index.entrySet().stream()
            .map(entry -> Map.entry(entry.getKey(), entry.getValue().size()))
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toList());
        
        stats.put("mostCommonTerms", commonTerms);
        
        return stats;
    }
    
    /**
     * Calculate search relevance for a document based on search terms
     * @param document The document to score
     * @param searchTerms The search terms
     */
    private void calculateSearchRelevance(DocumentIndex document, List<String> searchTerms) {
        double score = 0.0;
        
        for (String term : searchTerms) {
            if (document.containsTerm(term)) {
                // Base score for term presence
                score += 1.0;
                
                // Bonus for term frequency (TF - Term Frequency)
                int frequency = document.getTermFrequency(term);
                score += Math.log(1 + frequency);
                
                // Bonus for term position (terms in title get higher score)
                if (document.getTitle() != null && 
                    document.getTitle().toLowerCase().contains(term.toLowerCase())) {
                    score += 2.0;
                }
            }
        }
        
        // Normalize by number of search terms
        if (!searchTerms.isEmpty()) {
            score = score / searchTerms.size();
        }
        
        // Add document quality score
        score += document.getRelevanceScore() * 0.1;
        
        document.setRelevanceScore(score);
    }
    
    /**
     * Clear all indexed data
     */
    public void clear() {
        index.clear();
        documents.clear();
        totalTerms = 0;
        totalDocuments = 0;
    }
    
    /**
     * Get the total number of terms in the index
     * @return Total term count
     */
    public int getTotalTerms() {
        return totalTerms;
    }
    
    /**
     * Get the total number of documents in the index
     * @return Total document count
     */
    public int getTotalDocuments() {
        return totalDocuments;
    }
    
    /**
     * Get the number of unique terms in the index
     * @return Number of unique terms
     */
    public int getUniqueTermsCount() {
        return index.size();
    }
}
