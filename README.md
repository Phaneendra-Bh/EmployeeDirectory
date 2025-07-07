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
- Comprehensive logging framework with debug mode support
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
# Normal mode (validation report only)
mvn exec:java -Dexec.mainClass="com.example.employeedirectory.Main" -Dexec.args="employees.csv"

# Debug mode (all information including CSV reading, tree structure, etc.)
mvn exec:java -Dexec.mainClass="com.example.employeedirectory.Main" -Dexec.args="employees.csv --debug"
```

### Alternative: Using Java with Full Classpath
If you prefer using `java` directly, you need to include all dependencies:

```bash
# Build classpath with dependencies
mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt

# Run with full classpath (Windows)
java -cp target/classes;@classpath.txt com.example.employeedirectory.Main employees.csv --debug

# Run with full classpath (Linux/Mac)
java -cp target/classes:@classpath.txt com.example.employeedirectory.Main employees.csv --debug
```

## Debug Mode

The application supports a `--debug` flag that provides additional logging information:

### **Normal Mode Output:**
- Only validation report with employee names and IDs
- Clean, focused output

### **Debug Mode Output:**
- CSV file reading progress
- Employee data loading details
- Tree statistics and structure
- Tree operations examples
- Enhanced validation report with debug details
- Detailed employee validation information

### **Example Debug Output:**
```
Debug mode enabled
Reading employee data from: employees.csv
Successfully loaded 5 employees:
Employee{id='123', firstName='Joe', lastName='Doe', salary=60000.0, managerId='null'}
...

Tree Statistics:
Total employees: 5
Root nodes (top-level managers): 1

Employee Tree Structure:
========================
├─ Joe Doe (ID: 123, Salary: $60000.0)
  ├─ Martin Chekov (ID: 124, Salary: $45000.0)
...

Validation Report
=================
❌ UNDERPAID: Martin Chekov (ID: 124) is underpaid
   Shortfall: $15000.00
   DEBUG: Employee ID: 124
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
- **Debug Control**: Set via `--debug` flag or system property `debug.level`

## Functional Programming Features

The validation system uses functional interfaces for flexibility:
- `SalaryValidationRule`: Functional interface for salary validation
- `ReportingValidationRule`: Functional interface for reporting structure validation
- Validation rules can be easily extended or modified without changing core logic

## Example Usage

1. Create a CSV file with employee data (see `employees.csv` for an example)
2. Run the application with the CSV file path as an argument
3. Use `--debug` flag for detailed logging information
4. The application will display:
   - Validation report with employee names and IDs
   - Debug information (if `--debug` flag is used)
   - Tree structure and operations (debug mode only)
   - Detailed validation for specific employees

## Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ValidationReportServiceTest

# Run with debug output
mvn test -Ddebug.level=DEBUG
```

## Next Steps

This architecture can be extended with:
- Database integration
- Additional validation rules
- Custom validation rule injection
- Export functionality
- Web interface
- Additional logging configurations
- Performance monitoring