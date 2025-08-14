package com.minisearch.search;

import com.minisearch.indexer.DocumentIndex;
import com.minisearch.indexer.SimpleSearchIndexer;
import com.minisearch.indexer.TextProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Implementation of SearchEngine that provides core search functionality
 * Integrates with the indexer to perform fast document searches
 */
public class SimpleSearchEngine implements SearchEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleSearchEngine.class);
    
    // Core components
    private final SimpleSearchIndexer indexer;
    private final TextProcessor textProcessor;
    private final SearchStats searchStats;
    
    // Thread safety
    private final ReadWriteLock searchLock;
    
    public SimpleSearchEngine(SimpleSearchIndexer indexer) {
        this.indexer = indexer;
        this.textProcessor = indexer.getTextProcessor();
        this.searchStats = new SearchStats();
        this.searchLock = new ReentrantReadWriteLock();
        
        logger.info("SimpleSearchEngine initialized");
    }
    
    @Override
    public List<SearchResult> search(SearchQuery query) {
        if (query == null || !query.hasTerms()) {
            logger.warn("Invalid search query");
            return new ArrayList<>();
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            searchLock.readLock().lock();
            
            logger.info("Executing search query: {}", query.getQueryText());
            
            List<String> searchTerms = query.getAllSearchTerms();
            List<DocumentIndex> documents;
            
            // Execute search based on query type
            List<SearchResult> searchResults;
            switch (query.getQueryType()) {
                case EXACT_MATCH:
                    searchResults = searchPhrase(query.getQueryText());
                    break;
                case ALL_TERMS:
                    documents = indexer.search(searchTerms);
                    searchResults = convertToSearchResults(documents, searchTerms);
                    break;
                case ANY_TERMS:
                    documents = indexer.searchAny(searchTerms);
                    searchResults = convertToSearchResults(documents, searchTerms);
                    break;
                case FUZZY:
                    searchResults = fuzzySearch(query.getQueryText(), 2);
                    break;
                default:
                    documents = indexer.search(searchTerms);
                    searchResults = convertToSearchResults(documents, searchTerms);
                    break;
            }
            
            // Apply filters if specified (only for document-based searches)
            if (query.getQueryType() != SearchQuery.QueryType.EXACT_MATCH && 
                query.getQueryType() != SearchQuery.QueryType.FUZZY) {
                
                if (query.getRequiredTerms() != null && !query.getRequiredTerms().isEmpty()) {
                    documents = filterByRequiredTerms(documents, query.getRequiredTerms());
                    searchResults = convertToSearchResults(documents, searchTerms);
                }
                
                if (query.getExcludedTerms() != null && !query.getExcludedTerms().isEmpty()) {
                    documents = filterByExcludedTerms(documents, query.getExcludedTerms());
                    searchResults = convertToSearchResults(documents, searchTerms);
                }
            }
            
            // Use the search results directly
            List<SearchResult> results = searchResults;
            
            // Limit results if specified
            if (query.getMaxResults() > 0 && results.size() > query.getMaxResults()) {
                results = results.subList(0, query.getMaxResults());
            }
            
            // Record successful search
            long executionTime = System.currentTimeMillis() - startTime;
            searchStats.recordSuccessfulSearch(query.getQueryText(), executionTime, results.size());
            
            logger.info("Search completed in {}ms, found {} results", executionTime, results.size());
            
            return results;
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            searchStats.recordFailedSearch(query.getQueryText(), executionTime);
            logger.error("Search failed for query: {}", query.getQueryText(), e);
            return new ArrayList<>();
        } finally {
            searchLock.readLock().unlock();
        }
    }
    
    @Override
    public List<SearchResult> search(SearchQuery query, SearchFilters filters) {
        if (query == null || !query.hasTerms()) {
            logger.warn("Invalid search query");
            return new ArrayList<>();
        }
        
        // Execute base search
        List<SearchResult> results = search(query);
        
        // Apply additional filters
        if (filters != null && !filters.isEmpty()) {
            results = applyFilters(results, filters);
        }
        
        return results;
    }
    
    @Override
    public List<SearchResult> search(List<String> terms) {
        if (terms == null || terms.isEmpty()) {
            return new ArrayList<>();
        }
        
        SearchQuery query = SearchQuery.allTerms(terms.toArray(new String[0]));
        return search(query);
    }
    
    @Override
    public List<SearchResult> searchAny(List<String> terms) {
        if (terms == null || terms.isEmpty()) {
            return new ArrayList<>();
        }
        
        SearchQuery query = SearchQuery.anyTerms(terms.toArray(new String[0]));
        return search(query);
    }
    
    @Override
    public List<SearchResult> searchAll(List<String> terms) {
        return search(terms); // Default behavior is AND search
    }
    
    @Override
    public List<SearchResult> searchPhrase(String phrase) {
        if (phrase == null || phrase.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // For phrase search, we need to find documents with terms in sequence
        String[] words = phrase.trim().split("\\s+");
        List<String> searchTerms = new ArrayList<>();
        
        for (String word : words) {
            String processed = textProcessor.processText(word).stream().findFirst().orElse(null);
            if (processed != null) {
                searchTerms.add(processed);
            }
        }
        
        if (searchTerms.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Get documents containing all terms
        List<DocumentIndex> documents = indexer.search(searchTerms);
        
        // Filter by phrase proximity (simplified implementation)
        List<DocumentIndex> phraseDocuments = filterByPhraseProximity(documents, searchTerms);
        
        return convertToSearchResults(phraseDocuments, searchTerms);
    }
    
    @Override
    public List<String> getSuggestions(String partialQuery) {
        if (partialQuery == null || partialQuery.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            searchLock.readLock().lock();
            
            // Get all indexed terms
            List<String> allTerms = indexer.getAllIndexedTerms();
            
            // Filter terms that start with the partial query
            return allTerms.stream()
                .filter(term -> term.toLowerCase().startsWith(partialQuery.toLowerCase()))
                .limit(10)
                .collect(Collectors.toList());
                
        } finally {
            searchLock.readLock().unlock();
        }
    }
    
    @Override
    public SearchStats getSearchStats() {
        return searchStats;
    }
    
    @Override
    public List<String> getPopularSearchTerms(int limit) {
        return searchStats.getMostPopularQueries(limit).stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getRelatedTerms(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            searchLock.readLock().lock();
            
            // Find documents containing the query
            List<DocumentIndex> documents = indexer.search(List.of(query));
            
            // Extract terms from these documents
            List<String> relatedTerms = new ArrayList<>();
            for (DocumentIndex doc : documents) {
                relatedTerms.addAll(doc.getAllTerms());
            }
            
            // Remove the original query term and get most frequent
            return relatedTerms.stream()
                .filter(term -> !term.equalsIgnoreCase(query))
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
                
        } finally {
            searchLock.readLock().unlock();
        }
    }
    
    @Override
    public List<SearchResult> fuzzySearch(String query, int maxDistance) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Simple fuzzy search implementation
        // In a real system, you'd use algorithms like Levenshtein distance
        
        List<String> terms = textProcessor.processText(query);
        List<SearchResult> results = new ArrayList<>();
        
        for (String term : terms) {
            // Find similar terms by checking if they contain the original term
            List<String> allTerms = indexer.getAllIndexedTerms();
            List<String> similarTerms = allTerms.stream()
                .filter(t -> t.toLowerCase().contains(term.toLowerCase()) || 
                            term.toLowerCase().contains(t.toLowerCase()))
                .collect(Collectors.toList());
            
            // Search for documents containing similar terms
            for (String similarTerm : similarTerms) {
                List<DocumentIndex> docs = indexer.getDocumentsForTerm(similarTerm);
                results.addAll(convertToSearchResults(docs, List.of(similarTerm)));
            }
        }
        
        // Remove duplicates and sort by relevance
        return results.stream()
            .distinct()
            .sorted((a, b) -> Double.compare(b.getRelevanceScore(), a.getRelevanceScore()))
            .collect(Collectors.toList());
    }
    
    @Override
    public PaginatedSearchResult searchWithPagination(SearchQuery query, int page, int pageSize) {
        if (page < 0) page = 0;
        if (pageSize <= 0) pageSize = 10;
        
        // Execute search without pagination
        List<SearchResult> allResults = search(query);
        
        // Create paginated result
        return PaginatedSearchResult.fromResults(allResults, page, pageSize);
    }
    
    @Override
    public long getResultCount(SearchQuery query) {
        List<SearchResult> results = search(query);
        return results.size();
    }
    
    @Override
    public boolean isReady() {
        return indexer != null && indexer.getIndexSize() > 0;
    }
    
    @Override
    public int getTotalDocuments() {
        return indexer != null ? indexer.getIndexSize() : 0;
    }
    
    @Override
    public int getTotalTerms() {
        return indexer != null ? indexer.getAllIndexedTerms().size() : 0;
    }
    
    /**
     * Convert DocumentIndex objects to SearchResult objects
     */
    private List<SearchResult> convertToSearchResults(List<DocumentIndex> documents, List<String> searchTerms) {
        List<SearchResult> results = new ArrayList<>();
        
        for (DocumentIndex doc : documents) {
            SearchResult result = new SearchResult(doc);
            
            // Add matched terms and positions
            for (String term : searchTerms) {
                if (doc.containsTerm(term)) {
                    result.addMatchedTerm(term, doc.getTermPositions(term));
                }
            }
            
            // Generate snippet if content is available
            if (doc.getContent() != null && !doc.getContent().trim().isEmpty()) {
                String snippet = result.generateHighlightedSnippet(doc.getContent(), 200);
                result.setSnippet(snippet);
            }
            
            // Calculate relevance score
            result.calculateRelevanceScore();
            
            results.add(result);
        }
        
        // Sort by relevance score
        results.sort((a, b) -> Double.compare(b.getRelevanceScore(), a.getRelevanceScore()));
        
        return results;
    }
    
    /**
     * Filter documents by required terms
     */
    private List<DocumentIndex> filterByRequiredTerms(List<DocumentIndex> documents, List<String> requiredTerms) {
        return documents.stream()
            .filter(doc -> requiredTerms.stream().allMatch(doc::containsTerm))
            .collect(Collectors.toList());
    }
    
    /**
     * Filter documents by excluded terms
     */
    private List<DocumentIndex> filterByExcludedTerms(List<DocumentIndex> documents, List<String> excludedTerms) {
        return documents.stream()
            .filter(doc -> excludedTerms.stream().noneMatch(doc::containsTerm))
            .collect(Collectors.toList());
    }
    
    /**
     * Apply search filters to results
     */
    private List<SearchResult> applyFilters(List<SearchResult> results, SearchFilters filters) {
        return results.stream()
            .filter(result -> filters.isDocumentAllowed(
                result.getDomain(),
                result.getContentLength(),
                result.getIndexedAt(),
                result.getRelevanceScore()
            ))
            .collect(Collectors.toList());
    }
    
    /**
     * Filter documents by phrase proximity (simplified)
     */
    private List<DocumentIndex> filterByPhraseProximity(List<DocumentIndex> documents, List<String> terms) {
        // This is a simplified implementation
        // In a real system, you'd check actual term positions
        return documents;
    }
}
