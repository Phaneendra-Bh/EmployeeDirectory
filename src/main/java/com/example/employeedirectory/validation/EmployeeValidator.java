package com.example.employeedirectory.validation;

import com.example.employeedirectory.model.Employee;
import com.example.employeedirectory.model.EmployeeNode;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Function;

/**
 * Service for validating employee data and organizational structure.
 */
public class EmployeeValidator {
    
    /**
     * Functional interface for salary validation rules.
     */
    @FunctionalInterface
    public interface SalaryValidationRule {
        ValidationResult validate(Employee manager, List<EmployeeNode> directReports);
    }
    
    /**
     * Functional interface for reporting structure validation rules.
     */
    @FunctionalInterface
    public interface ReportingValidationRule {
        ValidationResult validate(EmployeeNode employeeNode);
    }
    
    /**
     * Represents the result of a validation check.
     */
    public static class ValidationResult {
        private final boolean isValid;
        private final String message;
        private final double amount; // For salary differences, depth violations, etc.
        
        public ValidationResult(boolean isValid, String message, double amount) {
            this.isValid = isValid;
            this.message = message;
            this.amount = amount;
        }
        
        public boolean isValid() { return isValid; }
        public String getMessage() { return message; }
        public double getAmount() { return amount; }
    }
    
    /**
     * Validates that managers earn at least 20% more than their direct subordinates' average salary.
     */
    public static SalaryValidationRule createMinimumSalaryRule() {
        return (manager, directReports) -> {
            if (directReports.isEmpty()) {
                return new ValidationResult(true, "No direct reports to validate", 0.0);
            }
            
            double averageSubordinateSalary = calculateAverageSalary(directReports);
            double minRequiredSalary = averageSubordinateSalary * 1.20;
            double managerSalary = manager.getSalary();
            
            if (managerSalary >= minRequiredSalary) {
                return new ValidationResult(true, "Manager meets minimum salary requirement", 0.0);
            } else {
                double shortfall = minRequiredSalary - managerSalary;
                return new ValidationResult(false, 
                    "Manager is underpaid", shortfall);
            }
        };
    }
    
    /**
     * Validates that managers don't earn more than 50% more than their direct subordinates' average salary.
     */
    public static SalaryValidationRule createMaximumSalaryRule() {
        return (manager, directReports) -> {
            if (directReports.isEmpty()) {
                return new ValidationResult(true, "No direct reports to validate", 0.0);
            }
            
            double averageSubordinateSalary = calculateAverageSalary(directReports);
            double maxAllowedSalary = averageSubordinateSalary * 1.50;
            double managerSalary = manager.getSalary();
            
            if (managerSalary <= maxAllowedSalary) {
                return new ValidationResult(true, "Manager meets maximum salary requirement", 0.0);
            } else {
                double excess = managerSalary - maxAllowedSalary;
                return new ValidationResult(false, 
                    "Manager is overpaid", excess);
            }
        };
    }
    
    /**
     * Validates that employees don't have more than 4 managers between them and the CEO.
     */
    public static ReportingValidationRule createReportingDepthRule() {
        return (employeeNode) -> {
            int depth = employeeNode.getDepth();
            if (depth <= 4) {
                return new ValidationResult(true, "Acceptable reporting depth", 0.0);
            } else {
                int levelsTooDeep = depth - 4;
                return new ValidationResult(false, 
                    "Reporting line too deep", levelsTooDeep);
            }
        };
    }
    
    /**
     * Validates all salary requirements for managers.
     * @param employeeNodes all employee nodes in the tree
     * @return validation results
     */
    public static List<ValidationResult> validateAllManagerSalaries(List<EmployeeNode> employeeNodes) {
        List<ValidationResult> results = new java.util.ArrayList<>();
        
        SalaryValidationRule minRule = createMinimumSalaryRule();
        SalaryValidationRule maxRule = createMaximumSalaryRule();
        
        for (EmployeeNode node : employeeNodes) {
            if (!node.isLeaf()) { // Only check managers
                List<EmployeeNode> directReports = node.getChildren();
                if (!directReports.isEmpty()) {
                    Employee manager = node.getEmployee();
                    
                    // Check minimum salary requirement
                    ValidationResult minResult = minRule.validate(manager, directReports);
                    if (!minResult.isValid()) {
                        results.add(minResult);
                    }
                    
                    // Check maximum salary requirement
                    ValidationResult maxResult = maxRule.validate(manager, directReports);
                    if (!maxResult.isValid()) {
                        results.add(maxResult);
                    }
                }
            }
        }
        
        return results;
    }
    
    /**
     * Validates reporting structure depth for all employees.
     * @param employeeNodes all employee nodes in the tree
     * @return validation results
     */
    public static List<ValidationResult> validateAllReportingDepths(List<EmployeeNode> employeeNodes) {
        List<ValidationResult> results = new java.util.ArrayList<>();
        
        ReportingValidationRule depthRule = createReportingDepthRule();
        
        for (EmployeeNode node : employeeNodes) {
            ValidationResult result = depthRule.validate(node);
            if (!result.isValid()) {
                results.add(result);
            }
        }
        
        return results;
    }
    
    /**
     * Calculates the average salary of a list of employee nodes.
     * @param nodes the list of employee nodes
     * @return the average salary
     */
    private static double calculateAverageSalary(List<EmployeeNode> nodes) {
        if (nodes.isEmpty()) {
            return 0.0;
        }
        
        double totalSalary = 0.0;
        for (EmployeeNode node : nodes) {
            totalSalary += node.getEmployee().getSalary();
        }
        
        return totalSalary / nodes.size();
    }
} 