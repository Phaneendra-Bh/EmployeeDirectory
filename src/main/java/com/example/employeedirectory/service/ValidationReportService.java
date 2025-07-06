package com.example.employeedirectory.service;

import com.example.employeedirectory.model.Employee;
import com.example.employeedirectory.model.EmployeeNode;
import com.example.employeedirectory.validation.EmployeeValidator;
import com.example.employeedirectory.validation.EmployeeValidator.ValidationResult;

import java.util.List;

/**
 * Service for generating and displaying validation reports.
 */
public class ValidationReportService {
    
    /**
     * Generates and displays a comprehensive validation report.
     * @param employeeNodes all employee nodes in the tree
     */
    public void generateValidationReport(List<EmployeeNode> employeeNodes) {
        System.out.println("Validation Report");
        System.out.println("=================");
        System.out.println();
        
        // Salary validation
        displaySalaryValidationResults(employeeNodes);
        
        // Reporting depth validation
        displayReportingDepthValidationResults(employeeNodes);
    }
    
    /**
     * Displays salary validation results.
     * @param employeeNodes all employee nodes in the tree
     */
    private void displaySalaryValidationResults(List<EmployeeNode> employeeNodes) {
        System.out.println("Salary Validation Results:");
        System.out.println("=========================");
        
        List<ValidationResult> salaryResults = EmployeeValidator.validateAllManagerSalaries(employeeNodes);
        
        int underpaidCount = 0;
        int overpaidCount = 0;
        
        for (ValidationResult result : salaryResults) {
            if (result.getMessage().contains("underpaid")) {
                underpaidCount++;
                System.out.println("❌ UNDERPAID: " + result.getMessage());
                System.out.println("   Shortfall: $" + String.format("%.2f", result.getAmount()));
                System.out.println();
            } else if (result.getMessage().contains("overpaid")) {
                overpaidCount++;
                System.out.println("❌ OVERPAID: " + result.getMessage());
                System.out.println("   Excess: $" + String.format("%.2f", result.getAmount()));
                System.out.println();
            }
        }
        
        System.out.println("Summary:");
        System.out.println("  Underpaid managers: " + underpaidCount);
        System.out.println("  Overpaid managers: " + overpaidCount);
        
        if (underpaidCount == 0 && overpaidCount == 0) {
            System.out.println("✅ All managers meet the salary requirements!");
        }
        System.out.println();
    }
    
    /**
     * Displays reporting depth validation results.
     * @param employeeNodes all employee nodes in the tree
     */
    private void displayReportingDepthValidationResults(List<EmployeeNode> employeeNodes) {
        System.out.println("Reporting Structure Validation:");
        System.out.println("==============================");
        
        List<ValidationResult> depthResults = EmployeeValidator.validateAllReportingDepths(employeeNodes);
        
        int tooDeepCount = 0;
        for (ValidationResult result : depthResults) {
            if (!result.isValid()) {
                tooDeepCount++;
                System.out.println("❌ TOO DEEP: " + result.getMessage());
                System.out.println("   Levels too deep: " + (int) result.getAmount());
                System.out.println();
            }
        }
        
        System.out.println("Summary:");
        System.out.println("  Employees with too long reporting lines: " + tooDeepCount);
        
        if (tooDeepCount == 0) {
            System.out.println("✅ All employees have acceptable reporting line lengths!");
        }
        System.out.println();
    }
    
    /**
     * Displays detailed validation results for a specific employee.
     * @param employeeNode the employee node to validate
     */
    public void displayEmployeeValidationDetails(EmployeeNode employeeNode) {
        Employee employee = employeeNode.getEmployee();
        System.out.println("Validation Details for " + employee.getFirstName() + " " + employee.getLastName() + " (ID: " + employee.getId() + "):");
        System.out.println("==================================================================");
        
        // Check if this is a manager
        if (!employeeNode.isLeaf()) {
            List<EmployeeNode> directReports = employeeNode.getChildren();
            if (!directReports.isEmpty()) {
                System.out.println("Manager Validation:");
                
                // Salary validation
                EmployeeValidator.SalaryValidationRule minRule = EmployeeValidator.createMinimumSalaryRule();
                EmployeeValidator.SalaryValidationRule maxRule = EmployeeValidator.createMaximumSalaryRule();
                
                ValidationResult minResult = minRule.validate(employee, directReports);
                ValidationResult maxResult = maxRule.validate(employee, directReports);
                
                if (!minResult.isValid()) {
                    System.out.println("  ❌ Minimum salary violation: $" + String.format("%.2f", minResult.getAmount()) + " shortfall");
                } else {
                    System.out.println("  ✅ Minimum salary requirement met");
                }
                
                if (!maxResult.isValid()) {
                    System.out.println("  ❌ Maximum salary violation: $" + String.format("%.2f", maxResult.getAmount()) + " excess");
                } else {
                    System.out.println("  ✅ Maximum salary requirement met");
                }
            }
        }
        
        // Reporting depth validation
        EmployeeValidator.ReportingValidationRule depthRule = EmployeeValidator.createReportingDepthRule();
        ValidationResult depthResult = depthRule.validate(employeeNode);
        
        if (!depthResult.isValid()) {
            System.out.println("  ❌ Reporting depth violation: " + (int) depthResult.getAmount() + " levels too deep");
        } else {
            System.out.println("  ✅ Reporting depth acceptable");
        }
        
        System.out.println();
    }
} 