package com.minisearch.dataset;

import com.minisearch.indexer.SimpleSearchIndexer;
import com.minisearch.model.WebPage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Processes datasets in various formats and converts them to searchable content
 */
public class DatasetProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(DatasetProcessor.class);
    
    private final SimpleSearchIndexer indexer;
    private final ObjectMapper objectMapper;
    private final Path datasetsPath;
    
    public DatasetProcessor(SimpleSearchIndexer indexer) {
        this.indexer = indexer;
        this.objectMapper = new ObjectMapper();
        this.datasetsPath = Paths.get("datasets");
    }
    
    /**
     * Process all datasets in the datasets folder
     */
    public void processAllDatasets() {
        logger.info("Starting dataset processing...");
        
        try {
            // Process uploaded datasets
            processUploadedDatasets();
            
            // Process sample datasets
            processSampleDatasets();
            
            // Process processed datasets
            processProcessedDatasets();
            
            logger.info("Dataset processing completed successfully!");
            
        } catch (Exception e) {
            logger.error("Error processing datasets", e);
        }
    }
    
    /**
     * Process datasets in the uploaded folder
     */
    public void processUploadedDatasets() {
        Path uploadedPath = datasetsPath.resolve("uploaded");
        if (!Files.exists(uploadedPath)) {
            logger.info("Uploaded datasets folder not found, skipping...");
            return;
        }
        
        logger.info("Processing uploaded datasets...");
        
        try {
            Files.walk(uploadedPath)
                .filter(Files::isRegularFile)
                .forEach(this::processFile);
                
            logger.info("Uploaded datasets processed successfully!");
            
        } catch (IOException e) {
            logger.error("Error processing uploaded datasets", e);
        }
    }
    
    /**
     * Process datasets in the samples folder
     */
    public void processSampleDatasets() {
        Path samplesPath = datasetsPath.resolve("samples");
        if (!Files.exists(samplesPath)) {
            logger.info("Sample datasets folder not found, skipping...");
            return;
        }
        
        logger.info("Processing sample datasets...");
        
        try {
            Files.walk(samplesPath)
                .filter(Files::isRegularFile)
                .forEach(this::processFile);
                
            logger.info("Sample datasets processed successfully!");
            
        } catch (IOException e) {
            logger.error("Error processing sample datasets", e);
        }
    }
    
    /**
     * Process datasets in the processed folder
     */
    public void processProcessedDatasets() {
        Path processedPath = datasetsPath.resolve("processed");
        if (!Files.exists(processedPath)) {
            logger.info("Processed datasets folder not found, skipping...");
            return;
        }
        
        logger.info("Processing processed datasets...");
        
        try {
            Files.walk(processedPath)
                .filter(Files::isRegularFile)
                .forEach(this::processFile);
                
            logger.info("Processed datasets processed successfully!");
            
        } catch (IOException e) {
            logger.error("Error processing processed datasets", e);
        }
    }
    
    /**
     * Process a single dataset file
     */
    private void processFile(Path filePath) {
        String fileName = filePath.getFileName().toString();
        String fileExtension = getFileExtension(fileName);
        
        logger.info("Processing file: {} ({} format)", fileName, fileExtension);
        
        try {
            List<WebPage> webPages = new ArrayList<>();
            
            switch (fileExtension.toLowerCase()) {
                case "csv":
                    webPages = processCsvFile(filePath);
                    break;
                case "json":
                    webPages = processJsonFile(filePath);
                    break;
                case "txt":
                    webPages = processTextFile(filePath);
                    break;
                default:
                    logger.warn("Unsupported file format: {}", fileExtension);
                    return;
            }
            
            // Index the processed pages
            for (WebPage page : webPages) {
                indexer.indexPage(page);
            }
            
            logger.info("Successfully processed {} pages from {}", webPages.size(), fileName);
            
        } catch (Exception e) {
            logger.error("Error processing file: {}", fileName, e);
        }
    }
    
    /**
     * Process CSV file
     */
    private List<WebPage> processCsvFile(Path filePath) throws IOException {
        List<WebPage> webPages = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line = reader.readLine(); // Skip header
            if (line == null) return webPages;
            
            String[] headers = line.split(",");
            int urlIndex = findColumnIndex(headers, "url");
            int titleIndex = findColumnIndex(headers, "title");
            int contentIndex = findColumnIndex(headers, "content");
            int domainIndex = findColumnIndex(headers, "domain");
            
            while ((line = reader.readLine()) != null) {
                String[] values = parseCsvLine(line);
                if (values.length >= Math.max(urlIndex, Math.max(titleIndex, contentIndex)) + 1) {
                    WebPage page = new WebPage(values[urlIndex]);
                    page.setTitle(values[titleIndex]);
                    page.setContent(values[contentIndex]);
                    if (domainIndex >= 0 && domainIndex < values.length) {
                        page.setDomain(values[domainIndex]);
                    }
                    webPages.add(page);
                }
            }
        }
        
        return webPages;
    }
    
    /**
     * Process JSON file
     */
    private List<WebPage> processJsonFile(Path filePath) throws IOException {
        String content = Files.readString(filePath);
        
        if (content.trim().startsWith("[")) {
            // Array of objects
            List<Map<String, Object>> data = objectMapper.readValue(content, new TypeReference<List<Map<String, Object>>>() {});
            return data.stream()
                .map(this::mapToWebPage)
                .collect(Collectors.toList());
        } else {
            // Single object
            Map<String, Object> data = objectMapper.readValue(content, Map.class);
            return Collections.singletonList(mapToWebPage(data));
        }
    }
    
    /**
     * Process text file
     */
    private List<WebPage> processTextFile(Path filePath) throws IOException {
        List<WebPage> webPages = new ArrayList<>();
        String content = Files.readString(filePath);
        
        // Simple parsing for text files with markdown-like structure
        String[] sections = content.split("## ");
        
        for (String section : sections) {
            if (section.trim().isEmpty()) continue;
            
            String[] lines = section.split("\n");
            if (lines.length < 3) continue;
            
            String title = lines[0].trim();
            String url = extractUrl(lines);
            String sectionContent = extractContent(lines);
            
            if (url != null && !title.isEmpty() && !sectionContent.isEmpty()) {
                WebPage page = new WebPage(url);
                page.setTitle(title);
                page.setContent(sectionContent);
                page.setDomain("example.com"); // Default domain for text files
                webPages.add(page);
            }
        }
        
        return webPages;
    }
    
    /**
     * Convert map data to WebPage
     */
    private WebPage mapToWebPage(Map<String, Object> data) {
        String url = (String) data.get("url");
        String title = (String) data.get("title");
        String content = (String) data.get("content");
        String domain = (String) data.getOrDefault("domain", "example.com");
        
        WebPage page = new WebPage(url);
        page.setTitle(title);
        page.setContent(content);
        page.setDomain(domain);
        
        return page;
    }
    
    /**
     * Helper methods
     */
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1) : "";
    }
    
    private int findColumnIndex(String[] headers, String columnName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }
    
    private String[] parseCsvLine(String line) {
        // Simple CSV parsing - handles quoted fields
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());
        
        return result.toArray(new String[0]);
    }
    
    private String extractUrl(String[] lines) {
        for (String line : lines) {
            if (line.startsWith("URL:")) {
                return line.substring(4).trim();
            }
        }
        return null;
    }
    
    private String extractContent(String[] lines) {
        StringBuilder content = new StringBuilder();
        boolean foundContent = false;
        
        for (String line : lines) {
            if (line.startsWith("Content:")) {
                foundContent = true;
                content.append(line.substring(8).trim());
            } else if (foundContent && !line.startsWith("URL:") && !line.startsWith("Title:")) {
                content.append(" ").append(line.trim());
            }
        }
        
        return content.toString().trim();
    }
    
    /**
     * Get processing statistics
     */
    public String getProcessingStats() {
        return String.format("Dataset processing completed. Index size: %d pages", indexer.getIndexSize());
    }
}
