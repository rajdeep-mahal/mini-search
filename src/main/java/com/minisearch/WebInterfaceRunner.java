package com.minisearch;

import com.minisearch.indexer.SimpleSearchIndexer;
import com.minisearch.search.SimpleSearchEngine;
import com.minisearch.web.SimpleWebServer;
import com.minisearch.model.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Dedicated runner for the web interface
 */
public class WebInterfaceRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(WebInterfaceRunner.class);
    
    public static void main(String[] args) {
        logger.info("Starting Mini Search Engine Web Interface...");
        
        try {
            // Create indexer and search engine
            SimpleSearchIndexer indexer = new SimpleSearchIndexer();
            SimpleSearchEngine searchEngine = new SimpleSearchEngine(indexer);
            
            // Index some sample pages for the web interface
            logger.info("Indexing sample pages for search...");
            
            WebPage webPage1 = new WebPage("https://example.com/java-web");
            webPage1.setTitle("Java Web Development Guide");
            webPage1.setContent("Java is excellent for building web applications. Spring Boot makes it easy to create RESTful APIs and web services. Java web development is popular in enterprise environments.");
            webPage1.setDomain("example.com");
            
            WebPage webPage2 = new WebPage("https://example.com/python-web");
            webPage2.setTitle("Python Web Development");
            webPage2.setContent("Python with Django and Flask is great for web development. FastAPI provides modern async web framework capabilities. Python web development is growing rapidly.");
            webPage2.setDomain("example.com");
            
            WebPage webPage3 = new WebPage("https://example.com/javascript-web");
            webPage3.setTitle("JavaScript Modern Web Development");
            webPage3.setContent("JavaScript powers the modern web. React, Vue, and Angular are popular frontend frameworks. Node.js enables server-side JavaScript development.");
            webPage3.setDomain("example.com");
            
            WebPage webPage4 = new WebPage("https://example.com/search-engine");
            webPage4.setTitle("Search Engine Technology");
            webPage4.setContent("Search engines use complex algorithms to find and rank information. They employ techniques like inverted indexing, relevance scoring, and machine learning to provide accurate results.");
            webPage4.setDomain("example.com");
            
            WebPage webPage5 = new WebPage("https://example.com/web-crawling");
            webPage5.setTitle("Web Crawling and Indexing");
            webPage5.setContent("Web crawlers systematically browse websites to discover and index content. They follow links, extract text, and build searchable databases of web pages.");
            webPage5.setDomain("example.com");
            
            // Index the pages
            indexer.indexPage(webPage1);
            indexer.indexPage(webPage2);
            indexer.indexPage(webPage3);
            indexer.indexPage(webPage4);
            indexer.indexPage(webPage5);
            
            logger.info("Indexed {} pages for search", indexer.getIndexSize());
            
            // Create and start web server
            logger.info("Starting web server on port 8080...");
            
            SimpleWebServer webServer = new SimpleWebServer(8080, searchEngine);
            webServer.start();
            
            logger.info("🎉 Web server started successfully!");
            logger.info("🌐 Open your browser and go to: http://localhost:8080");
            logger.info("🔍 Try searching for: 'web development', 'java', 'python', 'javascript', 'search engine'");
            logger.info("📊 The interface includes search suggestions, real-time results, and performance statistics");
            logger.info("");
            logger.info("Press Enter to stop the web server...");
            
            // Wait for user input to stop server
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            
            // Stop the web server
            logger.info("Stopping web server...");
            webServer.stop();
            logger.info("Web server stopped successfully!");
            
        } catch (Exception e) {
            logger.error("Error starting web interface", e);
            System.exit(1);
        }
    }
}
