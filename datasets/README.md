# Dataset Management for Mini Search Engine

This folder contains datasets that can be used with your Mini Search Engine.

## 📁 Folder Structure

```
datasets/
├── uploaded/          # Upload your custom datasets here
├── samples/           # Sample datasets provided with the project
├── processed/         # Processed datasets ready for indexing
└── README.md          # This file
```

## 🚀 How to Use Custom Datasets

### 1. **Upload Your Dataset**
Place your dataset files in the `uploaded/` folder. Supported formats:
- **CSV files** - Comma-separated values
- **JSON files** - Structured data
- **Text files** - Plain text documents
- **HTML files** - Web page content

### 2. **Dataset Format Requirements**
Your dataset should include these fields for best results:
```json
{
  "url": "https://example.com/page",
  "title": "Page Title",
  "content": "Main content text...",
  "domain": "example.com",
  "category": "optional-category"
}
```

### 3. **CSV Format Example**
```csv
url,title,content,domain,category
https://example.com/java,Java Programming,Java is a programming language...,example.com,programming
https://example.com/python,Python Tutorial,Python is versatile...,example.com,programming
```

### 4. **Processing Your Dataset**
The search engine will automatically:
- Parse your dataset files
- Extract text content
- Build search indexes
- Make content searchable

## 📊 Sample Datasets

The `samples/` folder contains example datasets to get you started:
- `programming_languages.csv` - Programming language information
- `web_technologies.json` - Web development technologies
- `sample_articles.txt` - Sample article content

## 🔧 Custom Dataset Integration

To use your custom dataset with the search engine:

1. **Upload files** to `datasets/uploaded/`
2. **Run the dataset processor**:
   ```bash
   mvn exec:java -Dexec.mainClass="com.minisearch.DatasetProcessor"
   ```
3. **Access via web interface** at http://localhost:8080

## 📝 Dataset Guidelines

- **File size**: Keep individual files under 10MB for best performance
- **Encoding**: Use UTF-8 encoding for international characters
- **Content quality**: Clean, well-formatted content works best
- **Regular updates**: Refresh datasets periodically for current information

## 🆘 Need Help?

If you encounter issues with your dataset:
1. Check the file format matches the examples
2. Verify encoding is UTF-8
3. Ensure content is properly formatted
4. Check the logs for error messages
