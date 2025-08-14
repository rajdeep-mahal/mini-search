package com.minisearch.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Represents a web page in the search engine
 */
public class WebPage {
    private String url;
    private String title;
    private String content;
    private String htmlContent;
    private List<String> links;
    private Map<String, String> metadata;
    private LocalDateTime lastCrawled;
    private int pageRank;
    private String domain;
    private int contentLength;
    
    // Constructors
    public WebPage() {}
    
    public WebPage(String url) {
        this.url = url;
        this.lastCrawled = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { 
        this.content = content; 
        this.contentLength = content != null ? content.length() : 0;
    }
    
    public String getHtmlContent() { return htmlContent; }
    public void setHtmlContent(String htmlContent) { this.htmlContent = htmlContent; }
    
    public List<String> getLinks() { return links; }
    public void setLinks(List<String> links) { this.links = links; }
    
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
    
    public LocalDateTime getLastCrawled() { return lastCrawled; }
    public void setLastCrawled(LocalDateTime lastCrawled) { this.lastCrawled = lastCrawled; }
    
    public int getPageRank() { return pageRank; }
    public void setPageRank(int pageRank) { this.pageRank = pageRank; }
    
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    
    public int getContentLength() { return contentLength; }
    
    @Override
    public String toString() {
        return "WebPage{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", contentLength=" + contentLength +
                ", lastCrawled=" + lastCrawled +
                ", pageRank=" + pageRank +
                '}';
    }
}
