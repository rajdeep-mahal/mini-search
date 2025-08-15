package com.minisearch.web;

import com.minisearch.search.SearchEngine;
import com.minisearch.search.SearchQuery;
import com.minisearch.search.SearchResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Simple web server that provides a search interface
 */
public class SimpleWebServer {
    private static final Logger logger = LoggerFactory.getLogger(SimpleWebServer.class);
    private final HttpServer server;
    private final SearchEngine searchEngine;

    public SimpleWebServer(int port, SearchEngine searchEngine) throws IOException {
        this.searchEngine = searchEngine;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.setExecutor(Executors.newFixedThreadPool(10));
        configureRoutes();
        logger.info("SimpleWebServer initialized on port {}", port);
    }

    /**
     * Configure HTTP routes
     */
    private void configureRoutes() {
        // Main search page
        server.createContext("/", new SearchPageHandler());
        
        // Search API endpoint
        server.createContext("/api/search", new SearchApiHandler());
        
        // Suggestions API endpoint
        server.createContext("/api/suggestions", new SuggestionsApiHandler());
        
        // Custom sample pages
        server.createContext("/spring", new CustomPageHandler("/spring"));
        server.createContext("/python", new CustomPageHandler("/python"));
        server.createContext("/javascript", new CustomPageHandler("/javascript"));
        server.createContext("/elasticsearch", new CustomPageHandler("/elasticsearch"));
        server.createContext("/scrapy", new CustomPageHandler("/scrapy"));

        
        // Health check endpoint
        server.createContext("/health", new HealthCheckHandler());
        
        logger.info("Routes configured: /, /api/search, /api/suggestions, /spring, /python, /javascript, /elasticsearch, /scrapy, /health");
    }

    /**
     * Start the web server
     */
    public void start() {
        server.start();
        logger.info("SimpleWebServer started on http://localhost:{}", server.getAddress().getPort());
        logger.info("Search interface available at: http://localhost:{}", server.getAddress().getPort());
    }

    /**
     * Stop the web server
     */
    public void stop() {
        server.stop(0);
        logger.info("SimpleWebServer stopped");
    }

    /**
     * Handler for the main search page
     */
    private class SearchPageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String html = generateBeautifulSearchPage();
                sendResponse(exchange, 200, "text/html; charset=utf-8", html);
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
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.startsWith("q=")) {
                    String searchQuery = query.substring(2);
                    List<SearchResult> results = searchEngine.search(new SearchQuery(searchQuery));
                    String jsonResponse = generateSearchResultsJson(results, searchQuery);
                    sendResponse(exchange, 200, "application/json", jsonResponse);
                } else {
                    sendResponse(exchange, 400, "application/json", "{\"error\":\"Missing query parameter\"}");
                }
            } else {
                sendResponse(exchange, 405, "text/plain", "Method not allowed");
            }
        }
    }

    /**
     * Handler for suggestions API requests
     */
    private class SuggestionsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.startsWith("q=")) {
                    String searchQuery = query.substring(2);
                    List<String> suggestions = searchEngine.getSuggestions(searchQuery);
                    String jsonResponse = generateSuggestionsJson(suggestions);
                    sendResponse(exchange, 200, "application/json", jsonResponse);
                } else {
                    sendResponse(exchange, 400, "application/json", "{\"error\":\"Missing query parameter\"}");
                }
            } else {
                sendResponse(exchange, 405, "text/plain", "Method not allowed");
            }
        }
    }

    /**
     * Handler for custom sample pages
     */
    private class CustomPageHandler implements HttpHandler {
        private final String path;

        public CustomPageHandler(String path) {
            this.path = path;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String html = generateCustomPage(path);
                sendResponse(exchange, 200, "text/html; charset=utf-8", html);
            } else {
                sendResponse(exchange, 405, "text/plain", "Method not allowed");
            }
        }
    }


    /**
     * Handler for health check requests
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
     * Generate a beautiful, modern search page HTML
     */
    private String generateBeautifulSearchPage() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"en\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<title>🚀 Mini Search Engine</title>");
        html.append("<link href=\"https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap\" rel=\"stylesheet\">");
        html.append("<style>");
        html.append("* { margin: 0; padding: 0; box-sizing: border-box; }");
        html.append("body { font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; color: #333; }");
        html.append(".container { max-width: 1200px; margin: 0 auto; padding: 20px; }");
        html.append(".header { text-align: center; margin-bottom: 40px; }");
        html.append(".logo { font-size: 3.5rem; margin-bottom: 10px; }");
        html.append("h1 { color: white; font-size: 2.5rem; font-weight: 700; margin-bottom: 10px; }");
        html.append(".tagline { font-size: 1.2rem; color: rgba(255,255,255,0.9); font-weight: 300; margin-bottom: 20px; }");
        html.append(".search-container { background: rgba(255,255,255,0.95); backdrop-filter: blur(10px); border-radius: 20px; padding: 40px; box-shadow: 0 20px 40px rgba(0,0,0,0.1); margin-bottom: 30px; }");
        html.append(".search-box { display: flex; gap: 15px; margin-bottom: 30px; }");
        html.append(".input-container { position: relative; flex: 1; }");
        html.append("#searchInput { width: 100%; padding: 18px 25px; font-size: 18px; border: 2px solid #e1e5e9; border-radius: 15px; outline: none; transition: all 0.3s ease; background: white; padding-right: 50px; box-sizing: border-box; }");
        html.append("#searchInput:focus { border-color: #667eea; box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1); transform: translateY(-2px); }");
        html.append("#searchInput::placeholder { color: #9ca3af; }");
        html.append(".clear-btn { position: absolute; right: 15px; top: 50%; transform: translateY(-50%); background: #e5e7eb; color: #6b7280; border: none; width: 24px; height: 24px; border-radius: 50%; cursor: pointer; display: none; align-items: center; justify-content: center; font-size: 14px; font-weight: bold; transition: all 0.2s ease; z-index: 10; }");
        html.append(".clear-btn:hover { background: #d1d5db; color: #374151; transform: scale(1.1); }");
        html.append(".clear-btn.visible { display: flex; }");
        html.append(".search-btn { padding: 18px 30px; font-size: 18px; font-weight: 600; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 15px; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3); }");
        html.append(".search-btn:hover { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4); }");
        html.append(".search-btn:active { transform: translateY(0); }");
        html.append(".suggestions { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 20px; }");
        html.append(".suggestion-tag { padding: 8px 16px; background: rgba(102, 126, 234, 0.1); color: #667eea; border-radius: 20px; cursor: pointer; transition: all 0.3s ease; font-size: 14px; font-weight: 500; }");
        html.append(".suggestion-tag:hover { background: rgba(102, 126, 234, 0.2); transform: translateY(-1px); }");
        html.append(".results-container { background: rgba(255,255,255,0.95); backdrop-filter: blur(10px); border-radius: 20px; padding: 30px; box-shadow: 0 20px 40px rgba(0,0,0,0.1); }");
        html.append(".results-header { margin-bottom: 25px; padding-bottom: 20px; border-bottom: 2px solid #f1f5f9; }");
        html.append(".results-title { font-size: 1.5rem; font-weight: 600; color: #1f2937; margin-bottom: 5px; }");
        html.append(".results-count { color: #6b7280; font-size: 1rem; }");
        html.append(".result { padding: 25px; margin-bottom: 20px; border: 1px solid #e5e7eb; border-radius: 15px; transition: all 0.3s ease; background: white; }");
        html.append(".result:hover { border-color: #667eea; box-shadow: 0 8px 25px rgba(0,0,0,0.1); transform: translateY(-2px); }");
        html.append(".result-title { color: #1d4ed8; font-size: 1.3rem; text-decoration: none; font-weight: 600; display: block; margin-bottom: 8px; transition: color 0.3s ease; }");
        html.append(".result-title:hover { color: #1e40af; }");
        html.append(".result-url { color: #059669; font-size: 0.9rem; margin-bottom: 12px; font-family: 'SF Mono', Monaco, monospace; }");
        html.append(".result-snippet { color: #4b5563; line-height: 1.6; margin-bottom: 15px; }");
        html.append(".loading { text-align: center; padding: 40px; color: #6b7280; }");
        html.append(".loading::after { content: ''; display: inline-block; width: 20px; height: 20px; border: 2px solid #e5e7eb; border-top: 2px solid #667eea; border-radius: 50%; animation: spin 1s linear infinite; margin-left: 10px; }");
        html.append("@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }");
        html.append(".no-results { text-align: center; padding: 40px; color: #6b7280; }");
        html.append(".footer { text-align: center; margin-top: 40px; color: rgba(255,255,255,0.8); font-size: 0.9rem; }");
        html.append("@media (max-width: 768px) { .search-box { flex-direction: column; } .search-container, .results-container { padding: 20px; } .logo { font-size: 2.5rem; } .input-container { width: 100%; } }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"container\">");
        html.append("<div class=\"header\">");
        html.append("<div class=\"logo\">🚀</div>");
        html.append("<h1>Mini Search Engine</h1>");
        html.append("<p class=\"tagline\">Discover the power of intelligent search</p>");
        html.append("</div>");
        html.append("<div class=\"search-container\">");
        html.append("<div class=\"search-box\">");
        html.append("<div class=\"input-container\">");
        html.append("<input type=\"text\" id=\"searchInput\" placeholder=\"Search for anything...\" autocomplete=\"off\">");
        html.append("<button class=\"clear-btn\" id=\"clearBtn\" onclick=\"clearSearch()\" title=\"Clear search\">✕</button>");
        html.append("</div>");
        html.append("<button class=\"search-btn\" onclick=\"performSearch()\">🔍 Search</button>");
        html.append("</div>");
        html.append("<div class=\"suggestions\">");
        html.append("<span class=\"suggestion-tag\" onclick=\"searchFor('spring')\">spring</span>");
        html.append("<span class=\"suggestion-tag\" onclick=\"searchFor('python')\">python</span>");
        html.append("<span class=\"suggestion-tag\" onclick=\"searchFor('javascript')\">javascript</span>");
        html.append("<span class=\"suggestion-tag\" onclick=\"searchFor('elasticsearch')\">elasticsearch</span>");
        html.append("<span class=\"suggestion-tag\" onclick=\"searchFor('scrapy')\">scrapy</span>");
        html.append("</div>");
        html.append("</div>");
        html.append("<div id=\"results\" class=\"results-container\" style=\"display: none;\"></div>");
        html.append("</div>");
        html.append("<div class=\"footer\">");
        html.append("<p>Built with ❤️ using Java & Modern Web Technologies</p>");
        html.append("</div>");
        html.append("<script>");
        html.append("document.getElementById('searchInput').addEventListener('keypress', function(e) { if (e.key === 'Enter') performSearch(); });");
        html.append("document.getElementById('searchInput').addEventListener('input', function(e) { updateClearButton(); if (e.target.value.trim()) showResults(); });");
        html.append("function searchFor(query) { document.getElementById('searchInput').value = query; updateClearButton(); performSearch(); }");
        html.append("function showResults() { document.getElementById('results').style.display = 'block'; }");
        html.append("function updateClearButton() { var clearBtn = document.getElementById('clearBtn'); var searchInput = document.getElementById('searchInput'); if (searchInput.value.trim()) { clearBtn.classList.add('visible'); } else { clearBtn.classList.remove('visible'); } }");
        html.append("function clearSearch() { document.getElementById('searchInput').value = ''; document.getElementById('results').style.display = 'none'; updateClearButton(); }");
        html.append("function performSearch() {");
        html.append("var query = document.getElementById('searchInput').value.trim();");
        html.append("if (!query) return;");
        html.append("updateClearButton();");
        html.append("showResults();");
        html.append("document.getElementById('results').innerHTML = '<div class=\"loading\">Searching...</div>';");
        html.append("fetch('/api/search?q=' + encodeURIComponent(query))");
        html.append(".then(response => response.json())");
        html.append(".then(data => {");
        html.append("if (data.results && data.results.length > 0) {");
        html.append("displayResults(data.results, query);");
        html.append("} else {");
        html.append("document.getElementById('results').innerHTML = '<div class=\"no-results\"><h3>No results found</h3><p>Try different keywords or check your spelling.</p></div>';");
        html.append("}");
        html.append("})");
        html.append(".catch(error => {");
        html.append("document.getElementById('results').innerHTML = '<div class=\"no-results\"><h3>Search Error</h3><p>Something went wrong. Please try again.</p></div>';");
        html.append("});");
        html.append("}");
        html.append("function displayResults(results, query) {");
        html.append("var resultsDiv = document.getElementById('results');");
        html.append("var html = '<div class=\"results-header\">';");
        html.append("html += '<div class=\"results-title\">Search Results</div>';");
        html.append("html += '<div class=\"results-count\">Found ' + results.length + ' results for \"' + query + '\"</div>';");
        html.append("html += '</div>';");
        html.append("for (var i = 0; i < results.length; i++) {");
        html.append("var result = results[i];");
        html.append("html += '<div class=\"result\">';");
        html.append("html += '<a href=\"' + result.url + '\" class=\"result-title\" target=\"_blank\">' + (result.title || result.url) + '</a>';");
        html.append("html += '<div class=\"result-url\">' + result.url + '</div>';");
        html.append("html += '<div class=\"result-snippet\">' + (result.snippet || 'No snippet available') + '</div>';");
        html.append("html += '</div>';");
        html.append("}");
        html.append("resultsDiv.innerHTML = html;");
        html.append("}");
        html.append("updateClearButton();");
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
     * Generate a custom sample page based on the URL path
     */
    private String generateCustomPage(String path) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"en\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<title>");
        
        String title = "";
        String content = "";
        
        switch (path) {
            case "/spring":
                title = "Spring Framework - Java Web Development";
                content = generateSpringContent();
                break;
            case "/python":
                title = "Python Programming Language";
                content = generatePythonContent();
                break;
            case "/javascript":
                title = "JavaScript - Modern Web Development";
                content = generateJavaScriptContent();
                break;
            case "/elasticsearch":
                title = "Elasticsearch - Search Engine Technology";
                content = generateElasticsearchContent();
                break;
            case "/scrapy":
                title = "Scrapy - Web Scraping Framework";
                content = generateScrapyContent();
                break;
            default:
                title = "Page Not Found";
                content = "<h1>Page Not Found</h1><p>The requested page could not be found.</p>";
        }
        
        html.append(title);
        html.append("</title>");
        html.append("<style>");
        html.append("body { font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif; line-height: 1.6; margin: 0; padding: 0; background: #f8fafc; }");
        html.append(".container { max-width: 800px; margin: 0 auto; padding: 40px 20px; background: white; min-height: 100vh; box-shadow: 0 0 20px rgba(0,0,0,0.1); }");
        html.append(".header { text-align: center; margin-bottom: 40px; padding-bottom: 20px; border-bottom: 2px solid #e2e8f0; }");
        html.append(".back-btn { display: inline-block; padding: 10px 20px; background: #667eea; color: white; text-decoration: none; border-radius: 8px; margin-bottom: 20px; transition: background 0.3s ease; }");
        html.append(".back-btn:hover { background: #5a67d8; }");
        html.append("h1 { color: #1a202c; font-size: 2.5rem; margin-bottom: 10px; }");
        html.append(".subtitle { color: #4a5568; font-size: 1.2rem; margin-bottom: 20px; }");
        html.append(".content { color: #2d3748; font-size: 1.1rem; }");
        html.append(".content h2 { color: #2d3748; margin-top: 30px; margin-bottom: 15px; }");
        html.append(".content p { margin-bottom: 20px; }");
        html.append(".content ul { margin-bottom: 20px; }");
        html.append(".content li { margin-bottom: 10px; }");
        html.append(".code-block { background: #f7fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 20px; margin: 20px 0; font-family: 'SF Mono', Monaco, monospace; font-size: 0.9rem; overflow-x: auto; }");
        html.append(".highlight { background: #fef5e7; padding: 2px 6px; border-radius: 4px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"container\">");
        html.append("<div class=\"header\">");
        html.append("<a href=\"/\" class=\"back-btn\">← Back to Search</a>");
        html.append("<h1>").append(title).append("</h1>");
        html.append("</div>");
        html.append("<div class=\"content\">");
        html.append(content);
        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }
    
    private String generateSpringContent() {
        return "<h2>What is Spring Framework?</h2>" +
               "<p>Spring Framework is a comprehensive solution for building enterprise-grade Java applications. It provides a wide range of features that make Java development easier and more productive.</p>" +
               
               "<h2>Key Features</h2>" +
               "<ul>" +
               "<li><strong>Dependency Injection:</strong> Spring's IoC container manages object creation and dependencies</li>" +
               "<li><strong>Aspect-Oriented Programming:</strong> Cross-cutting concerns like logging and security</li>" +
               "<li><strong>Data Access:</strong> Simplified database operations with Spring Data</li>" +
               "<li><strong>Web Development:</strong> Spring MVC for building web applications</li>" +
               "</ul>" +
               
               "<h2>Spring Boot</h2>" +
               "<p>Spring Boot is an extension of Spring Framework that simplifies the setup and configuration of Spring applications. It provides auto-configuration and starter dependencies.</p>" +
               
               "<div class=\"code-block\">" +
               "@SpringBootApplication<br>" +
               "public class MyApplication {<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;public static void main(String[] args) {<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SpringApplication.run(MyApplication.class, args);<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;}<br>" +
               "}" +
               "</div>" +
               
               "<h2>Why Choose Spring?</h2>" +
               "<p>Spring Framework is the most popular Java framework because it offers:</p>" +
               "<ul>" +
               "<li>Comprehensive coverage of enterprise application concerns</li>" +
               "<li>Excellent documentation and community support</li>" +
               "<li>Integration with other popular frameworks and tools</li>" +
               "<li>Proven track record in production environments</li>" +
               "</ul>";
    }
    
    private String generatePythonContent() {
        return "<h2>Python Programming Language</h2>" +
               "<p>Python is a high-level, interpreted programming language known for its simplicity and readability. It's widely used in web development, data science, artificial intelligence, and automation.</p>" +
               
               "<h2>Key Characteristics</h2>" +
               "<ul>" +
               "<li><strong>Readable Syntax:</strong> Python code is easy to read and understand</li>" +
               "<li><strong>Dynamic Typing:</strong> Variables can change types during execution</li>" +
               "<li><strong>Rich Standard Library:</strong> Extensive built-in modules and packages</li>" +
               "<li><strong>Cross-Platform:</strong> Runs on Windows, macOS, and Linux</li>" +
               "</ul>" +
               
               "<h2>Web Development with Python</h2>" +
               "<p>Python offers several excellent web frameworks:</p>" +
               "<ul>" +
               "<li><strong>Django:</strong> Full-featured web framework with admin interface</li>" +
               "<li><strong>Flask:</strong> Lightweight and flexible micro-framework</li>" +
               "<li><strong>FastAPI:</strong> Modern, fast web framework for building APIs</li>" +
               "</ul>" +
               
               "<div class=\"code-block\">" +
               "from flask import Flask<br><br>" +
               "app = Flask(__name__)<br><br>" +
               "@app.route('/')<br>" +
               "def hello_world():<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;return 'Hello, World!'<br><br>" +
               "if __name__ == '__main__':<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;app.run()" +
               "</div>" +
               
               "<h2>Data Science & AI</h2>" +
               "<p>Python is the dominant language in data science and machine learning:</p>" +
               "<ul>" +
               "<li>NumPy for numerical computing</li>" +
               "<li>Pandas for data manipulation</li>" +
               "<li>Matplotlib for data visualization</li>" +
               "<li>Scikit-learn for machine learning</li>" +
               "<li>TensorFlow and PyTorch for deep learning</li>" +
               "</ul>";
    }
    
    private String generateJavaScriptContent() {
        return "<h2>JavaScript - The Language of the Web</h2>" +
               "<p>JavaScript is a versatile programming language that powers the modern web. It runs in browsers and has expanded to server-side development with Node.js.</p>" +
               
               "<h2>Core Features</h2>" +
               "<ul>" +
               "<li><strong>Event-Driven:</strong> Responds to user interactions and browser events</li>" +
               "<li><strong>Asynchronous:</strong> Non-blocking operations with callbacks and promises</li>" +
               "<li><strong>Object-Oriented:</strong> Prototype-based inheritance system</li>" +
               "<li><strong>Functional:</strong> First-class functions and closures</li>" +
               "</ul>" +
               
               "<h2>Frontend Frameworks</h2>" +
               "<p>Modern JavaScript development relies on powerful frameworks:</p>" +
               "<ul>" +
               "<li><strong>React:</strong> Component-based library by Facebook</li>" +
               "<li><strong>Vue.js:</strong> Progressive framework with gentle learning curve</li>" +
               "<li><strong>Angular:</strong> Full-featured framework by Google</li>" +
               "</ul>" +
               
               "<div class=\"code-block\">" +
               "// Modern JavaScript with ES6+<br>" +
               "const greeting = (name) => {<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;return `Hello, ${name}!`;<br>" +
               "};<br><br>" +
               "// Async/await for clean asynchronous code<br>" +
               "async function fetchData() {<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;try {<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;const response = await fetch('/api/data');<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return await response.json();<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;} catch (error) {<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;console.error('Error:', error);<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;}<br>" +
               "}" +
               "</div>" +
               
               "<h2>Node.js Ecosystem</h2>" +
               "<p>JavaScript on the server side offers:</p>" +
               "<ul>" +
               "<li>Express.js for building web applications</li>" +
               "<li>npm for package management</li>" +
               "<li>Real-time applications with Socket.io</li>" +
               "<li>Microservices architecture support</li>" +
               "</ul>";
    }
    
    private String generateElasticsearchContent() {
        return "<h2>Elasticsearch - Distributed Search Engine</h2>" +
               "<p>Elasticsearch is a distributed, RESTful search and analytics engine built on Apache Lucene. It's designed for horizontal scalability and real-time search capabilities.</p>" +
               
               "<h2>Core Concepts</h2>" +
               "<ul>" +
               "<li><strong>Index:</strong> Collection of documents with similar characteristics</li>" +
               "<li><strong>Document:</strong> JSON object stored in an index</li>" +
               "<li><strong>Shard:</strong> Subset of an index for horizontal scaling</li>" +
               "<li><strong>Node:</strong> Single server instance in the cluster</li>" +
               "</ul>" +
               
               "<h2>Key Features</h2>" +
               "<ul>" +
               "<li><strong>Full-Text Search:</strong> Advanced text search with relevance scoring</li>" +
               "<li><strong>Real-Time Analytics:</strong> Aggregations and data analysis</li>" +
               "<li><strong>Schema-Free:</strong> JSON documents with flexible structure</li>" +
               "<li><strong>RESTful API:</strong> HTTP-based interface for all operations</li>" +
               "</ul>" +
               
               "<div class=\"code-block\">" +
               "// Index a document<br>" +
               "PUT /products/_doc/1<br>" +
               "{<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;\"name\": \"Elasticsearch Guide\",<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;\"category\": \"books\",<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;\"price\": 29.99,<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;\"tags\": [\"search\", \"analytics\", \"database\"]<br>" +
               "}<br><br>" +
               "// Search for documents<br>" +
               "GET /products/_search<br>" +
               "{<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;\"query\": {<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"match\": {<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"name\": \"elasticsearch\"<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;}<br>" +
               "}" +
               "</div>" +
               
               "<h2>Use Cases</h2>" +
               "<ul>" +
               "<li>Website search and e-commerce</li>" +
               "<li>Log analysis and monitoring</li>" +
               "<li>Business intelligence and analytics</li>" +
               "<li>Security information and event management</li>" +
               "<li>Geospatial search and mapping</li>" +
               "</ul>";
    }
    
    private String generateScrapyContent() {
        return "<h2>Scrapy - Web Scraping Framework</h2>" +
               "<p>Scrapy is a fast, high-level web crawling and web scraping framework written in Python. It's used to extract structured data from websites and can be used for data mining, monitoring, and automated testing.</p>" +
               
               "<h2>Architecture</h2>" +
               "<ul>" +
               "<li><strong>Spider:</strong> Defines how to crawl and parse websites</li>" +
               "<li><strong>Engine:</strong> Coordinates between components</li>" +
               "<li><strong>Scheduler:</strong> Queues requests for processing</li>" +
               "<li><strong>Downloader:</strong> Fetches web pages and returns responses</li>" +
               "<li><strong>Item Pipeline:</strong> Processes extracted data</li>" +
               "</ul>" +
               
               "<h2>Key Features</h2>" +
               "<ul>" +
               "<li><strong>Built-in Support:</strong> CSS and XPath selectors</li>" +
               "<li><strong>Export Formats:</strong> JSON, CSV, XML output</li>" +
               "<li><strong>Robust:</strong> Handles encoding, redirects, and cookies</li>" +
               "<li><strong>Extensible:</strong> Middleware and pipeline support</li>" +
               "<li><strong>Performance:</strong> Asynchronous networking and processing</li>" +
               "</ul>" +
               
               "<div class=\"code-block\">" +
               "import scrapy<br><br>" +
               "class ExampleSpider(scrapy.Spider):<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;name = 'example'<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;start_urls = ['http://example.com']<br><br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;def parse(self, response):<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;for title in response.css('h1::text'):<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;yield {<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'title': title.get()<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br><br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;next_page = response.css('a::attr(href)').get()<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if next_page is not None:<br>" +
               "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;yield response.follow(next_page, self.parse)" +
               "</div>" +
               
               "<h2>Common Use Cases</h2>" +
               "<ul>" +
               "<li>E-commerce price monitoring</li>" +
               "<li>News aggregation and monitoring</li>" +
               "<li>Social media data collection</li>" +
               "<li>Research and academic data gathering</li>" +
               "<li>Competitive intelligence</li>" +
               "</ul>";
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
