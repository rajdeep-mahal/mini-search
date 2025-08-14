package com.minisearch.indexer;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Processes text for indexing by tokenizing, normalizing, and filtering
 * This is a crucial component for building effective search indexes
 */
public class TextProcessor {
    
    // Common stop words that don't add search value
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
        "has", "he", "in", "is", "it", "its", "of", "on", "that", "the",
        "to", "was", "will", "with", "the", "this", "but", "they", "have",
        "had", "what", "said", "each", "which", "she", "do", "how", "their",
        "if", "up", "out", "many", "then", "them", "these", "so", "some",
        "her", "would", "make", "like", "into", "him", "time", "two", "more",
        "go", "no", "way", "could", "my", "than", "first", "been", "call",
        "who", "its", "now", "find", "long", "down", "day", "did", "get",
        "come", "made", "may", "part"
    ));
    
    // Pattern to match words (letters, numbers, and some special characters)
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\w']+");
    
    // Minimum word length to be considered for indexing
    private static final int MIN_WORD_LENGTH = 2;
    
    /**
     * Process text content and return processed terms
     * @param text Raw text content
     * @return List of processed terms
     */
    public List<String> processText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> terms = new ArrayList<>();
        String[] words = text.toLowerCase().split("\\s+");
        
        for (String word : words) {
            String processed = processWord(word);
            if (processed != null) {
                terms.add(processed);
            }
        }
        
        return terms;
    }
    
    /**
     * Process a single word
     * @param word Raw word
     * @return Processed word or null if should be filtered out
     */
    private String processWord(String word) {
        // Remove punctuation and clean the word
        String cleaned = word.replaceAll("[^\\w']", "");
        
        // Check minimum length
        if (cleaned.length() < MIN_WORD_LENGTH) {
            return null;
        }
        
        // Check if it's a stop word
        if (STOP_WORDS.contains(cleaned)) {
            return null;
        }
        
        // Check if it's just numbers
        if (cleaned.matches("\\d+")) {
            return null;
        }
        
        return cleaned;
    }
    
    /**
     * Extract terms with their positions in the text
     * @param text Raw text content
     * @return Map of term -> list of positions
     */
    public Map<String, List<Integer>> extractTermsWithPositions(String text) {
        Map<String, List<Integer>> termPositions = new HashMap<>();
        
        if (text == null || text.trim().isEmpty()) {
            return termPositions;
        }
        
        String[] words = text.toLowerCase().split("\\s+");
        int position = 0;
        
        for (String word : words) {
            String processed = processWord(word);
            if (processed != null) {
                termPositions.computeIfAbsent(processed, k -> new ArrayList<>()).add(position);
            }
            position++;
        }
        
        return termPositions;
    }
    
    /**
     * Calculate term frequencies from text
     * @param text Raw text content
     * @return Map of term -> frequency count
     */
    public Map<String, Integer> calculateTermFrequencies(String text) {
        Map<String, Integer> frequencies = new HashMap<>();
        
        if (text == null || text.trim().isEmpty()) {
            return frequencies;
        }
        
        String[] words = text.toLowerCase().split("\\s+");
        
        for (String word : words) {
            String processed = processWord(word);
            if (processed != null) {
                frequencies.put(processed, frequencies.getOrDefault(processed, 0) + 1);
            }
        }
        
        return frequencies;
    }
    
    /**
     * Extract key phrases from text (consecutive terms)
     * @param text Raw text content
     * @param phraseLength Length of phrases to extract
     * @return List of key phrases
     */
    public List<String> extractKeyPhrases(String text, int phraseLength) {
        List<String> phrases = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty() || phraseLength < 2) {
            return phrases;
        }
        
        String[] words = text.toLowerCase().split("\\s+");
        
        for (int i = 0; i <= words.length - phraseLength; i++) {
            StringBuilder phrase = new StringBuilder();
            boolean validPhrase = true;
            
            for (int j = 0; j < phraseLength; j++) {
                String word = processWord(words[i + j]);
                if (word == null) {
                    validPhrase = false;
                    break;
                }
                
                if (j > 0) phrase.append(" ");
                phrase.append(word);
            }
            
            if (validPhrase) {
                phrases.add(phrase.toString());
            }
        }
        
        return phrases;
    }
    
    /**
     * Get the most frequent terms from text
     * @param text Raw text content
     * @param limit Maximum number of terms to return
     * @return List of most frequent terms
     */
    public List<String> getMostFrequentTerms(String text, int limit) {
        Map<String, Integer> frequencies = calculateTermFrequencies(text);
        
        return frequencies.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Check if a term is a stop word
     * @param term The term to check
     * @return true if it's a stop word, false otherwise
     */
    public boolean isStopWord(String term) {
        return STOP_WORDS.contains(term.toLowerCase());
    }
    
    /**
     * Get the total number of stop words
     * @return Number of stop words
     */
    public int getStopWordsCount() {
        return STOP_WORDS.size();
    }
    
    /**
     * Get all stop words (for debugging/analysis)
     * @return Set of all stop words
     */
    public Set<String> getAllStopWords() {
        return new HashSet<>(STOP_WORDS);
    }
}
