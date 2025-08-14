package com.minisearch.gemini;

import java.util.List;

/**
 * Response object for Gemini API calls
 */
public class GeminiResponse {
    private final String text;
    private final List<String> images;
    private final long timestamp;
    
    public GeminiResponse(String text, List<String> images) {
        this.text = text;
        this.images = images;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getText() {
        return text;
    }
    
    public List<String> getImages() {
        return images;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public boolean hasImages() {
        return images != null && !images.isEmpty();
    }
    
    public boolean isError() {
        return text != null && text.startsWith("Error");
    }
    
    @Override
    public String toString() {
        return "GeminiResponse{" +
                "text='" + (text != null ? text.substring(0, Math.min(100, text.length())) + "..." : "null") + '\'' +
                ", images=" + images +
                ", timestamp=" + timestamp +
                '}';
    }
}
