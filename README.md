# Employee Directory

A Java application that reads employee data from a CSV file and validates organizational structure using Maven with comprehensive logging support.

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/example/employeedirectory/
│           ├── Main.java
│           ├── CSVReader.java
│           ├── EmployeeTree.java
│           ├── model/
│           │   ├── Employee.java
│           │   └── EmployeeNode.java
│           ├── validation/
│           │   └── EmployeeValidator.java
│           └── service/
│               └── ValidationReportService.java
└── test/
    └── java/
```

## Architecture

The application follows a clean separation of concerns:

- **Model Layer**: `Employee` and `EmployeeNode` classes for data representation
- **Tree Layer**: `EmployeeTree` handles only tree structure and navigation
- **Validation Layer**: `EmployeeValidator` contains business logic using functional interfaces
- **Service Layer**: `ValidationReportService` handles reporting and display logic
- **Logging**: SLF4J + Logback for comprehensive logging with configurable levels

## Features

- Read employee data from CSV files
- Build hierarchical tree structure with bidirectional relationships
- Validate manager salary requirements (20% - 50% more than subordinates)
- Validate reporting structure depth (max 4 levels)
- Functional programming approach for validation rules
- Detailed validation reports with specific amounts and employee names/IDs
- Comprehensive logging framework
- Configurable log levels and output formatting

## CSV Format

The application expects CSV files with the following format:
```
Id,firstName,lastName,salary,managerId
123,Joe,Doe,60000,
124,Martin,Chekov,45000,123
125,Bob,Ronstad,47000,123
```

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Building the Project

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the application
mvn package
```

## Running the Application

### **Recommended: Using Maven Exec Plugin**
This automatically includes all dependencies and is the easiest way to run the application:

```bash
mvn exec:java -Dexec.mainClass="com.example.employeedirectory.Main" -Dexec.args="employees.csv"
```

### Alternative: Using Java with Full Classpath
If you prefer using `java` directly, you need to include all dependencies:

```bash
# Build classpath with dependencies
mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt

# Run with full classpath (Windows)
java -cp target/classes;@classpath.txt com.example.employeedirectory.Main employees.csv

# Run with full classpath (Linux/Mac)
java -cp target/classes:@classpath.txt com.example.employeedirectory.Main employees.csv
```



## Validation Rules

### Salary Validation
- Managers must earn at least 20% more than their direct subordinates' average salary
- Managers must not earn more than 50% more than their direct subordinates' average salary
- Validation messages include specific employee names and IDs

### Reporting Structure Validation
- No employee should have more than 4 managers between them and the CEO
- Validation messages include specific employee names and IDs

## Logging Configuration

The application uses SLF4J with Logback for logging:

- **Main Configuration**: `src/main/resources/logback.xml`
- **Test Configuration**: `src/test/resources/logback-test.xml`
- **Log Levels**: DEBUG, INFO, WARN, ERROR

## Functional Programming Features

The validation system uses functional interfaces for flexibility:
- `SalaryValidationRule`: Functional interface for salary validation
- `ReportingValidationRule`: Functional interface for reporting structure validation
- Validation rules can be easily extended or modified without changing core logic

## Example Usage

1. Create a CSV file with employee data (see `employees.csv` for an example)
2. Run the application with the CSV file path as an argument
3. The application will display:
   - Validation report with employee names and IDs
   - Detailed validation information for specific employees

## Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ValidationReportServiceTest
```

## Next Steps

This architecture can be extended with:
- Database integration
- Additional validation rules
- Custom validation rule injection
- Export functionality
- Web interface
- Performance monitoring