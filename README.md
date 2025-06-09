# Spring-Patrin

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/java-1.8-orange.svg)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.1.4-green.svg)](https://spring.io/projects/spring-boot)

A Spring Boot extension that enhances Spring MVC to support convention-based URL mapping for REST APIs by automatically generating request paths from controller class and method names.

## Overview

Spring-Patrin simplifies REST API development by automatically generating URL mappings based on your project's package structure and naming conventions. This eliminates the need for repetitive `@RequestMapping` annotations and produces cleaner, more consistent API endpoints.

### Key Features

- **Convention over Configuration**: Generate REST endpoints based on controller class names and package structure
- **Path Variables Support**: Automatically handle path variables with special naming patterns like `$variableName$`
- **HTTP Method Inference**: Determine HTTP methods based on method name prefixes (GET__, POST__, etc.)
- **Spring Boot Auto-configuration**: Seamless integration with Spring Boot applications
- **Configurable Conventions**: Customize prefixes and suffixes through application properties

## Getting Started

### Prerequisites

- Java 8+
- Maven
- Spring Boot 2.1+

### Installation

Add the following dependency to your pom.xml:

```xml
<dependency>
    <groupId>com.github.landyking</groupId>
    <artifactId>spring-patrin</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Configuration

Spring-Patrin uses Spring Boot's auto-configuration system. You can customize it through application properties:

```properties
# Configure the controller package prefix (default: controller.)
patrin.prefixPkg=controller.

# Configure the controller class suffix (default: Controller)
patrin.suffixClass=Controller
```

## Usage Examples

### Basic Controller

```java
package controller;

@RestController
public class TestController {
    // Maps to "/test" with HTTP GET
    public ResponseEntity index() {
        return ResponseEntity.ok("hello");
    }
    
    // Maps to "/test/lala/{test}" with HTTP GET
    public void lala__$test$(@PathVariable("test") String test) {
        System.out.println(test);
    }
    
    // Maps to "/test/lala" with HTTP GET
    public void lala() {
    }
}
```

### Nested Package Structure

```java
package controller.api.v1.management.private_.marketing.$merchantId$;

@RestController
public class ActivitiesController {
    // Maps to "/api/v1/management/private/marketing/{merchantId}/activities" with HTTP GET
    public void GET__(){
    }
    
    // Maps to "/api/v1/management/private/marketing/{merchantId}/activities" with HTTP POST
    public void POST__(){
    }
    
    // Maps to "/api/v1/management/private/marketing/{merchantId}/activities/{id}" with HTTP PUT
    public void PUT__$id$(){
    }
}
```

### Path Variable Naming Conventions

- `$variableName$` in package name or method name is converted to `{variableName}` in the URL path
- Package names ending with underscore (e.g., `private_`) will have the trailing underscore removed in the URL

### HTTP Method Conventions

Method names can be prefixed with the HTTP method followed by double underscores:
- `GET__`: HTTP GET
- `POST__`: HTTP POST
- `PUT__`: HTTP PUT
- `DELETE__`: HTTP DELETE
- ... and all other standard HTTP methods

## Project Structure

- **spring-patrin**: Core library with handler mappings and auto-configuration
- **spring-patrin-example**: Example Spring Boot application demonstrating usage

## How It Works

Spring-Patrin provides a custom `RequestMappingHandlerMapping` implementation that:

1. Processes controller class names and their package structure to generate base URL paths
2. Parses method names to extract additional path segments and HTTP methods
3. Registers these generated mappings with Spring MVC's handler mapping system
4. Integrates with Spring Boot via auto-configuration

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Inspired by convention-over-configuration patterns in web frameworks
- Built on top of Spring MVC and Spring Boot
