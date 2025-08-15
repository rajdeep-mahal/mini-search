// Supabase configuration
const SUPABASE_URL = 'https://nvgzqnmpgdkiqysngxaj.supabase.co';
const SUPABASE_ANON_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im52Z3pxbm1wZ2RraXF5c25neGFqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTUyMTYwMzEsImV4cCI6MjA3MDc5MjAzMX0.yJCiTditdmjK7G0PGAWkAh9WNfm0-2jyURExMGIkrUs';

// Initialize Supabase client
const supabase = window.supabase.createClient(SUPABASE_URL, SUPABASE_ANON_KEY);

// DOM elements
const searchInput = document.getElementById('searchInput');
const clearBtn = document.getElementById('clearBtn');
const resultsDiv = document.getElementById('results');
const resultsContent = document.getElementById('resultsContent');
const loadingDiv = document.getElementById('loading');

// Event listeners
searchInput.addEventListener('input', function(e) {
    updateClearButton();
    if (e.target.value.trim()) {
        showResults();
    }
});

searchInput.addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        performSearch();
    }
});

// Functions
function searchFor(query) {
    searchInput.value = query;
    updateClearButton();
    performSearch();
}

function showResults() {
    resultsDiv.style.display = 'block';
}

function updateClearButton() {
    if (searchInput.value.trim()) {
        clearBtn.classList.add('visible');
    } else {
        clearBtn.classList.remove('visible');
    }
}

function clearSearch() {
    searchInput.value = '';
    resultsDiv.style.display = 'none';
    updateClearButton();
}

async function performSearch() {
    const query = searchInput.value.trim();
    if (!query) return;
    
    updateClearButton();
    
    // Show loading
    loadingDiv.style.display = 'block';
    resultsDiv.style.display = 'none';
    
    try {
        // Search in Supabase using full-text search
        const { data, error } = await supabase
            .from('search_pages')
            .select('*')
            .textSearch('content', query, {
                type: 'websearch',
                config: 'english'
            })
            .limit(10);
        
        if (error) {
            throw error;
        }
        
        // Display results
        displayResults(data, query);
        
    } catch (error) {
        console.error('Search error:', error);
        displayError('Search failed. Please try again.');
    } finally {
        loadingDiv.style.display = 'none';
    }
}

function displayResults(results, query) {
    if (!results || results.length === 0) {
        resultsContent.innerHTML = `
            <div class="result-item">
                <p>No results found for "${query}". Try different keywords.</p>
            </div>
        `;
        resultsDiv.style.display = 'block';
        return;
    }
    
    let html = '';
    results.forEach(result => {
        const snippet = generateSnippet(result.content, query);
        html += `
            <div class="result-item">
                <a href="${result.url}" class="result-title">${result.title}</a>
                <div class="result-url">${result.url}</div>
                <div class="result-snippet">${snippet}</div>
            </div>
        `;
    });
    
    resultsContent.innerHTML = html;
    resultsDiv.style.display = 'block';
}

function generateSnippet(content, query) {
    const maxLength = 200;
    const queryLower = query.toLowerCase();
    const contentLower = content.toLowerCase();
    
    // Find the first occurrence of the query
    const index = contentLower.indexOf(queryLower);
    
    if (index === -1) {
        // Query not found, return beginning of content
        return content.substring(0, maxLength) + (content.length > maxLength ? '...' : '');
    }
    
    // Start from the query position, but ensure we don't cut words
    let start = Math.max(0, index - 50);
    let end = Math.min(content.length, index + maxLength);
    
    // Adjust to word boundaries
    while (start > 0 && content[start] !== ' ') start++;
    while (end < content.length && content[end] !== ' ') end++;
    
    let snippet = content.substring(start, end);
    
    // Add ellipsis if needed
    if (start > 0) snippet = '...' + snippet;
    if (end < content.length) snippet = snippet + '...';
    
    return snippet;
}

function displayError(message) {
    resultsContent.innerHTML = `
        <div class="result-item">
            <p style="color: #dc2626;">${message}</p>
        </div>
    `;
    resultsDiv.style.display = 'block';
}

// Initialize clear button state
updateClearButton();

// Duplicate suggestions for seamless infinite scrolling
function setupInfiniteScroll() {
    const container = document.querySelector('.suggestions-container');
    if (container) {
        // Clone the suggestions and append them
        const clone = container.cloneNode(true);
        container.parentNode.appendChild(clone);
        
        // Set the container width to accommodate all suggestions
        const totalWidth = container.scrollWidth;
        container.style.width = totalWidth + 'px';
    }
}

// Call the function when the page loads
document.addEventListener('DOMContentLoaded', setupInfiniteScroll);
