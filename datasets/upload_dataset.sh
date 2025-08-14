#!/bin/bash

# Dataset Upload Script for Mini Search Engine
# This script helps you upload and manage your datasets

echo "🚀 Mini Search Engine - Dataset Manager"
echo "========================================"

# Check if datasets directory exists
if [ ! -d "datasets" ]; then
    echo "❌ Datasets directory not found. Please run this script from the project root."
    exit 1
fi

# Function to show current datasets
show_datasets() {
    echo ""
    echo "📁 Current Datasets:"
    echo "-------------------"
    
    if [ -d "datasets/uploaded" ]; then
        echo "📤 Uploaded Datasets:"
        if [ "$(ls -A datasets/uploaded 2>/dev/null)" ]; then
            ls -la datasets/uploaded/
        else
            echo "   (empty)"
        fi
    fi
    
    if [ -d "datasets/samples" ]; then
        echo "📚 Sample Datasets:"
        if [ "$(ls -A datasets/samples 2>/dev/null)" ]; then
            ls -la datasets/samples/
        else
            echo "   (empty)"
        fi
    fi
    
    if [ -d "datasets/processed" ]; then
        echo "⚙️  Processed Datasets:"
        if [ "$(ls -A datasets/processed 2>/dev/null)" ]; then
            ls -la datasets/processed/
        else
            echo "   (empty)"
        fi
    fi
}

# Function to upload a dataset
upload_dataset() {
    echo ""
    echo "📤 Upload Dataset"
    echo "----------------"
    
    if [ -z "$1" ]; then
        echo "Usage: $0 upload <file_path>"
        echo "Example: $0 upload ~/my_dataset.csv"
        return
    fi
    
    if [ ! -f "$1" ]; then
        echo "❌ File not found: $1"
        return
    fi
    
    filename=$(basename "$1")
    extension="${filename##*.}"
    
    # Check if file format is supported
    case $extension in
        csv|json|txt)
            echo "✅ Supported format: $extension"
            ;;
        *)
            echo "❌ Unsupported format: $extension"
            echo "Supported formats: CSV, JSON, TXT"
            return
            ;;
    esac
    
    # Copy file to uploaded folder
    cp "$1" "datasets/uploaded/"
    echo "✅ Dataset uploaded successfully: $filename"
    echo "📁 Location: datasets/uploaded/$filename"
}

# Function to process datasets
process_datasets() {
    echo ""
    echo "⚙️  Processing Datasets..."
    echo "------------------------"
    
    # Check if Java and Maven are available
    if ! command -v java &> /dev/null; then
        echo "❌ Java not found. Please install Java first."
        return
    fi
    
    if ! command -v mvn &> /dev/null; then
        echo "❌ Maven not found. Please install Maven first."
        return
    fi
    
    echo "🔨 Compiling project..."
    mvn clean compile
    
    if [ $? -eq 0 ]; then
        echo "✅ Compilation successful!"
        echo "🚀 Starting dataset processor..."
        echo "Press Ctrl+C to stop the processor"
        mvn exec:java -Dexec.mainClass="com.minisearch.DatasetProcessorRunner"
    else
        echo "❌ Compilation failed!"
    fi
}

# Function to show help
show_help() {
    echo ""
    echo "📖 Usage:"
    echo "--------"
    echo "  $0 show                    - Show current datasets"
    echo "  $0 upload <file_path>      - Upload a dataset file"
    echo "  $0 process                 - Process all datasets and start search engine"
    echo "  $0 help                    - Show this help message"
    echo ""
    echo "📋 Supported File Formats:"
    echo "-------------------------"
    echo "  CSV  - Comma-separated values with headers: url,title,content,domain"
    echo "  JSON - Array of objects with url, title, content, domain fields"
    echo "  TXT  - Text files with markdown-like structure"
    echo ""
    echo "🔧 Example CSV Format:"
    echo "url,title,content,domain"
    echo "https://example.com,Example Title,Example content...,example.com"
}

# Main script logic
case "$1" in
    "show")
        show_datasets
        ;;
    "upload")
        upload_dataset "$2"
        ;;
    "process")
        process_datasets
        ;;
    "help"|"--help"|"-h"|"")
        show_help
        ;;
    *)
        echo "❌ Unknown command: $1"
        echo "Use '$0 help' for usage information."
        exit 1
        ;;
esac
