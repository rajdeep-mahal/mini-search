# Mini Search Engine

A mini search engine implementation in Java, inspired by Google's search functionality.

> 📚 **New!** Check out our **[DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md)** for a complete step-by-step explanation of how we're building this search engine from scratch!

## Features

- **Web Crawler**: Automatically discover and index web pages
- **Search Index**: Build an inverted index for fast searching
- **Search Engine**: Process queries and return relevant results
- **Ranking Algorithm**: Implement scoring for result relevance
- **Web Interface**: Simple search interface for users

## Project Structure

```
mini-search/
├── src/
│   ├── main/
│   │   ├── java/com/minisearch/
│   │   │   ├── Main.java                 # Application entry point
│   │   │   ├── crawler/                  # Web crawling components
│   │   │   ├── indexer/                  # Indexing components
│   │   │   ├── search/                   # Search engine components
│   │   │   └── web/                      # Web interface components
│   │   └── resources/
│   │       └── logback.xml               # Logging configuration
│   └── test/
│       └── java/com/minisearch/          # Test classes
├── pom.xml                               # Maven configuration
└── README.md                             # This file
```

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Building the Project

```bash
# Clone the repository
git clone <repository-url>
cd mini-search

# Build the project
mvn clean compile

# Run tests
mvn test

# Create executable JAR
mvn package

# Run the application
java -jar target/mini-search-engine-1.0.0.jar
```

## Dependencies

- **Apache HttpClient**: For web crawling
- **JSoup**: HTML parsing
- **Jackson**: JSON processing
- **SLF4J + Logback**: Logging framework
- **JUnit 5**: Testing framework

## Architecture

The search engine follows a modular architecture:

1. **Crawler Module**: Discovers and fetches web pages
2. **Indexer Module**: Processes pages and builds search index
3. **Search Module**: Handles queries and returns results
4. **Web Module**: Provides user interface

## Configuration

The application can be configured through:
- `logback.xml`: Logging configuration
- Environment variables: Database connections, crawler settings
- Configuration files: Search engine parameters

## Development Status

🚧 **Under Development** 🚧

This project is currently in active development. The following components are planned:

- [x] Project structure setup
- [x] Web crawler implementation
- [x] Indexer implementation
- [x] Search engine core
- [ ] Web interface
- [ ] Configuration management
- [x] Testing suite

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is open source and available under the MIT License.

## Contact

For questions or contributions, please open an issue on GitHub.
