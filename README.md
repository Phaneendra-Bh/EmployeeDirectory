# Employee Directory

A Java application that reads employee data from a CSV file and validates organizational structure using Maven.

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

## Features

- Read employee data from CSV files
- Build hierarchical tree structure with bidirectional relationships
- Validate manager salary requirements (20% - 50% more than subordinates)
- Validate reporting structure depth (max 4 levels)
- Functional programming approach for validation rules
- Detailed validation reports with specific amounts

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

# Package the application
mvn package
```

## Running the Application

After building the project, you can run it in several ways:

### Option 1: Using Maven Exec Plugin
```bash
mvn compile exec:java -Dexec.args="employees.csv"
```

### Option 2: Using Java directly (after compilation)
```bash
mvn compile
java -cp target/classes com.example.employeedirectory.Main employees.csv
```

### Option 3: Using the JAR file
```bash
mvn package
java -jar target/java-project-1.0.0.jar employees.csv
```

## Validation Rules

### Salary Validation
- Managers must earn at least 20% more than their direct subordinates' average salary
- Managers must not earn more than 50% more than their direct subordinates' average salary

### Reporting Structure Validation
- No employee should have more than 4 managers between them and the CEO

## Functional Programming Features

The validation system uses functional interfaces for flexibility:
- `SalaryValidationRule`: Functional interface for salary validation
- `ReportingValidationRule`: Functional interface for reporting structure validation
- Validation rules can be easily extended or modified without changing core logic

## Example Usage

1. Create a CSV file with employee data (see `employees.csv` for an example)
2. Run the application with the CSV file path as an argument
3. The application will display:
   - Loaded employee records
   - Tree structure
   - Validation results with specific amounts
   - Detailed validation for specific employees

## Next Steps

This architecture can be extended with:
- Database integration
- Additional validation rules
- Custom validation rule injection
- Export functionality
- Web interface