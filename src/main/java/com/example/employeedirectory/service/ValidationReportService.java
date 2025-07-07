package com.example.employeedirectory.service;

import com.example.employeedirectory.model.Employee;
import com.example.employeedirectory.model.EmployeeNode;
import com.example.employeedirectory.validation.EmployeeValidator;
import com.example.employeedirectory.validation.EmployeeValidator.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service for generating and displaying validation reports.
 */
public class ValidationReportService {
    
    private static final Logger logger = LoggerFactory.getLogger(ValidationReportService.class);
    
    private List<ValidationResult> salaryResults;
    private List<ValidationResult> depthResults;
    private boolean validationExecuted = false;
    
    /**
     * Creates a ValidationReportService.
     */
    public ValidationReportService() {
    }
    
    /**
     * Generates and displays a comprehensive validation report.
     * @param employeeNodes all employee nodes in the tree
     */
    public void generateValidationReport(List<EmployeeNode> employeeNodes) {
        // Run validation once and store results
        if (!validationExecuted) {
            logger.debug("Running validation for {} employee nodes...", employeeNodes.size());
            salaryResults = EmployeeValidator.validateAllManagerSalaries(employeeNodes);
            depthResults = EmployeeValidator.validateAllReportingDepths(employeeNodes);
            validationExecuted = true;
            logger.debug("Validation completed. Found {} salary violations and {} depth violations.", 
                        salaryResults.size(), depthResults.size());
        }
        
        logger.info("Validation Report");
        logger.info("=================");
        
        // Salary validation
        displaySalaryValidationResults();
        
        // Reporting depth validation
        displayReportingDepthValidationResults();
    }
    
    /**
     * Displays salary validation results using stored results.
     */
    private void displaySalaryValidationResults() {
        logger.info("Salary Validation Results:");
        logger.info("=========================");
        
        int underpaidCount = 0;
        int overpaidCount = 0;
        
        for (ValidationResult result : salaryResults) {
            Employee employee = result.getEmployee();
            String employeeInfo = employee.getFirstName() + " " + employee.getLastName() + " (ID: " + employee.getId() + ")";
            
            if (result.getMessage().contains("underpaid")) {
                underpaidCount++;
                logger.info("❌ UNDERPAID: {} is underpaid", employeeInfo);
                logger.info("   Shortfall: ${}", String.format("%.2f", result.getAmount()));
                logger.debug("   DEBUG: Employee ID: {}", result.getEmployee().getId());
                logger.info("");
            } else if (result.getMessage().contains("overpaid")) {
                overpaidCount++;
                logger.info("❌ OVERPAID: {} is overpaid", employeeInfo);
                logger.info("   Excess: ${}", String.format("%.2f", result.getAmount()));
                logger.debug("   DEBUG: Employee ID: {}", result.getEmployee().getId());
                logger.info("");
            }
        }
        
        logger.info("Summary:");
        logger.info("  Underpaid managers: {}", underpaidCount);
        logger.info("  Overpaid managers: {}", overpaidCount);
        
        if (underpaidCount == 0 && overpaidCount == 0) {
            logger.info("✅ All managers meet the salary requirements!");
        }
        logger.info("");
    }
    
    /**
     * Displays reporting depth validation results using stored results.
     */
    private void displayReportingDepthValidationResults() {
        logger.info("Reporting Structure Validation:");
        logger.info("==============================");
        
        int tooDeepCount = 0;
        for (ValidationResult result : depthResults) {
            if (!result.isValid()) {
                Employee employee = result.getEmployee();
                String employeeInfo = employee.getFirstName() + " " + employee.getLastName() + " (ID: " + employee.getId() + ")";
                
                tooDeepCount++;
                logger.info("❌ TOO DEEP: {} has reporting line too deep", employeeInfo);
                logger.info("   Levels too deep: {}", (int) result.getAmount());
                logger.debug("   DEBUG: Employee ID: {}", result.getEmployee().getId());
                logger.info("");
            }
        }
        
        logger.info("Summary:");
        logger.info("  Employees with too long reporting lines: {}", tooDeepCount);
        
        if (tooDeepCount == 0) {
            logger.info("✅ All employees have acceptable reporting line lengths!");
        }
        logger.info("");
    }
    
    /**
     * Displays detailed validation results for a specific employee using stored results.
     * @param employeeNode the employee node to validate
     */
    public void displayEmployeeValidationDetails(EmployeeNode employeeNode) {
        // Ensure validation has been executed
        if (!validationExecuted) {
            throw new IllegalStateException("Validation must be executed before displaying individual details. Call generateValidationReport() first.");
        }
        
        Employee employee = employeeNode.getEmployee();
        logger.info("Validation Details for {} {} (ID: {}):", 
                   employee.getFirstName(), employee.getLastName(), employee.getId());
        logger.info("==================================================================");
        
        // Check if this is a manager
        if (!employeeNode.isLeaf()) {
            List<EmployeeNode> directReports = employeeNode.getChildren();
            if (!directReports.isEmpty()) {
                logger.info("Manager Validation:");
                
                // Find salary validation results for this specific employee
                boolean hasMinViolation = false;
                boolean hasMaxViolation = false;
                
                for (ValidationResult result : salaryResults) {
                    if (result.getEmployee().getId().equals(employee.getId())) {
                        if (result.getMessage().contains("underpaid")) {
                            hasMinViolation = true;
                            logger.info("  ❌ Minimum salary violation: ${} shortfall", String.format("%.2f", result.getAmount()));
                        } else if (result.getMessage().contains("overpaid")) {
                            hasMaxViolation = true;
                            logger.info("  ❌ Maximum salary violation: ${} excess", String.format("%.2f", result.getAmount()));
                        }
                    }
                }
                
                if (!hasMinViolation) {
                    logger.info("  ✅ Minimum salary requirement met");
                }
                if (!hasMaxViolation) {
                    logger.info("  ✅ Maximum salary requirement met");
                }
            }
        }
        
        // Find depth validation result for this specific employee
        boolean hasDepthViolation = false;
        for (ValidationResult result : depthResults) {
            if (result.getEmployee().getId().equals(employee.getId())) {
                hasDepthViolation = true;
                logger.info("  ❌ Reporting depth violation: {} levels too deep", (int) result.getAmount());
                break;
            }
        }
        
        if (!hasDepthViolation) {
            logger.info("  ✅ Reporting depth acceptable");
        }
        logger.info("");
    }
} 