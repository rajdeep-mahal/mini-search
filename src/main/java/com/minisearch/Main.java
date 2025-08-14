package com.minisearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class for the Mini Search Engine
 * This is the entry point for the application
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting Mini Search Engine...");
        
        try {
            // TODO: Initialize search engine components
            // TODO: Start web crawler
            // TODO: Start web server for search interface
            
            logger.info("Mini Search Engine started successfully!");
            
            // Keep the application running
            Thread.currentThread().join();
            
        } catch (InterruptedException e) {
            logger.info("Mini Search Engine interrupted, shutting down...");
        } catch (Exception e) {
            logger.error("Error starting Mini Search Engine", e);
            System.exit(1);
        }
    }
}
