package com.example.employeedirectory;

import com.example.employeedirectory.model.Employee;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads employee data from a CSV file.
 */
public class CSVReader {
    
    /**
     * Reads employee data from the specified CSV file.
     * @param filePath path to the CSV file
     * @return list of Employee objects
     * @throws IOException if there's an error reading the file
     */
    public List<Employee> readEmployees(String filePath) throws IOException {
        List<Employee> employees = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Skip header line
            String line = reader.readLine();
            if (line == null || !line.startsWith("Id,firstName,lastName,salary,managerId")) {
                throw new IOException("Invalid CSV format. Expected header: Id,firstName,lastName,salary,managerId");
            }
            
            // Read data lines
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Employee employee = parseEmployeeLine(line);
                    if (employee != null) {
                        employees.add(employee);
                    }
                }
            }
        }
        
        return employees;
    }
    
    /**
     * Parses a single line from the CSV file into an Employee object.
     * @param line the CSV line to parse
     * @return Employee object or null if parsing fails
     */
    private Employee parseEmployeeLine(String line) {
        try {
            // Split by comma and include empty strings
            String[] parts = line.split(",", -1);
            
            if (parts.length != 5) {
                System.err.println("Invalid line format: " + line + " (expected 5 parts, got " + parts.length + ")");
                return null;
            }
            
            String id = parts[0].trim();
            String firstName = parts[1].trim();
            String lastName = parts[2].trim();
            double salary = Double.parseDouble(parts[3].trim());
            String managerId = parts[4].trim();
            
            // Convert empty managerId to null
            if (managerId.isEmpty()) {
                managerId = null;
            }
            
            return new Employee(id, firstName, lastName, salary, managerId);
            
        } catch (NumberFormatException e) {
            System.err.println("Error parsing salary in line: " + line);
            return null;
        } catch (Exception e) {
            System.err.println("Error parsing line: " + line + " - " + e.getMessage());
            return null;
        }
    }
} 