package com.minisearch;

import com.minisearch.dataset.AnimalsDataset;
import com.minisearch.indexer.SimpleSearchIndexer;
import com.minisearch.search.SimpleSearchEngine;
import com.minisearch.web.SimpleWebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Animals Search Runner - Specialized runner for testing search with animals dataset
 * Provides a focused, educational search experience
 */
public class AnimalsSearchRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(AnimalsSearchRunner.class);
    
    public static void main(String[] args) {
        logger.info("🦁 Starting Mini Search Engine with Animals Dataset...");
        
        try {
            // Create indexer and search engine
            SimpleSearchIndexer indexer = new SimpleSearchIndexer();
            SimpleSearchEngine searchEngine = new SimpleSearchEngine(indexer);
            
            // Load and index the animals dataset
            logger.info("📚 Loading animals dataset...");
            AnimalsDataset.printDatasetStats();
            
            var animals = AnimalsDataset.getAnimalsDataset();
            logger.info("🔍 Indexing {} animal pages...", animals.size());
            
            for (var animal : animals) {
                indexer.indexPage(animal);
            }
            
            logger.info("✅ Successfully indexed {} animal pages", indexer.getIndexSize());
            logger.info("📊 Index contains {} unique terms", searchEngine.getTotalTerms());
            
            // Start web server
            logger.info("🌐 Starting web server on port 8080...");
            SimpleWebServer webServer = new SimpleWebServer(8080, searchEngine);
            webServer.start();
            
            logger.info("🎉 Animals Search Engine is now running!");
            logger.info("🌐 Open your browser and go to: http://localhost:8080");
            logger.info("");
            logger.info("🔍 Try these search examples:");
            logger.info("   • 'lion' - Find information about lions");
            logger.info("   • 'ocean' - Find ocean-dwelling animals");
            logger.info("   • 'mammal' - Find all mammals");
            logger.info("   • 'hunt' - Find hunting animals");
            logger.info("   • 'endangered' - Find endangered species");
            logger.info("   • 'africa' - Find animals from Africa");
            logger.info("   • 'intelligent' - Find smart animals");
            logger.info("   • 'large' - Find large animals");
            logger.info("");
            logger.info("📚 The dataset includes:");
            logger.info("   • 12 different animal species");
            logger.info("   • Various habitats (ocean, forest, savanna)");
            logger.info("   • Different animal types (mammals, birds, fish, reptiles)");
            logger.info("   • Rich descriptions with scientific names and facts");
            logger.info("");
            logger.info("Press Enter to stop the server...");
            
            // Wait for user input
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            
            // Stop server
            logger.info("🛑 Stopping web server...");
            webServer.stop();
            logger.info("✅ Web server stopped successfully!");
            
        } catch (Exception e) {
            logger.error("❌ Error starting animals search engine", e);
            System.exit(1);
        }
    }
}
