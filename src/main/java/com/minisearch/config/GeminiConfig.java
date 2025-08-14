package com.minisearch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration for Gemini API integration
 * Loads API key from environment variables or config file
 */
public class GeminiConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(GeminiConfig.class);
    private static final String CONFIG_FILE = "config/gemini.properties";
    private static final String ENV_API_KEY = "GEMINI_API_KEY";
    private static final String CONFIG_API_KEY = "gemini.api.key";
    
    private final String apiKey;
    private final boolean enabled;
    
    public GeminiConfig() {
        this.apiKey = loadApiKey();
        this.enabled = apiKey != null && !apiKey.trim().isEmpty();
        
        if (enabled) {
            logger.info("Gemini API integration enabled");
        } else {
            logger.warn("Gemini API integration disabled - no valid API key found");
        }
    }
    
    private String loadApiKey() {
        // First try environment variable
        String envKey = System.getenv(ENV_API_KEY);
        if (envKey != null && !envKey.trim().isEmpty()) {
            logger.info("Using Gemini API key from environment variable");
            return envKey.trim();
        }
        
        // Then try config file
        try {
            Properties props = new Properties();
            try (InputStream input = new FileInputStream(CONFIG_FILE)) {
                props.load(input);
                String configKey = props.getProperty(CONFIG_API_KEY);
                if (configKey != null && !configKey.trim().isEmpty()) {
                    logger.info("Using Gemini API key from config file");
                    return configKey.trim();
                }
            }
        } catch (IOException e) {
            logger.debug("Config file not found: {}", CONFIG_FILE);
        }
        
        // Finally, try hardcoded key (for development only)
        String hardcodedKey = "AIzaSyAes-EAgPiuLXmhjkgHqUhCkJQIhuTt9fE";
        if (hardcodedKey != null && !hardcodedKey.trim().isEmpty()) {
            logger.warn("Using hardcoded Gemini API key - not recommended for production");
            return hardcodedKey.trim();
        }
        
        return null;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Create a sample config file
     */
    public static void createSampleConfig() {
        try {
            Properties props = new Properties();
            props.setProperty(CONFIG_API_KEY, "YOUR_GEMINI_API_KEY_HERE");
            props.setProperty("gemini.model", "gemini-pro");
            props.setProperty("gemini.max_tokens", "1000");
            
            // Create config directory if it doesn't exist
            java.io.File configDir = new java.io.File("config");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            
            try (java.io.FileOutputStream out = new java.io.FileOutputStream(CONFIG_FILE)) {
                props.store(out, "Gemini API Configuration");
                logger.info("Sample config file created: {}", CONFIG_FILE);
            }
        } catch (IOException e) {
            logger.error("Error creating sample config file", e);
        }
    }
}
