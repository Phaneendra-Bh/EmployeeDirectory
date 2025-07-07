package com.example.employeedirectory;

import com.example.employeedirectory.model.Employee;
import com.example.employeedirectory.model.EmployeeNode;
import com.example.employeedirectory.service.ValidationReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;

/**
 * Main class for the Employee Directory application.
 */
public class Main {
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        // Check if file path is provided as command line argument
        if (args.length == 0) {
            System.err.println("Usage: java Main <csv-file-path>");
            System.err.println("Example: java Main employees.csv");
            System.exit(1);
        }
        
        String filePath = args[0];
        
        logger.info("Reading employee data from: {}", filePath);
        
        try {
            // Create CSV reader and read employee data
            CSVReader csvReader = new CSVReader();
            List<Employee> employees = csvReader.readEmployees(filePath);
            
            logger.info("Successfully loaded {} employees", employees.size());
            
            // Build the employee tree
            EmployeeTree employeeTree = new EmployeeTree();
            employeeTree.buildTree(employees);
            
            // Generate validation report
            ValidationReportService reportService = new ValidationReportService();
            reportService.generateValidationReport(employeeTree.getAllNodes());
            
        } catch (IOException e) {
            logger.error("Error reading file: {}", e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            System.exit(1);
        }
    }
} 