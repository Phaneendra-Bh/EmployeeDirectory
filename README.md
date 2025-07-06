# Employee Directory

A Java application that reads employee data from a CSV file using Maven.

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/example/employeedirectory/
│           ├── Main.java
│           ├── CSVReader.java
│           └── model/
│               └── Employee.java
└── test/
    └── java/
```

## Features

- Read employee data from CSV files
- Parse employee records with ID, name, salary, and manager information
- Display loaded employee data

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

## Example Usage

1. Create a CSV file with employee data (see `employees.csv` for an example)
2. Run the application with the CSV file path as an argument
3. The application will display all loaded employee records

## Next Steps

This basic structure can be extended with:
- Database integration
- Employee search functionality
- Manager-employee relationship analysis
- Salary reporting features