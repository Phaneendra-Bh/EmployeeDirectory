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
            System.err.println("Usage: java Main <csv-file-path> [--debug]");
            System.err.println("Example: java Main employees.csv");
            System.err.println("Example: java Main employees.csv --debug");
            System.exit(1);
        }
        
        String filePath = args[0];
        boolean debugMode = false;
        
        // Check for debug flag
        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                if ("--debug".equals(args[i])) {
                    debugMode = true;
                    // Set system property for logback configuration
                    System.setProperty("debug.level", "DEBUG");
                    logger.info("Debug mode enabled");
                    break;
                }
            }
        }
        
        logger.debug("Reading employee data from: {}", filePath);
        
        try {
            // Create CSV reader and read employee data
            CSVReader csvReader = new CSVReader();
            List<Employee> employees = csvReader.readEmployees(filePath);
            
            logger.debug("Successfully loaded {} employees:", employees.size());
            
            for (Employee employee : employees) {
                logger.debug("{}", employee);
            }
            
            logger.debug("=".repeat(50));
            
            // Build and display the employee tree
            EmployeeTree employeeTree = new EmployeeTree();
            employeeTree.buildTree(employees);
            
            logger.debug("Tree Statistics:");
            logger.debug("Total employees: {}", employeeTree.getTotalEmployeeCount());
            logger.debug("Root nodes (top-level managers): {}", employeeTree.getRootNodeCount());
            
            // Print the tree structure
            employeeTree.printTree();
            
            logger.debug("=".repeat(50));
            
            // Generate validation report using the new service
            ValidationReportService reportService = new ValidationReportService();
            reportService.generateValidationReport(employeeTree.getAllNodes());
            
            logger.debug("=".repeat(50));
            logger.debug("Tree Operations Examples:");
            
            // Example: Get direct reports of Joe Doe (ID: 123)
            List<EmployeeNode> joeDirectReports = employeeTree.getDirectReports("123");
            logger.debug("Direct reports of Joe Doe (ID: 123):");
            for (EmployeeNode report : joeDirectReports) {
                Employee emp = report.getEmployee();
                logger.debug("  - {} {} (ID: {})", emp.getFirstName(), emp.getLastName(), emp.getId());
            }
            
            // Example: Get all subordinates of Joe Doe
            List<EmployeeNode> joeAllSubordinates = employeeTree.getAllSubordinates("123");
            logger.debug("All subordinates of Joe Doe (ID: 123):");
            for (EmployeeNode subordinate : joeAllSubordinates) {
                Employee emp = subordinate.getEmployee();
                logger.debug("  - {} {} (ID: {})", emp.getFirstName(), emp.getLastName(), emp.getId());
            }
            
            // Example: Show detailed validation for a specific employee
            logger.debug("=".repeat(50));
            EmployeeNode martinNode = employeeTree.getNodeById("124");
            if (martinNode != null) {
                reportService.displayEmployeeValidationDetails(martinNode);
            }
            
        } catch (IOException e) {
            logger.error("Error reading file: {}", e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            System.exit(1);
        }
    }
} 