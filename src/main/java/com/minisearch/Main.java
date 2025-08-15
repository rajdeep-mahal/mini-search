package com.minisearch;

import com.minisearch.indexer.SimpleSearchIndexer;
import com.minisearch.model.WebPage;
import com.minisearch.search.SimpleSearchEngine;
import com.minisearch.web.SimpleWebServer;
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
            // Create indexer and search engine
            SimpleSearchIndexer indexer = new SimpleSearchIndexer();
            SimpleSearchEngine searchEngine = new SimpleSearchEngine(indexer);
            
            // Index sample pages
            indexSamplePages(indexer);
            
            // Start web server
            int port = 8080;
            if (args.length > 0) {
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid port number, using default: 8080");
                }
            }
            
            SimpleWebServer webServer = new SimpleWebServer(port, searchEngine);
            webServer.start();
            
            logger.info("🌐 Mini Search Engine started successfully!");
            logger.info("📱 Open your browser and go to: http://localhost:{}", port);
            logger.info("🔍 Try searching for: 'spring', 'python', 'javascript', 'elasticsearch', 'scrapy'");
            logger.info("⏹️  Press Ctrl+C to stop the server");
            
            // Keep the server running
            while (true) {
                Thread.sleep(1000);
            }
            
        } catch (Exception e) {
            logger.error("Error starting Mini Search Engine", e);
            System.exit(1);
        }
    }
    
    /**
     * Index sample pages for demonstration
     */
    private static void indexSamplePages(SimpleSearchIndexer indexer) {
        logger.info("Indexing sample pages...");
        
        WebPage webPage1 = new WebPage("/spring");
        webPage1.setTitle("Spring Framework - Java Web Development");
        webPage1.setContent("Spring Framework is a comprehensive solution for building enterprise-grade Java applications. It provides a wide range of features that make Java development easier and more productive. Learn about Spring's core features, web development, data access, and more.");
        webPage1.setDomain("localhost:8080");
        
        WebPage webPage2 = new WebPage("/python");
        webPage2.setTitle("Python Programming Language");
        webPage2.setContent("Python is a high-level, interpreted programming language known for its simplicity and readability. It's widely used in web development, data science, artificial intelligence, and automation. Learn about Python's key characteristics, web frameworks, and data science capabilities.");
        webPage2.setDomain("localhost:8080");
        
        WebPage webPage3 = new WebPage("/javascript");
        webPage3.setTitle("JavaScript - Modern Web Development");
        webPage3.setContent("JavaScript is a versatile programming language that powers the modern web. It runs in browsers and has expanded to server-side development with Node.js. Learn about JavaScript's core features, frontend frameworks, and Node.js ecosystem.");
        webPage3.setDomain("localhost:8080");
        
        WebPage webPage4 = new WebPage("/elasticsearch");
        webPage4.setTitle("Elasticsearch - Search Engine Technology");
        webPage4.setContent("Elasticsearch is a distributed, RESTful search and analytics engine built on Apache Lucene. It's designed for horizontal scalability and real-time search capabilities. Learn about Elasticsearch's core concepts, features, and use cases.");
        webPage4.setDomain("localhost:8080");
        
        WebPage webPage5 = new WebPage("/scrapy");
        webPage5.setTitle("Scrapy - Web Scraping Framework");
        webPage5.setContent("Scrapy is a fast high-level web crawling and web scraping framework written in Python. It's used to extract structured data from websites and can be used for data mining, monitoring, and automated testing. Learn about Scrapy's architecture and features.");
        webPage5.setDomain("localhost:8080");
        
        // Index the pages
        indexer.indexPage(webPage1);
        indexer.indexPage(webPage2);
        indexer.indexPage(webPage3);
        indexer.indexPage(webPage4);
        indexer.indexPage(webPage5);
        
        logger.info("Indexed {} sample pages", indexer.getIndexSize());
    }
}
