package com.minisearch.indexer;

import com.minisearch.model.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Map;

/**
 * Implementation of SearchIndexer that builds and maintains search indexes
 * Uses InvertedIndex for fast document retrieval and TextProcessor for text analysis
 */
public class SimpleSearchIndexer implements SearchIndexer {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleSearchIndexer.class);
    
    // Core components
    private final InvertedIndex invertedIndex;
    private final TextProcessor textProcessor;
    private final IndexingStats stats;
    
    // Thread safety
    private final ReadWriteLock indexLock;
    
    public SimpleSearchIndexer() {
        this.invertedIndex = new InvertedIndex();
        this.textProcessor = new TextProcessor();
        this.stats = new IndexingStats();
        this.indexLock = new ReentrantReadWriteLock();
        
        logger.info("SimpleSearchIndexer initialized");
    }
    
    @Override
    public void indexPage(WebPage webPage) {
        if (webPage == null || webPage.getUrl() == null || webPage.getUrl().trim().isEmpty()) {
            logger.warn("Cannot index null or invalid web page");
            return;
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            indexLock.writeLock().lock();
            
            // Check if already indexed
            if (invertedIndex.isDocumentIndexed(webPage.getUrl())) {
                logger.info("Updating existing indexed page: {}", webPage.getUrl());
                updatePage(webPage);
                return;
            }
            
            logger.info("Indexing new page: {}", webPage.getUrl());
            
            // Process the web page content
            DocumentIndex documentIndex = processWebPage(webPage);
            
            // Add to inverted index
            invertedIndex.addDocument(documentIndex);
            
            // Update statistics
            long indexingTime = System.currentTimeMillis() - startTime;
            stats.recordPageIndexed(indexingTime);
            stats.setIndexSize(invertedIndex.getTotalDocuments());
            
            logger.info("Successfully indexed page: {} ({} terms, {}ms)", 
                webPage.getUrl(), 
                documentIndex.getAllTerms().size(),
                indexingTime);
                
        } catch (Exception e) {
            logger.error("Error indexing page: {}", webPage.getUrl(), e);
        } finally {
            indexLock.writeLock().unlock();
        }
    }
    
    @Override
    public void indexPages(List<WebPage> webPages) {
        if (webPages == null || webPages.isEmpty()) {
            logger.warn("No web pages to index");
            return;
        }
        
        logger.info("Starting batch indexing of {} pages", webPages.size());
        
        int successCount = 0;
        int failureCount = 0;
        
        for (WebPage webPage : webPages) {
            try {
                indexPage(webPage);
                successCount++;
            } catch (Exception e) {
                logger.error("Failed to index page: {}", webPage.getUrl(), e);
                failureCount++;
            }
        }
        
        logger.info("Batch indexing completed: {} successful, {} failed", successCount, failureCount);
    }
    
    @Override
    public void removePage(String url) {
        if (url == null || url.trim().isEmpty()) {
            logger.warn("Cannot remove page with null or empty URL");
            return;
        }
        
        try {
            indexLock.writeLock().lock();
            
            if (invertedIndex.isDocumentIndexed(url)) {
                invertedIndex.removeDocument(url);
                stats.recordPageRemoved();
                stats.setIndexSize(invertedIndex.getTotalDocuments());
                logger.info("Removed page from index: {}", url);
            } else {
                logger.warn("Page not found in index: {}", url);
            }
            
        } catch (Exception e) {
            logger.error("Error removing page: {}", url, e);
        } finally {
            indexLock.writeLock().unlock();
        }
    }
    
    @Override
    public void updatePage(WebPage webPage) {
        if (webPage == null || webPage.getUrl() == null) {
            logger.warn("Cannot update null or invalid web page");
            return;
        }
        
        try {
            indexLock.writeLock().lock();
            
            if (invertedIndex.isDocumentIndexed(webPage.getUrl())) {
                // Process the updated web page
                DocumentIndex updatedDocument = processWebPage(webPage);
                
                // Update in inverted index
                invertedIndex.updateDocument(updatedDocument);
                
                // Update statistics
                stats.recordPageUpdated();
                
                logger.info("Successfully updated indexed page: {}", webPage.getUrl());
            } else {
                logger.warn("Page not found in index, adding as new: {}", webPage.getUrl());
                indexPage(webPage);
            }
            
        } catch (Exception e) {
            logger.error("Error updating page: {}", webPage.getUrl(), e);
        } finally {
            indexLock.writeLock().unlock();
        }
    }
    
    @Override
    public int getIndexSize() {
        try {
            indexLock.readLock().lock();
            return invertedIndex.getTotalDocuments();
        } finally {
            indexLock.readLock().unlock();
        }
    }
    
    @Override
    public boolean isIndexed(String url) {
        try {
            indexLock.readLock().lock();
            return invertedIndex.isDocumentIndexed(url);
        } finally {
            indexLock.readLock().unlock();
        }
    }
    
    @Override
    public IndexingStats getIndexingStats() {
        try {
            indexLock.readLock().lock();
            stats.setIndexSize(invertedIndex.getTotalDocuments());
            return stats;
        } finally {
            indexLock.readLock().unlock();
        }
    }
    
    @Override
    public void optimizeIndex() {
        try {
            indexLock.writeLock().lock();
            
            logger.info("Starting index optimization...");
            
            // Get current stats
            Map<String, Object> beforeStats = invertedIndex.getIndexStats();
            logger.info("Before optimization: {}", beforeStats);
            
            // For now, just log optimization opportunity
            // In a real implementation, this could include:
            // - Merging small index segments
            // - Removing low-frequency terms
            // - Compressing index data
            // - Rebalancing data structures
            
            logger.info("Index optimization completed (basic implementation)");
            
        } catch (Exception e) {
            logger.error("Error during index optimization", e);
        } finally {
            indexLock.writeLock().unlock();
        }
    }
    
    @Override
    public void clearIndex() {
        try {
            indexLock.writeLock().lock();
            
            logger.info("Clearing entire search index");
            invertedIndex.clear();
            stats.setIndexSize(0);
            
            logger.info("Search index cleared successfully");
            
        } catch (Exception e) {
            logger.error("Error clearing search index", e);
        } finally {
            indexLock.writeLock().unlock();
        }
    }
    
    /**
     * Process a web page and create a DocumentIndex
     * @param webPage The web page to process
     * @return Processed DocumentIndex
     */
    private DocumentIndex processWebPage(WebPage webPage) {
        // Create document index
        DocumentIndex documentIndex = new DocumentIndex(webPage);
        
        // Process text content
        String content = webPage.getContent();
        if (content != null && !content.trim().isEmpty()) {
            // Extract terms with frequencies and positions
            Map<String, Integer> termFrequencies = textProcessor.calculateTermFrequencies(content);
            Map<String, List<Integer>> termPositions = textProcessor.extractTermsWithPositions(content);
            
            // Add terms to document
            for (Map.Entry<String, Integer> entry : termFrequencies.entrySet()) {
                String term = entry.getKey();
                Integer frequency = entry.getValue();
                List<Integer> positions = termPositions.get(term);
                
                documentIndex.addTerm(term, frequency, positions);
            }
            
            // Calculate relevance score
            documentIndex.calculateRelevanceScore();
        }
        
        return documentIndex;
    }
    
    /**
     * Search for documents containing specific terms
     * @param terms List of search terms
     * @return List of matching documents
     */
    public List<DocumentIndex> search(List<String> terms) {
        try {
            indexLock.readLock().lock();
            return invertedIndex.search(terms);
        } finally {
            indexLock.readLock().unlock();
        }
    }
    
    /**
     * Search for documents containing ANY of the terms
     * @param terms List of search terms
     * @return List of matching documents
     */
    public List<DocumentIndex> searchAny(List<String> terms) {
        try {
            indexLock.readLock().lock();
            return invertedIndex.searchAny(terms);
        } finally {
            indexLock.readLock().unlock();
        }
    }
    
    /**
     * Get documents containing a specific term
     * @param term The search term
     * @return List of documents containing the term
     */
    public List<DocumentIndex> getDocumentsForTerm(String term) {
        try {
            indexLock.readLock().lock();
            return invertedIndex.getDocumentsForTerm(term);
        } finally {
            indexLock.readLock().unlock();
        }
    }
    
    /**
     * Get index statistics
     * @return Map containing index statistics
     */
    public Map<String, Object> getIndexStats() {
        try {
            indexLock.readLock().lock();
            return invertedIndex.getIndexStats();
        } finally {
            indexLock.readLock().unlock();
        }
    }
    
    /**
     * Get all indexed terms
     * @return Set of all indexed terms
     */
    public List<String> getAllIndexedTerms() {
        try {
            indexLock.readLock().lock();
            return new ArrayList<>(invertedIndex.getAllTerms());
        } finally {
            indexLock.readLock().unlock();
        }
    }
    
    /**
     * Get the TextProcessor instance (for testing/debugging)
     * @return TextProcessor instance
     */
    public TextProcessor getTextProcessor() {
        return textProcessor;
    }
}
