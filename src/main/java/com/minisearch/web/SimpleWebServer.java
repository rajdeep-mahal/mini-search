package com.minisearch.web;

import com.minisearch.search.SimpleSearchEngine;
import com.minisearch.search.SearchQuery;
import com.minisearch.search.SearchResult;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Simple HTTP server for the search engine web interface
 */
public class SimpleWebServer {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleWebServer.class);
    
    private final HttpServer server;
    private final SimpleSearchEngine searchEngine;
    private final int port;
    
    public SimpleWebServer(int port, SimpleSearchEngine searchEngine) throws IOException {
        this.port = port;
        this.searchEngine = searchEngine;
        
        // Create HTTP server
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Set up routes
        setupRoutes();
        
        // Set thread pool
        server.setExecutor(Executors.newFixedThreadPool(10));
        
        logger.info("SimpleWebServer initialized on port {}", port);
    }
    
    /**
     * Set up all the HTTP routes
     */
    private void setupRoutes() {
        // Main search page
        server.createContext("/", new SearchPageHandler());
        
        // Search API endpoint
        server.createContext("/api/search", new SearchApiHandler());
        
        // Search suggestions API
        server.createContext("/api/suggestions", new SuggestionsApiHandler());
        
        // Search statistics API
        server.createContext("/api/stats", new StatsApiHandler());
        
        // Health check endpoint
        server.createContext("/health", new HealthCheckHandler());
        
        logger.info("Routes configured: /, /api/search, /api/suggestions, /api/stats, /health");
    }
    
    /**
     * Start the web server
     */
    public void start() {
        server.start();
        logger.info("SimpleWebServer started on http://localhost:{}", port);
        logger.info("Search interface available at: http://localhost:{}", port);
    }
    
    /**
     * Stop the web server
     */
    public void stop() {
        server.stop(0);
        logger.info("SimpleWebServer stopped");
    }
    
    /**
     * Get the server port
     */
    public int getPort() {
        return port;
    }
    
    /**
     * Check if server is running
     */
    public boolean isRunning() {
        return server != null;
    }
    
    /**
     * Handler for the main search page
     */
    private class SearchPageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                // Return the main search page HTML
                String html = generateSimpleSearchPage();
                sendResponse(exchange, 200, "text/html", html);
            } else {
                sendResponse(exchange, 405, "text/plain", "Method not allowed");
            }
        }
    }
    
    /**
     * Handler for search API requests
     */
    private class SearchApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                // Parse query parameters
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.contains("q=")) {
                    String searchTerm = query.substring(query.indexOf("q=") + 2);
                    if (searchTerm.contains("&")) {
                        searchTerm = searchTerm.substring(0, searchTerm.indexOf("&"));
                    }
                    searchTerm = java.net.URLDecoder.decode(searchTerm, StandardCharsets.UTF_8);
                    
                    // Perform search
                    SearchQuery searchQuery = new SearchQuery(searchTerm);
                    List<SearchResult> results = searchEngine.search(searchQuery);
                    
                    // Return results as JSON
                    String json = generateSearchResultsJson(results, searchTerm);
                    sendResponse(exchange, 200, "application/json", json);
                } else {
                    sendResponse(exchange, 400, "application/json", "{\"error\": \"Missing search query\"}");
                }
            } else {
                sendResponse(exchange, 405, "text/plain", "Method not allowed");
            }
        }
    }
    
    /**
     * Handler for search suggestions
     */
    private class SuggestionsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.contains("q=")) {
                    String partialQuery = query.substring(query.indexOf("q=") + 2);
                    if (partialQuery.contains("&")) {
                        partialQuery = partialQuery.substring(0, partialQuery.indexOf("&"));
                    }
                    partialQuery = java.net.URLDecoder.decode(partialQuery, StandardCharsets.UTF_8);
                    
                    List<String> suggestions = searchEngine.getSuggestions(partialQuery);
                    String json = generateSuggestionsJson(suggestions);
                    sendResponse(exchange, 200, "application/json", json);
                } else {
                    sendResponse(exchange, 400, "application/json", "{\"error\": \"Missing query parameter\"}");
                }
            } else {
                sendResponse(exchange, 405, "text/plain", "Method not allowed");
            }
        }
    }
    
    /**
     * Handler for search statistics
     */
    private class StatsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                var stats = searchEngine.getSearchStats();
                String json = generateStatsJson(stats);
                sendResponse(exchange, 200, "application/json", json);
            } else {
                sendResponse(exchange, 405, "text/plain", "Method not allowed");
            }
        }
    }
    
    /**
     * Handler for health check
     */
    private class HealthCheckHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = "{\"status\": \"healthy\", \"searchEngine\": " + 
                                searchEngine.isReady() + ", \"totalDocuments\": " + 
                                searchEngine.getTotalDocuments() + "}";
                sendResponse(exchange, 200, "application/json", response);
            } else {
                sendResponse(exchange, 405, "text/plain", "Method not allowed");
            }
        }
    }
    
    /**
     * Send HTTP response
     */
    private void sendResponse(HttpExchange exchange, int statusCode, String contentType, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", contentType);
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
    
    /**
     * Generate a simple search page HTML
     */
    private String generateSimpleSearchPage() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"en\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<title>🦁 Animals Search Engine</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); min-height: 100vh; }");
        html.append(".container { max-width: 900px; margin: 0 auto; background: white; padding: 30px; border-radius: 15px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }");
        html.append("h1 { color: #333; text-align: center; margin-bottom: 10px; font-size: 2.5em; }");
        html.append(".subtitle { color: #666; text-align: center; margin-bottom: 30px; font-size: 1.1em; }");
        html.append(".search-box { text-align: center; margin-bottom: 30px; }");
        html.append("#searchInput { width: 70%; padding: 15px; font-size: 18px; border: 2px solid #ddd; border-radius: 25px; outline: none; }");
        html.append("#searchInput:focus { border-color: #4facfe; }");
        html.append("button { padding: 15px 30px; font-size: 18px; background-color: #4facfe; color: white; border: none; border-radius: 25px; cursor: pointer; margin-left: 10px; }");
        html.append("button:hover { background-color: #3d9be8; }");
        html.append(".suggestions { text-align: center; margin: 20px 0; }");
        html.append(".suggestion { display: inline-block; background: #4facfe; color: white; padding: 8px 15px; margin: 5px; border-radius: 20px; cursor: pointer; font-size: 14px; }");
        html.append(".suggestion:hover { background: #3d9be8; }");
        html.append(".categories { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin: 20px 0; }");
        html.append(".category { background: #f0f8ff; padding: 15px; border-radius: 10px; text-align: center; border: 2px solid #4facfe; }");
        html.append(".category h3 { margin: 0 0 10px 0; color: #4facfe; }");
        html.append(".category p { margin: 0; color: #666; font-size: 14px; }");
        html.append(".results { margin-top: 30px; }");
        html.append(".result { border: 1px solid #ddd; padding: 15px; margin-bottom: 15px; border-radius: 5px; }");
        html.append(".result-title { color: #1a0dab; font-size: 18px; text-decoration: none; font-weight: bold; }");
        html.append(".result-title:hover { text-decoration: underline; }");
        html.append(".result-url { color: #006621; font-size: 14px; margin: 5px 0; }");
        html.append(".result-snippet { color: #545454; margin: 10px 0; }");
        html.append(".stats { margin-top: 30px; padding: 15px; background-color: #f9f9f9; border-radius: 5px; font-size: 14px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"container\">");
        html.append("<h1>🦁 Animals Search Engine</h1>");
        html.append("<div class=\"subtitle\">Discover amazing facts about animals from around the world!</div>");
        html.append("<div class=\"search-box\">");
        html.append("<input type=\"text\" id=\"searchInput\" placeholder=\"Search for animals, habitats, or characteristics...\">");
        html.append("<button onclick=\"performSearch()\">🔍 Search</button>");
        html.append("</div>");
        html.append("<div class=\"suggestions\">");
        html.append("<div class=\"suggestion\" onclick=\"search('lion')\">🦁 lion</div>");
        html.append("<div class=\"suggestion\" onclick=\"search('ocean')\">🌊 ocean</div>");
        html.append("<div class=\"suggestion\" onclick=\"search('mammal')\">🐘 mammal</div>");
        html.append("<div class=\"suggestion\" onclick=\"search('endangered')\">⚠️ endangered</div>");
        html.append("<div class=\"suggestion\" onclick=\"search('africa')\">🌍 africa</div>");
        html.append("<div class=\"suggestion\" onclick=\"search('intelligent')\">🧠 intelligent</div>");
        html.append("</div>");
        html.append("<div class=\"categories\">");
        html.append("<div class=\"category\">");
        html.append("<h3>🦁 Big Cats</h3>");
        html.append("<p>Lions, Tigers, and more</p>");
        html.append("</div>");
        html.append("<div class=\"category\">");
        html.append("<h3>🌊 Ocean Life</h3>");
        html.append("<p>Dolphins, Sharks, Whales</p>");
        html.append("</div>");
        html.append("<div class=\"category\">");
        html.append("<h3>🐘 Large Mammals</h3>");
        html.append("<p>Elephants, Gorillas, Whales</p>");
        html.append("</div>");
        html.append("<div class=\"category\">");
        html.append("<h3>🦅 Birds of Prey</h3>");
        html.append("<p>Eagles, Owls, and more</p>");
        html.append("</div>");
        html.append("</div>");
        html.append("<div id=\"results\" class=\"results\"></div>");
        html.append("<div id=\"stats\" class=\"stats\"></div>");
        html.append("</div>");
        html.append("<script>");
        html.append("document.getElementById('searchInput').addEventListener('keypress', function(e) {");
        html.append("if (e.key === 'Enter') { performSearch(); }");
        html.append("});");
        html.append("function search(query) {");
        html.append("document.getElementById('searchInput').value = query;");
        html.append("performSearch();");
        html.append("}");
        html.append("function performSearch() {");
        html.append("var query = document.getElementById('searchInput').value.trim();");
        html.append("if (!query) return;");
        html.append("document.getElementById('results').innerHTML = '<p>🔍 Searching for \"' + query + '\"...</p>';");
        html.append("fetch('/api/search?q=' + encodeURIComponent(query))");
        html.append(".then(response => response.json())");
        html.append(".then(data => {");
        html.append("if (data.results && data.results.length > 0) {");
        html.append("displayResults(data.results, query);");
        html.append("} else {");
        html.append("document.getElementById('results').innerHTML = '<p>❌ No animals found for \"' + query + '\"</p>';");
        html.append("}");
        html.append("updateStats();");
        html.append("})");
        html.append(".catch(error => {");
        html.append("document.getElementById('results').innerHTML = '<p>❌ Error: ' + error.message + '</p>';");
        html.append("});");
        html.append("}");
        html.append("function displayResults(results, query) {");
        html.append("var resultsDiv = document.getElementById('results');");
        html.append("var html = '<h3>🦁 Found ' + results.length + ' animals for \"' + query + '\"</h3>';");
        html.append("for (var i = 0; i < results.length; i++) {");
        html.append("var result = results[i];");
        html.append("html += '<div class=\"result\">';");
        html.append("html += '<a href=\"' + result.url + '\" class=\"result-title\" target=\"_blank\">' + (result.title || result.url) + '</a>';");
        html.append("html += '<div class=\"result-url\">' + result.url + '</div>';");
        html.append("html += '<div class=\"result-snippet\">' + (result.snippet || 'No description available') + '</div>';");
        html.append("html += '</div>';");
        html.append("}");
        html.append("resultsDiv.innerHTML = html;");
        html.append("}");
        html.append("function updateStats() {");
        html.append("fetch('/api/stats')");
        html.append(".then(response => response.json())");
        html.append(".then(data => {");
        html.append("var statsDiv = document.getElementById('stats');");
        html.append("statsDiv.innerHTML = '<strong>📊 Animals Search Statistics:</strong><br>' +");
        html.append("'🔍 Total searches: ' + data.totalSearches + '<br>' +");
        html.append("'✅ Success rate: ' + data.successRate + '<br>' +");
        html.append("'⏱️ Average search time: ' + data.averageSearchTime + '<br>' +");
        html.append("'🦁 Total animals indexed: ' + (data.totalDocuments || 'N/A');");
        html.append("})");
        html.append(".catch(error => console.error('Stats error:', error));");
        html.append("}");
        html.append("window.addEventListener('load', updateStats);");
        html.append("</script>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
    
    /**
     * Generate search results as JSON
     */
    private String generateSearchResultsJson(List<SearchResult> results, String query) {
        StringBuilder json = new StringBuilder();
        json.append("{\"query\":\"").append(query).append("\",\"results\":[");
        
        for (int i = 0; i < results.size(); i++) {
            SearchResult result = results.get(i);
            json.append("{");
            json.append("\"title\":\"").append(escapeJson(result.getTitle())).append("\",");
            json.append("\"url\":\"").append(escapeJson(result.getUrl())).append("\",");
            json.append("\"snippet\":\"").append(escapeJson(result.getSnippet())).append("\",");
            json.append("\"relevanceScore\":").append(result.getRelevanceScore()).append(",");
            json.append("\"domain\":\"").append(escapeJson(result.getDomain())).append("\"");
            json.append("}");
            if (i < results.size() - 1) json.append(",");
        }
        
        json.append("]}");
        return json.toString();
    }
    
    /**
     * Generate suggestions as JSON
     */
    private String generateSuggestionsJson(List<String> suggestions) {
        StringBuilder json = new StringBuilder();
        json.append("{\"suggestions\":[");
        
        for (int i = 0; i < suggestions.size(); i++) {
            json.append("\"").append(escapeJson(suggestions.get(i))).append("\"");
            if (i < suggestions.size() - 1) json.append(",");
        }
        
        json.append("]}");
        return json.toString();
    }
    
    /**
     * Generate statistics as JSON
     */
    private String generateStatsJson(com.minisearch.search.SearchStats stats) {
        return String.format(
            "{\"totalSearches\":%d,\"successRate\":\"%.1f%%\",\"averageSearchTime\":\"%.2fms\",\"totalDocuments\":%d}",
            stats.getTotalSearches(),
            stats.getSuccessRate(),
            stats.getAverageSearchTime(),
            searchEngine.getTotalDocuments()
        );
    }
    
    /**
     * Escape JSON string
     */
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
