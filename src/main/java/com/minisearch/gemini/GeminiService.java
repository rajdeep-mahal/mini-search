package com.minisearch.gemini;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for integrating with Google Gemini AI API
 * Provides dynamic content generation for search queries
 */
public class GeminiService {
    
    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    
    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public GeminiService(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Generate content for a search query using Gemini AI
     */
    public GeminiResponse generateContent(String query) {
        try {
            String prompt = buildPrompt(query);
            String requestBody = buildRequestBody(prompt);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_API_URL + "?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return parseResponse(response.body());
            } else {
                logger.error("Gemini API error: {} - {}", response.statusCode(), response.body());
                return new GeminiResponse("Error generating content", new ArrayList<>());
            }
            
        } catch (Exception e) {
            logger.error("Error calling Gemini API", e);
            return new GeminiResponse("Error: " + e.getMessage(), new ArrayList<>());
        }
    }
    
    private String buildPrompt(String query) {
        return String.format(
            "Provide a comprehensive response about '%s' including:\n" +
            "1. A detailed description (2-3 paragraphs)\n" +
            "2. Key facts and information\n" +
            "3. Related topics or categories\n" +
            "4. Any interesting trivia\n\n" +
            "Make the response informative and engaging for a search engine.",
            query
        );
    }
    
    private String buildRequestBody(String prompt) {
        return String.format(
            "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
            prompt.replace("\"", "\\\"")
        );
    }
    
    private GeminiResponse parseResponse(String responseBody) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.path("candidates");
            
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                
                if (parts.isArray() && parts.size() > 0) {
                    String text = parts.get(0).path("text").asText();
                    return new GeminiResponse(text, extractImages(text));
                }
            }
            
            return new GeminiResponse("No content generated", new ArrayList<>());
            
        } catch (Exception e) {
            logger.error("Error parsing Gemini response", e);
            return new GeminiResponse("Error parsing response", new ArrayList<>());
        }
    }
    
    private List<String> extractImages(String text) {
        // Simple image extraction - in a real implementation, 
        // you might want to ask Gemini specifically for image URLs
        List<String> images = new ArrayList<>();
        // For now, return empty list - you can enhance this later
        return images;
    }
    
    /**
     * Check if Gemini service is available
     */
    public boolean isAvailable() {
        try {
            String testResponse = generateContent("test").getText();
            return testResponse != null && !testResponse.startsWith("Error");
        } catch (Exception e) {
            return false;
        }
    }
}
