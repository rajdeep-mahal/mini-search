# Gemini AI Integration for Mini Search Engine

This document explains how to use the Gemini AI integration to enhance your search engine with dynamic, AI-generated content.

## 🚀 What It Does

When someone searches for terms like "Dog", "Python programming", or "Machine learning", your search engine will:

1. **First**: Check local datasets for relevant results
2. **Then**: Call Google's Gemini AI API to generate fresh, comprehensive content
3. **Finally**: Combine both sources, with AI-generated content appearing at the top

## 🔑 Setup

### 1. API Key Configuration

Your Gemini API key is already configured in `config/gemini.properties`:
```properties
gemini.api.key=AIzaSyAes-EAgPiuLXmhjkgHqUhCkJQIhuTt9fE
```

### 2. Environment Variable (Alternative)

You can also set the API key as an environment variable:
```bash
export GEMINI_API_KEY="your_api_key_here"
```

## 🏃‍♂️ Running the Demo

### Quick Test
```bash
mvn compile exec:java -Dexec.mainClass="com.minisearch.GeminiIntegrationDemo"
```

### Expected Output
```
🔍 Searching for: 'Dog'
Found 1 results:
  1. AI-Generated Content: Dog - gemini.ai (Gemini AI)
     💡 AI-Generated content: Dogs are domesticated mammals that have been companions to humans for thousands of years...
```

## 🔧 How It Works

### 1. **HybridSearchEngine**
- Combines local search with Gemini AI results
- Automatically decides when to use Gemini (general knowledge queries)
- Falls back to local-only search if Gemini fails

### 2. **GeminiService**
- Makes HTTP calls to Google's Gemini API
- Handles API responses and error handling
- Supports timeouts and retries

### 3. **Smart Query Routing**
- **Use Gemini for**: General topics, animals, concepts, broad subjects
- **Skip Gemini for**: URLs, file paths, technical terms in local datasets

## 📊 Search Results Structure

Each Gemini-enhanced search returns:
- **Local Results**: From your existing datasets
- **AI-Generated Result**: At the top, marked with `(Gemini AI)` source
- **Rich Content**: Detailed descriptions, facts, and related information

## 🎯 Example Queries to Try

### Good for Gemini (General Knowledge):
- "Dog" → Comprehensive dog information
- "Space exploration" → Current space missions and history
- "Machine learning" → AI concepts and applications
- "Climate change" → Environmental science and facts

### Better for Local Datasets:
- "Java programming" → If you have programming tutorials
- "Web technologies" → If you have tech documentation
- Specific URLs or file paths

## ⚠️ Important Notes

### API Costs
- **Free Tier**: Limited requests per month
- **Paid Tier**: Pay per request after free limit
- **Rate Limits**: API has request frequency limits

### Performance
- **Local Search**: Instant results
- **Gemini API**: 200-500ms additional latency
- **Fallback**: If API fails, shows local results only

### Content Quality
- **Always Fresh**: No need to maintain static datasets
- **AI-Generated**: Content quality depends on Gemini's training
- **Factual**: Gemini provides generally accurate information

## 🛠️ Customization

### Modify Gemini Prompts
Edit `GeminiService.java` to change how queries are sent to Gemini:

```java
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
```

### Adjust Query Routing
Modify `shouldUseGemini()` in `HybridSearchEngine.java` to control when Gemini is used:

```java
private boolean shouldUseGemini(SearchQuery query) {
    String queryText = query.getQueryText().toLowerCase().trim();
    
    // Customize these conditions
    return queryText.length() > 2 && 
           !queryText.contains("http") && 
           !queryText.contains("file:") &&
           !queryText.contains("localhost");
}
```

## 🚨 Troubleshooting

### Common Issues

1. **"Gemini API is not enabled"**
   - Check your API key in `config/gemini.properties`
   - Verify billing is enabled in Google Cloud Console

2. **"Gemini API is not responding"**
   - Check internet connection
   - Verify API key is valid
   - Check Google Cloud Console for API status

3. **Rate limiting errors**
   - Add delays between requests
   - Implement request caching
   - Check your API quota

### Debug Mode
Enable detailed logging in `logback.xml`:
```xml
<logger name="com.minisearch.gemini" level="DEBUG"/>
```

## 🔮 Future Enhancements

### Image Support
- Ask Gemini for image URLs
- Display images in search results
- Cache images locally

### Caching
- Cache Gemini responses
- Reduce API calls
- Improve response times

### Advanced Prompts
- Domain-specific prompts
- Multi-language support
- Structured data extraction

## 📚 API Documentation

- [Google AI Studio](https://makersuite.google.com/app/apikey)
- [Gemini API Reference](https://ai.google.dev/api/gemini-api)
- [Rate Limits & Pricing](https://ai.google.dev/pricing)

## 🎉 Success!

Your mini search engine now has the power of Google's Gemini AI! Users will get rich, dynamic content for general queries while maintaining fast local search for specific content.

---

**Need Help?** Check the logs or modify the configuration files. The integration is designed to gracefully fall back to local-only search if anything goes wrong.
