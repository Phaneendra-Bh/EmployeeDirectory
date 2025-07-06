package com.example.employeedirectory;

import com.example.employeedirectory.model.Employee;
import com.example.employeedirectory.model.EmployeeNode;
import java.io.IOException;
import java.util.List;

/**
 * Main class for the Employee Directory application.
 */
public class Main {
    
    public static void main(String[] args) {
        // Check if file path is provided as command line argument
        if (args.length == 0) {
            System.err.println("Usage: java Main <csv-file-path>");
            System.err.println("Example: java Main employees.csv");
            System.exit(1);
        }
        
        String filePath = args[0];
        System.out.println("Reading employee data from: " + filePath);
        
        try {
            // Create CSV reader and read employee data
            CSVReader csvReader = new CSVReader();
            List<Employee> employees = csvReader.readEmployees(filePath);
            
            // Display the results
            System.out.println("Successfully loaded " + employees.size() + " employees:");
            System.out.println();
            
            for (Employee employee : employees) {
                System.out.println(employee);
            }
            
            System.out.println("\n" + "=".repeat(50) + "\n");
            
            // Build and display the employee tree
            EmployeeTree employeeTree = new EmployeeTree();
            employeeTree.buildTree(employees);
            
            System.out.println("Tree Statistics:");
            System.out.println("Total employees: " + employeeTree.getTotalEmployeeCount());
            System.out.println("Root nodes (top-level managers): " + employeeTree.getRootNodeCount());
            System.out.println();
            
            // Print the tree structure
            employeeTree.printTree();
            
            // Demonstrate some tree operations
            System.out.println("\n" + "=".repeat(50) + "\n");
            System.out.println("Tree Operations Examples:");
            System.out.println();
            
            // Example: Get direct reports of Joe Doe (ID: 123)
            List<EmployeeNode> joeDirectReports = employeeTree.getDirectReports("123");
            System.out.println("Direct reports of Joe Doe (ID: 123):");
            for (EmployeeNode report : joeDirectReports) {
                Employee emp = report.getEmployee();
                System.out.println("  - " + emp.getFirstName() + " " + emp.getLastName() + " (ID: " + emp.getId() + ")");
            }
            
            // Example: Get all subordinates of Joe Doe
            List<EmployeeNode> joeAllSubordinates = employeeTree.getAllSubordinates("123");
            System.out.println("\nAll subordinates of Joe Doe (ID: 123):");
            for (EmployeeNode subordinate : joeAllSubordinates) {
                Employee emp = subordinate.getEmployee();
                System.out.println("  - " + emp.getFirstName() + " " + emp.getLastName() + " (ID: " + emp.getId() + ")");
            }
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            System.exit(1);
        }
    }
} 