package com.minisearch.search;

import com.minisearch.gemini.GeminiService;
import com.minisearch.gemini.GeminiResponse;
import com.minisearch.indexer.SimpleSearchIndexer;
import com.minisearch.model.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Hybrid search engine that combines local dataset search with Gemini AI results
 */
public class HybridSearchEngine implements SearchEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(HybridSearchEngine.class);
    
    private final SimpleSearchEngine localSearchEngine;
    private final GeminiService geminiService;
    private final boolean geminiEnabled;
    
    public HybridSearchEngine(SimpleSearchEngine localSearchEngine, GeminiService geminiService) {
        this.localSearchEngine = localSearchEngine;
        this.geminiService = geminiService;
        this.geminiEnabled = geminiService != null && geminiService.isAvailable();
        
        if (geminiEnabled) {
            logger.info("Hybrid search engine initialized with Gemini AI support");
        } else {
            logger.info("Hybrid search engine initialized with local search only");
        }
    }
    
    @Override
    public List<SearchResult> search(SearchQuery query) {
        logger.info("Performing hybrid search for: {}", query.getQueryText());
        
        // Start local search
        List<SearchResult> localResults = localSearchEngine.search(query);
        
        // If Gemini is enabled, enhance with AI-generated content
        if (geminiEnabled && shouldUseGemini(query) && !localResults.isEmpty()) {
            try {
                List<SearchResult> enhancedResults = enhanceWithGemini(query, localResults);
                logger.info("Search enhanced with Gemini AI content");
                return enhancedResults;
            } catch (Exception e) {
                logger.warn("Failed to enhance search with Gemini, returning local results", e);
            }
        }
        
        return localResults;
    }
    
    @Override
    public List<SearchResult> search(SearchQuery query, SearchFilters filters) {
        return localSearchEngine.search(query, filters);
    }
    
    @Override
    public List<SearchResult> search(List<String> terms) {
        return localSearchEngine.search(terms);
    }
    
    @Override
    public List<SearchResult> searchAny(List<String> terms) {
        return localSearchEngine.searchAny(terms);
    }
    
    @Override
    public List<SearchResult> searchAll(List<String> terms) {
        return localSearchEngine.searchAll(terms);
    }
    
    @Override
    public List<SearchResult> searchPhrase(String phrase) {
        return localSearchEngine.searchPhrase(phrase);
    }
    
    @Override
    public List<String> getSuggestions(String partialQuery) {
        return localSearchEngine.getSuggestions(partialQuery);
    }
    
    @Override
    public List<String> getPopularSearchTerms(int limit) {
        return localSearchEngine.getPopularSearchTerms(limit);
    }
    
    @Override
    public List<String> getRelatedTerms(String query, int limit) {
        return localSearchEngine.getRelatedTerms(query, limit);
    }
    
    @Override
    public List<SearchResult> fuzzySearch(String query, int maxDistance) {
        return localSearchEngine.fuzzySearch(query, maxDistance);
    }
    
    @Override
    public PaginatedSearchResult searchWithPagination(SearchQuery query, int page, int pageSize) {
        return localSearchEngine.searchWithPagination(query, page, pageSize);
    }
    
    @Override
    public long getResultCount(SearchQuery query) {
        return localSearchEngine.getResultCount(query);
    }
    
    @Override
    public boolean isReady() {
        return localSearchEngine.isReady();
    }
    
    @Override
    public int getTotalDocuments() {
        return localSearchEngine.getTotalDocuments();
    }
    
    @Override
    public int getTotalTerms() {
        return localSearchEngine.getTotalTerms();
    }
    
    private boolean shouldUseGemini(SearchQuery query) {
        String queryText = query.getQueryText().toLowerCase().trim();
        
        // Use Gemini for general knowledge queries
        // Avoid for very specific technical terms that might be in local datasets
        return queryText.length() > 2 && 
               !queryText.contains("http") && 
               !queryText.contains("file:") &&
               !queryText.contains("localhost");
    }
    
    private List<SearchResult> enhanceWithGemini(SearchQuery query, List<SearchResult> localResults) {
        try {
            // Call Gemini API asynchronously with timeout
            CompletableFuture<GeminiResponse> geminiFuture = CompletableFuture
                .supplyAsync(() -> geminiService.generateContent(query.getQueryText()))
                .orTimeout(5, TimeUnit.SECONDS);
            
            GeminiResponse geminiResponse = geminiFuture.get();
            
            if (geminiResponse != null && !geminiResponse.isError()) {
                return createEnhancedResults(query, localResults, geminiResponse);
            }
            
        } catch (Exception e) {
            logger.warn("Gemini API call failed or timed out", e);
        }
        
        return localResults;
    }
    
    private List<SearchResult> createEnhancedResults(SearchQuery query, List<SearchResult> localResults, GeminiResponse geminiResponse) {
        List<SearchResult> enhancedResults = new ArrayList<>(localResults);
        
        // Create a special Gemini result
        SearchResult geminiResult = createGeminiResult(query.getQueryText(), geminiResponse);
        enhancedResults.add(0, geminiResult); // Add at the top
        
        return enhancedResults;
    }
    
    private SearchResult createGeminiResult(String query, GeminiResponse geminiResponse) {
        SearchResult result = new SearchResult();
        result.setUrl("gemini://ai-generated-content");
        result.setTitle("AI-Generated Content: " + query);
        result.setSnippet(geminiResponse.getText());
        result.setDomain("gemini.ai");
        
        // Add metadata
        result.addMetadata("source", "Gemini AI");
        result.addMetadata("generated_at", String.valueOf(geminiResponse.getTimestamp()));
        result.addMetadata("has_images", String.valueOf(geminiResponse.hasImages()));
        
        return result;
    }
    
    @Override
    public SearchStats getSearchStats() {
        return localSearchEngine.getSearchStats();
    }
}
