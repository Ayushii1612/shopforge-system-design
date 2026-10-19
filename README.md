# E-Commerce Platform

An interview-focused e-commerce platform built as a modular monolith with a pure Java domain core.

## Step 1: Architecture Baseline

- [Requirements](docs/REQUIREMENTS.md)
- [Architecture and diagrams](docs/ARCHITECTURE.md)
- [Decision log](docs/DECISIONS.md)
- Automated domain dependency rule: `DomainArchitectureTest`

## Build

Requires JDK 17+ and Maven 3.8+.

```bash
mvn test
```

The package markers define the intended module boundaries. Business modules are added incrementally in the build order from the project prompt.