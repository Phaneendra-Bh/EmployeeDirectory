package com.example.employeedirectory;

import com.example.employeedirectory.model.Employee;
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
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            System.exit(1);
        }
    }
} 