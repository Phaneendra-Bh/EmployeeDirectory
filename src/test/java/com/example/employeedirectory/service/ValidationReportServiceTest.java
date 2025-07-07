package com.example.employeedirectory.service;

import com.example.employeedirectory.model.Employee;
import com.example.employeedirectory.model.EmployeeNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationReportServiceTest {

    private ValidationReportService reportService;
    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        reportService = new ValidationReportService();
        
        // Set up logging capture
        logger = (Logger) LoggerFactory.getLogger(ValidationReportService.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        logger.setLevel(ch.qos.logback.classic.Level.DEBUG);
    }

    void clearOutput() {
        listAppender.list.clear();
    }

    String getOutput() {
        StringBuilder sb = new StringBuilder();
        for (ILoggingEvent event : listAppender.list) {
            sb.append(event.getFormattedMessage()).append("\n");
        }
        return sb.toString();
    }

    @Test
    void testGenerateValidationReport_NoViolations() {
        clearOutput();
        // Arrange
        Employee manager = new Employee("1", "John", "Manager", 60000.0, null);
        Employee subordinate = new Employee("2", "Alice", "Sub", 40000.0, "1");
        
        EmployeeNode managerNode = new EmployeeNode(manager);
        EmployeeNode subNode = new EmployeeNode(subordinate);
        managerNode.addChild(subNode);
        
        List<EmployeeNode> allNodes = Arrays.asList(managerNode, subNode);

        // Act
        reportService.generateValidationReport(allNodes);

        // Assert
        String output = getOutput();
        assertTrue(output.contains("Validation Report"));
        assertTrue(output.contains("Salary Validation Results:"));
        assertTrue(output.contains("Reporting Structure Validation:"));
        assertTrue(output.contains("All managers meet the salary requirements"));
        assertTrue(output.contains("All employees have acceptable reporting line lengths"));
    }

    @Test
    void testGenerateValidationReport_WithSalaryViolations() {
        clearOutput();
        // Arrange
        Employee manager = new Employee("1", "John", "Manager", 40000.0, null);
        Employee subordinate = new Employee("2", "Alice", "Sub", 40000.0, "1");
        
        EmployeeNode managerNode = new EmployeeNode(manager);
        EmployeeNode subNode = new EmployeeNode(subordinate);
        managerNode.addChild(subNode);
        
        List<EmployeeNode> allNodes = Arrays.asList(managerNode, subNode);

        // Act
        reportService.generateValidationReport(allNodes);

        // Assert
        String output = getOutput();
        if (!output.contains("❌ UNDERPAID: John Manager (ID: 1) is underpaid")) {
            System.out.println("Actual output:\n" + output);
        }
        assertTrue(output.contains("❌ UNDERPAID: John Manager (ID: 1) is underpaid"));
        assertTrue(output.contains("Shortfall: $"));
        assertTrue(output.contains("Underpaid managers: 1"));
        assertTrue(output.contains("Overpaid managers: 0"));
    }

    @Test
    void testGenerateValidationReport_WithDepthViolations() {
        clearOutput();
        // Arrange
        Employee employee = new Employee("1", "John", "Employee", 50000.0, null);
        EmployeeNode employeeNode = new EmployeeNode(employee);
        
        // Create a deep hierarchy to make this employee too deep
        EmployeeNode level1 = new EmployeeNode(new Employee("2", "Level1", "Manager", 60000.0, null));
        EmployeeNode level2 = new EmployeeNode(new Employee("3", "Level2", "Manager", 70000.0, null));
        EmployeeNode level3 = new EmployeeNode(new Employee("4", "Level3", "Manager", 80000.0, null));
        EmployeeNode level4 = new EmployeeNode(new Employee("5", "Level4", "Manager", 90000.0, null));
        EmployeeNode level5 = new EmployeeNode(new Employee("6", "Level5", "Manager", 100000.0, null));
        
        level1.addChild(level2);
        level2.addChild(level3);
        level3.addChild(level4);
        level4.addChild(level5);
        level5.addChild(employeeNode);
        
        List<EmployeeNode> allNodes = Arrays.asList(level1, level2, level3, level4, level5, employeeNode);

        // Act
        reportService.generateValidationReport(allNodes);

        // Assert
        String output = getOutput();
        assertTrue(output.contains("❌ TOO DEEP: John Employee (ID: 1) has reporting line too deep"));
        assertTrue(output.contains("Levels too deep: 1"));
        assertTrue(output.contains("Employees with too long reporting lines: 1"));
    }

    @Test
    void testDisplayEmployeeValidationDetails_ManagerWithViolations() {
        clearOutput();
        // Arrange
        Employee manager = new Employee("1", "John", "Manager", 40000.0, null);
        Employee subordinate = new Employee("2", "Alice", "Sub", 40000.0, "1");
        
        EmployeeNode managerNode = new EmployeeNode(manager);
        EmployeeNode subNode = new EmployeeNode(subordinate);
        managerNode.addChild(subNode);

        // Act
        reportService.generateValidationReport(Arrays.asList(managerNode, subNode));
        reportService.displayEmployeeValidationDetails(managerNode);

        // Assert
        String output = getOutput();
        if (!output.contains("Validation Details for John Manager (ID: 1):")) {
            System.out.println("Actual output:\n" + output);
        }
        assertTrue(output.contains("Validation Details for John Manager (ID: 1):"));
        assertTrue(output.contains("Manager Validation:"));
        assertTrue(output.contains("❌ Minimum salary violation: $"));
        assertTrue(output.contains("✅ Maximum salary requirement met"));
        assertTrue(output.contains("✅ Reporting depth acceptable"));
    }

    @Test
    void testDisplayEmployeeValidationDetails_NonManager() {
        clearOutput();
        // Arrange
        Employee employee = new Employee("1", "John", "Employee", 50000.0, null);
        EmployeeNode employeeNode = new EmployeeNode(employee);

        // Act
        reportService.generateValidationReport(Arrays.asList(employeeNode));
        reportService.displayEmployeeValidationDetails(employeeNode);

        // Assert
        String output = getOutput();
        assertTrue(output.contains("Validation Details for John Employee (ID: 1):"));
        assertTrue(output.contains("✅ Reporting depth acceptable"));
        // Should not show manager validation since this is not a manager
    }

    @Test
    void testDisplayEmployeeValidationDetails_ManagerWithOverpaidViolation() {
        clearOutput();
        // Arrange
        Employee manager = new Employee("1", "John", "Manager", 80000.0, null);
        Employee subordinate1 = new Employee("2", "Alice", "Sub", 40000.0, "1");
        Employee subordinate2 = new Employee("3", "Bob", "Sub", 50000.0, "1");
        
        EmployeeNode managerNode = new EmployeeNode(manager);
        EmployeeNode sub1Node = new EmployeeNode(subordinate1);
        EmployeeNode sub2Node = new EmployeeNode(subordinate2);
        
        managerNode.addChild(sub1Node);
        managerNode.addChild(sub2Node);

        // Act
        reportService.generateValidationReport(Arrays.asList(managerNode, sub1Node, sub2Node));
        reportService.displayEmployeeValidationDetails(managerNode);

        // Assert
        String output = getOutput();
        assertTrue(output.contains("Validation Details for John Manager (ID: 1):"));
        assertTrue(output.contains("Manager Validation:"));
        assertTrue(output.contains("✅ Minimum salary requirement met"));
        assertTrue(output.contains("❌ Maximum salary violation: $"));
        assertTrue(output.contains("✅ Reporting depth acceptable"));
    }

    @Test
    void testDisplayEmployeeValidationDetails_EmployeeWithDepthViolation() {
        clearOutput();
        // Arrange
        Employee employee = new Employee("1", "John", "Employee", 50000.0, null);
        EmployeeNode employeeNode = new EmployeeNode(employee);
        
        // Create a deep hierarchy to make this employee too deep
        EmployeeNode level1 = new EmployeeNode(new Employee("2", "Level1", "Manager", 60000.0, null));
        EmployeeNode level2 = new EmployeeNode(new Employee("3", "Level2", "Manager", 70000.0, null));
        EmployeeNode level3 = new EmployeeNode(new Employee("4", "Level3", "Manager", 80000.0, null));
        EmployeeNode level4 = new EmployeeNode(new Employee("5", "Level4", "Manager", 90000.0, null));
        EmployeeNode level5 = new EmployeeNode(new Employee("6", "Level5", "Manager", 100000.0, null));
        
        level1.addChild(level2);
        level2.addChild(level3);
        level3.addChild(level4);
        level4.addChild(level5);
        level5.addChild(employeeNode);

        // Act
        reportService.generateValidationReport(Arrays.asList(level1, level2, level3, level4, level5, employeeNode));
        reportService.displayEmployeeValidationDetails(employeeNode);

        // Assert
        String output = getOutput();
        assertTrue(output.contains("Validation Details for John Employee (ID: 1):"));
        assertTrue(output.contains("❌ Reporting depth violation: 1 levels too deep"));
    }

    @Test
    void testDisplayEmployeeValidationDetails_ManagerWithNoDirectReports() {
        clearOutput();
        // Arrange
        Employee manager = new Employee("1", "John", "Manager", 60000.0, null);
        EmployeeNode managerNode = new EmployeeNode(manager);

        // Act
        reportService.generateValidationReport(Arrays.asList(managerNode));
        reportService.displayEmployeeValidationDetails(managerNode);

        // Assert
        String output = getOutput();
        assertTrue(output.contains("Validation Details for John Manager (ID: 1):"));
        assertTrue(output.contains("✅ Reporting depth acceptable"));
        // Should not show manager validation since there are no direct reports
    }

    @Test
    void testGenerateValidationReport_EmptyList() {
        clearOutput();
        // Arrange
        List<EmployeeNode> emptyList = Collections.emptyList();

        // Act
        reportService.generateValidationReport(emptyList);

        // Assert
        String output = getOutput();
        assertTrue(output.contains("Validation Report"));
        assertTrue(output.contains("All managers meet the salary requirements"));
        assertTrue(output.contains("All employees have acceptable reporting line lengths"));
    }

    @Test
    void testGenerateValidationReport_MixedViolations() {
        clearOutput();
        // Arrange
        // Create a manager who is underpaid
        Employee manager1 = new Employee("1", "John", "Manager", 50000.0, null);
        Employee sub1 = new Employee("2", "Alice", "Sub", 40000.0, "1");
        EmployeeNode manager1Node = new EmployeeNode(manager1);
        EmployeeNode sub1Node = new EmployeeNode(sub1);
        manager1Node.addChild(sub1Node);
        
        // Create a manager who is overpaid
        Employee manager2 = new Employee("3", "Jane", "Manager", 80000.0, null);
        Employee sub2 = new Employee("4", "Bob", "Sub", 40000.0, "3");
        EmployeeNode manager2Node = new EmployeeNode(manager2);
        EmployeeNode sub2Node = new EmployeeNode(sub2);
        manager2Node.addChild(sub2Node);
        
        // Create a deep hierarchy for depth violation
        Employee deepEmployee = new Employee("5", "Deep", "Employee", 50000.0, null);
        EmployeeNode deepNode = new EmployeeNode(deepEmployee);
        EmployeeNode level1 = new EmployeeNode(new Employee("6", "Level1", "Manager", 60000.0, null));
        EmployeeNode level2 = new EmployeeNode(new Employee("7", "Level2", "Manager", 70000.0, null));
        EmployeeNode level3 = new EmployeeNode(new Employee("8", "Level3", "Manager", 80000.0, null));
        EmployeeNode level4 = new EmployeeNode(new Employee("9", "Level4", "Manager", 90000.0, null));
        EmployeeNode level5 = new EmployeeNode(new Employee("10", "Level5", "Manager", 100000.0, null));
        
        level1.addChild(level2);
        level2.addChild(level3);
        level3.addChild(level4);
        level4.addChild(level5);
        level5.addChild(deepNode);
        
        List<EmployeeNode> allNodes = Arrays.asList(
            manager1Node, sub1Node, manager2Node, sub2Node,
            level1, level2, level3, level4, level5, deepNode
        );

        // Act
        reportService.generateValidationReport(allNodes);

        // Assert
        String output = getOutput();
        assertTrue(output.contains("❌ UNDERPAID"));
        assertTrue(output.contains("❌ OVERPAID"));
        assertTrue(output.contains("❌ TOO DEEP"));
        assertTrue(output.contains("Underpaid managers: 4"));
        assertTrue(output.contains("Overpaid managers: 2"));
        assertTrue(output.contains("Employees with too long reporting lines: 1"));
    }

    @Test
    void testGenerateValidationReport_WithDebugMode() {
        clearOutput();
        // Arrange
        ValidationReportService debugReportService = new ValidationReportService();
        Employee manager = new Employee("1", "John", "Manager", 50000.0, null);
        Employee subordinate = new Employee("2", "Alice", "Sub", 40000.0, "1");
        
        EmployeeNode managerNode = new EmployeeNode(manager);
        EmployeeNode subNode = new EmployeeNode(subordinate);
        managerNode.addChild(subNode);
        
        List<EmployeeNode> allNodes = Arrays.asList(managerNode, subNode);

        // Act
        debugReportService.generateValidationReport(allNodes);

        // Assert
        String output = getOutput();
        assertTrue(output.contains("Running validation for 2 employee nodes"));
        assertTrue(output.contains("Validation completed"));
        // No violations in this data, so don't check for UNDERPAID or Employee ID debug lines
        assertTrue(output.contains("All managers meet the salary requirements"));
    }

    @Test
    void testGenerateValidationReport_WithoutDebugMode() {
        clearOutput();
        // Arrange
        ValidationReportService normalReportService = new ValidationReportService();
        Employee manager = new Employee("1", "John", "Manager", 50000.0, null);
        Employee subordinate = new Employee("2", "Alice", "Sub", 40000.0, "1");
        
        EmployeeNode managerNode = new EmployeeNode(manager);
        EmployeeNode subNode = new EmployeeNode(subordinate);
        managerNode.addChild(subNode);
        
        List<EmployeeNode> allNodes = Arrays.asList(managerNode, subNode);

        // Act
        normalReportService.generateValidationReport(allNodes);

        // Assert
        String output = getOutput();
        assertTrue(output.contains("Validation Report"));
        assertTrue(output.contains("Salary Validation Results:"));
        assertTrue(output.contains("Reporting Structure Validation:"));
    }
} 