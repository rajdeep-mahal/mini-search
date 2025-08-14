package com.minisearch;

import com.minisearch.config.GeminiConfig;
import com.minisearch.gemini.GeminiService;
import com.minisearch.search.HybridSearchEngine;
import com.minisearch.search.SimpleSearchEngine;
import com.minisearch.search.SearchQuery;
import com.minisearch.search.SearchResult;
import com.minisearch.indexer.SimpleSearchIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Demo class showing Gemini AI integration with the search engine
 */
public class GeminiIntegrationDemo {
    
    private static final Logger logger = LoggerFactory.getLogger(GeminiIntegrationDemo.class);
    
    public static void main(String[] args) {
        logger.info("Starting Gemini Integration Demo...");
        
        try {
            // Initialize Gemini configuration
            GeminiConfig geminiConfig = new GeminiConfig();
            
            if (!geminiConfig.isEnabled()) {
                logger.error("Gemini API is not enabled. Please check your API key configuration.");
                return;
            }
            
            // Initialize Gemini service
            GeminiService geminiService = new GeminiService(geminiConfig.getApiKey());
            
            // Test Gemini service
            logger.info("Testing Gemini API connection...");
            if (geminiService.isAvailable()) {
                logger.info("✅ Gemini API is working!");
            } else {
                logger.error("❌ Gemini API is not responding");
                return;
            }
            
            // Initialize search components
            SimpleSearchIndexer indexer = new SimpleSearchIndexer();
            SimpleSearchEngine localEngine = new SimpleSearchEngine(indexer);
            HybridSearchEngine hybridEngine = new HybridSearchEngine(localEngine, geminiService);
            
            // Demo search queries
            String[] demoQueries = {
                "Dog",
                "Python programming",
                "Machine learning",
                "Space exploration"
            };
            
            for (String queryText : demoQueries) {
                logger.info("\n🔍 Searching for: '{}'", queryText);
                
                SearchQuery query = new SearchQuery(queryText);
                List<SearchResult> results = hybridEngine.search(query);
                
                logger.info("Found {} results:", results.size());
                
                for (int i = 0; i < results.size(); i++) {
                    SearchResult result = results.get(i);
                    String source = result.getMetadata("source") != null ? 
                        " (" + result.getMetadata("source") + ")" : "";
                    
                    logger.info("  {}. {} - {}{}", 
                        i + 1, 
                        result.getTitle(), 
                        result.getDomain(),
                        source);
                    
                    if (i == 0 && result.getMetadata("source") != null && 
                        "Gemini AI".equals(result.getMetadata("source"))) {
                        logger.info("     💡 AI-Generated content: {}", 
                            result.getSnippet().substring(0, Math.min(100, result.getSnippet().length())) + "...");
                    }
                }
                
                // Add delay between queries to avoid rate limiting
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            logger.info("\n🎉 Demo completed successfully!");
            
        } catch (Exception e) {
            logger.error("Demo failed with error", e);
        }
    }
}
