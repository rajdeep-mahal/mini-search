package com.minisearch.crawler;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Configuration for the web crawler
 */
public class CrawlerConfig {
    private int maxPages = 100;
    private int delayMs = 1000; // 1 second between requests
    private int timeoutMs = 10000; // 10 second timeout
    private int maxDepth = 3;
    private int threadPoolSize = 5;
    private String userAgent = "MiniSearch-Engine/1.0";
    private List<String> allowedDomains;
    private List<String> excludedDomains;
    private List<Pattern> excludedPatterns;
    private boolean followRedirects = true;
    private boolean respectRobotsTxt = true;
    private int maxRetries = 3;
    
    // Default excluded file extensions
    private static final List<String> DEFAULT_EXCLUDED_EXTENSIONS = List.of(
        "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", 
        "zip", "rar", "exe", "dmg", "pkg", "jpg", "jpeg", 
        "png", "gif", "mp3", "mp4", "avi", "mov", "wav", 
        "flv", "swf", "css", "js", "xml", "rss", "atom"
    );
    
    public CrawlerConfig() {
        // Initialize default excluded patterns
        this.excludedPatterns = DEFAULT_EXCLUDED_EXTENSIONS.stream()
            .map(ext -> Pattern.compile(".*\\." + ext + "$", Pattern.CASE_INSENSITIVE))
            .toList();
    }
    
    // Getters and Setters
    public int getMaxPages() { return maxPages; }
    public void setMaxPages(int maxPages) { this.maxPages = maxPages; }
    
    public int getDelayMs() { return delayMs; }
    public void setDelayMs(int delayMs) { this.delayMs = delayMs; }
    
    public int getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; }
    
    public int getMaxDepth() { return maxDepth; }
    public void setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; }
    
    public int getThreadPoolSize() { return threadPoolSize; }
    public void setThreadPoolSize(int threadPoolSize) { this.threadPoolSize = threadPoolSize; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public List<String> getAllowedDomains() { return allowedDomains; }
    public void setAllowedDomains(List<String> allowedDomains) { this.allowedDomains = allowedDomains; }
    
    public List<String> getExcludedDomains() { return excludedDomains; }
    public void setExcludedDomains(List<String> excludedDomains) { this.excludedDomains = excludedDomains; }
    
    public List<Pattern> getExcludedPatterns() { return excludedPatterns; }
    public void setExcludedPatterns(List<Pattern> excludedPatterns) { this.excludedPatterns = excludedPatterns; }
    
    public boolean isFollowRedirects() { return followRedirects; }
    public void setFollowRedirects(boolean followRedirects) { this.followRedirects = followRedirects; }
    
    public boolean isRespectRobotsTxt() { return respectRobotsTxt; }
    public void setRespectRobotsTxt(boolean respectRobotsTxt) { this.respectRobotsTxt = respectRobotsTxt; }
    
    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
    
    /**
     * Builder pattern for easy configuration
     */
    public static class Builder {
        private final CrawlerConfig config = new CrawlerConfig();
        
        public Builder maxPages(int maxPages) {
            config.setMaxPages(maxPages);
            return this;
        }
        
        public Builder delayMs(int delayMs) {
            config.setDelayMs(delayMs);
            return this;
        }
        
        public Builder timeoutMs(int timeoutMs) {
            config.setTimeoutMs(timeoutMs);
            return this;
        }
        
        public Builder maxDepth(int maxDepth) {
            config.setMaxDepth(maxDepth);
            return this;
        }
        
        public Builder threadPoolSize(int threadPoolSize) {
            config.setThreadPoolSize(threadPoolSize);
            return this;
        }
        
        public Builder userAgent(String userAgent) {
            config.setUserAgent(userAgent);
            return this;
        }
        
        public Builder allowedDomains(List<String> allowedDomains) {
            config.setAllowedDomains(allowedDomains);
            return this;
        }
        
        public Builder excludedDomains(List<String> excludedDomains) {
            config.setExcludedDomains(excludedDomains);
            return this;
        }
        
        public Builder followRedirects(boolean followRedirects) {
            config.setFollowRedirects(followRedirects);
            return this;
        }
        
        public Builder respectRobotsTxt(boolean respectRobotsTxt) {
            config.setRespectRobotsTxt(respectRobotsTxt);
            return this;
        }
        
        public Builder maxRetries(int maxRetries) {
            config.setMaxRetries(maxRetries);
            return this;
        }
        
        public CrawlerConfig build() {
            return config;
        }
    }
    
    /**
     * Create a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Check if a domain is allowed
     */
    public boolean isDomainAllowed(String domain) {
        if (allowedDomains == null || allowedDomains.isEmpty()) {
            return true; // No restrictions
        }
        return allowedDomains.contains(domain);
    }
    
    /**
     * Check if a domain is excluded
     */
    public boolean isDomainExcluded(String domain) {
        if (excludedDomains == null || excludedDomains.isEmpty()) {
            return false; // No exclusions
        }
        return excludedDomains.contains(domain);
    }
    
    /**
     * Check if a URL matches excluded patterns
     */
    public boolean isUrlExcluded(String url) {
        if (url == null || excludedPatterns == null) {
            return false;
        }
        
        String lowerUrl = url.toLowerCase();
        return excludedPatterns.stream().anyMatch(pattern -> pattern.matcher(lowerUrl).matches());
    }
    
    @Override
    public String toString() {
        return "CrawlerConfig{" +
                "maxPages=" + maxPages +
                ", delayMs=" + delayMs +
                ", timeoutMs=" + timeoutMs +
                ", maxDepth=" + maxDepth +
                ", threadPoolSize=" + threadPoolSize +
                ", userAgent='" + userAgent + '\'' +
                ", followRedirects=" + followRedirects +
                ", respectRobotsTxt=" + respectRobotsTxt +
                ", maxRetries=" + maxRetries +
                '}';
    }
}
