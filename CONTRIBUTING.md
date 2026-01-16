# Contributing to AI Agent

Thank you for your interest in contributing to AI Agent! This document provides guidelines for contributing to the project.

## Code of Conduct

By participating in this project, you agree to abide by our Code of Conduct. Please read [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) before contributing.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the issue list as you might find out that you don't need to create one. When you are creating a bug report, please include as many details as possible:

* Use a clear and descriptive title
* Describe the exact steps which reproduce the problem
* Provide specific examples to demonstrate the steps
* Describe the behavior you observed after following the steps
* Explain which behavior you expected to see instead and why
* Include screenshots if relevant
* Include your environment details (OS, Java version, etc.)

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

* Use a clear and descriptive title
* Provide a detailed description of the suggested enhancement
* Explain why this enhancement would be useful
* List some examples of how this enhancement would be used

### Pull Requests

1. Fork the repository and create your branch from `main`
2. If you've added code that should be tested, add tests
3. If you've changed APIs, update the documentation
4. Ensure the test suite passes
5. Make sure your code follows the existing code style
6. Issue that pull request!

## Development Setup

### Prerequisites

* Java 17 or 21
* Maven 3.9+
* PostgreSQL (for database features)

### Setting Up Development Environment

1. Clone your fork of the repository:
   ```bash
   git clone https://github.com/YOUR-USERNAME/ai-agent.git
   cd ai-agent
   ```

2. Install dependencies:
   ```bash
   ./mvnw clean install
   ```

3. Run tests:
   ```bash
   ./mvnw test
   ```

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Code Style Guidelines

* Follow Java naming conventions
* Use meaningful variable and method names
* Add comments for complex logic
* Keep methods short and focused
* Write unit tests for new features
* Use Lombok annotations appropriately
* Follow the existing project structure

## Testing

* Write unit tests for all new functionality
* Ensure all tests pass before submitting a PR
* Aim for high code coverage
* Test edge cases and error conditions

## Commit Messages

* Use the present tense ("Add feature" not "Added feature")
* Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
* Limit the first line to 72 characters or less
* Reference issues and pull requests liberally after the first line

## Documentation

* Update the README.md if you change functionality
* Add JavaDoc comments for public APIs
* Update configuration documentation if you add new properties
* Include code examples where appropriate

## Questions?

Feel free to open an issue with your question or reach out to the maintainers.

Thank you for contributing! 🎉
