package com.minisearch;

import com.minisearch.dataset.DatasetProcessor;
import com.minisearch.indexer.SimpleSearchIndexer;
import com.minisearch.search.SimpleSearchEngine;
import com.minisearch.web.SimpleWebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Runner for processing custom datasets and starting the search engine
 */
public class DatasetProcessorRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DatasetProcessorRunner.class);
    
    public static void main(String[] args) {
        logger.info("Starting Mini Search Engine with Custom Dataset Processing...");
        
        try {
            // Create indexer and search engine
            SimpleSearchIndexer indexer = new SimpleSearchIndexer();
            SimpleSearchEngine searchEngine = new SimpleSearchEngine(indexer);
            
            // Process all datasets
            logger.info("Processing datasets...");
            DatasetProcessor processor = new DatasetProcessor(indexer);
            processor.processAllDatasets();
            
            logger.info("Dataset processing completed!");
            logger.info("Index size: {} pages", indexer.getIndexSize());
            
            // Show available search terms
            logger.info("Available search terms: {}", indexer.getAllIndexedTerms());
            
            // Start web server
            logger.info("Starting web server on port 8080...");
            SimpleWebServer webServer = new SimpleWebServer(8080, searchEngine);
            webServer.start();
            
            logger.info("🎉 Web server started successfully!");
            logger.info("🌐 Open your browser and go to: http://localhost:8080");
            logger.info("🔍 Search for terms from your datasets!");
            logger.info("📊 Total indexed pages: {}", indexer.getIndexSize());
            logger.info("");
            logger.info("Press Enter to stop the web server...");
            
            // Wait for user input
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            
            // Stop server
            logger.info("Stopping web server...");
            webServer.stop();
            logger.info("Web server stopped successfully!");
            
        } catch (Exception e) {
            logger.error("Error during dataset processing", e);
            System.exit(1);
        }
    }
}
