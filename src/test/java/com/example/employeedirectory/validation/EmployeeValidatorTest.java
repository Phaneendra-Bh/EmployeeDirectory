package com.example.employeedirectory.validation;

import com.example.employeedirectory.model.Employee;
import com.example.employeedirectory.model.EmployeeNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeValidatorTest {

    private EmployeeValidator.SalaryValidationRule minSalaryRule;
    private EmployeeValidator.SalaryValidationRule maxSalaryRule;
    private EmployeeValidator.ReportingValidationRule depthRule;

    @BeforeEach
    void setUp() {
        minSalaryRule = EmployeeValidator.createMinimumSalaryRule();
        maxSalaryRule = EmployeeValidator.createMaximumSalaryRule();
        depthRule = EmployeeValidator.createReportingDepthRule();
    }

    @Test
    void testMinimumSalaryRule_ManagerMeetsRequirement() {
        // Arrange
        Employee manager = new Employee("1", "John", "Manager", 60000.0, null);
        Employee subordinate1 = new Employee("2", "Alice", "Sub", 40000.0, "1");
        Employee subordinate2 = new Employee("3", "Bob", "Sub", 50000.0, "1");
        
        List<EmployeeNode> directReports = Arrays.asList(
            new EmployeeNode(subordinate1),
            new EmployeeNode(subordinate2)
        );

        // Act
        EmployeeValidator.ValidationResult result = minSalaryRule.validate(manager, directReports);

        // Assert
        assertTrue(result.isValid());
        assertEquals("Manager meets minimum salary requirement", result.getMessage());
        assertEquals(0.0, result.getAmount(), 0.01);
    }

    @Test
    void testMinimumSalaryRule_ManagerUnderpaid() {
        // Arrange
        Employee manager = new Employee("1", "John", "Manager", 50000.0, null);
        Employee subordinate1 = new Employee("2", "Alice", "Sub", 40000.0, "1");
        Employee subordinate2 = new Employee("3", "Bob", "Sub", 50000.0, "1");
        
        List<EmployeeNode> directReports = Arrays.asList(
            new EmployeeNode(subordinate1),
            new EmployeeNode(subordinate2)
        );

        // Act
        EmployeeValidator.ValidationResult result = minSalaryRule.validate(manager, directReports);

        // Assert
        assertFalse(result.isValid());
        assertEquals("Manager is underpaid", result.getMessage());
        assertEquals(4000.0, result.getAmount(), 0.01); // 54000 - 50000 = 4000
    }

    @Test
    void testMinimumSalaryRule_NoDirectReports() {
        // Arrange
        Employee manager = new Employee("1", "John", "Manager", 60000.0, null);
        List<EmployeeNode> directReports = Collections.emptyList();

        // Act
        EmployeeValidator.ValidationResult result = minSalaryRule.validate(manager, directReports);

        // Assert
        assertTrue(result.isValid());
        assertEquals("No direct reports to validate", result.getMessage());
        assertEquals(0.0, result.getAmount(), 0.01);
    }

    @Test
    void testMaximumSalaryRule_ManagerMeetsRequirement() {
        // Arrange
        Employee manager = new Employee("1", "John", "Manager", 60000.0, null);
        Employee subordinate1 = new Employee("2", "Alice", "Sub", 40000.0, "1");
        Employee subordinate2 = new Employee("3", "Bob", "Sub", 50000.0, "1");
        
        List<EmployeeNode> directReports = Arrays.asList(
            new EmployeeNode(subordinate1),
            new EmployeeNode(subordinate2)
        );

        // Act
        EmployeeValidator.ValidationResult result = maxSalaryRule.validate(manager, directReports);

        // Assert
        assertTrue(result.isValid());
        assertEquals("Manager meets maximum salary requirement", result.getMessage());
        assertEquals(0.0, result.getAmount(), 0.01);
    }

    @Test
    void testMaximumSalaryRule_ManagerOverpaid() {
        // Arrange
        Employee manager = new Employee("1", "John", "Manager", 80000.0, null);
        Employee subordinate1 = new Employee("2", "Alice", "Sub", 40000.0, "1");
        Employee subordinate2 = new Employee("3", "Bob", "Sub", 50000.0, "1");
        
        List<EmployeeNode> directReports = Arrays.asList(
            new EmployeeNode(subordinate1),
            new EmployeeNode(subordinate2)
        );

        // Act
        EmployeeValidator.ValidationResult result = maxSalaryRule.validate(manager, directReports);

        // Assert
        assertFalse(result.isValid());
        assertEquals("Manager is overpaid", result.getMessage());
        assertEquals(12500.0, result.getAmount(), 0.01); // 80000 - 67500 = 12500
    }

    @Test
    void testMaximumSalaryRule_NoDirectReports() {
        // Arrange
        Employee manager = new Employee("1", "John", "Manager", 60000.0, null);
        List<EmployeeNode> directReports = Collections.emptyList();

        // Act
        EmployeeValidator.ValidationResult result = maxSalaryRule.validate(manager, directReports);

        // Assert
        assertTrue(result.isValid());
        assertEquals("No direct reports to validate", result.getMessage());
        assertEquals(0.0, result.getAmount(), 0.01);
    }

    @Test
    void testReportingDepthRule_AcceptableDepth() {
        // Arrange
        Employee employee = new Employee("1", "John", "Employee", 50000.0, null);
        EmployeeNode node = new EmployeeNode(employee);
        // Set up a simple hierarchy to control depth
        EmployeeNode parent = new EmployeeNode(new Employee("2", "Parent", "Manager", 60000.0, null));
        parent.addChild(node);

        // Act
        EmployeeValidator.ValidationResult result = depthRule.validate(node);

        // Assert
        assertTrue(result.isValid());
        assertEquals("Acceptable reporting depth", result.getMessage());
        assertEquals(0.0, result.getAmount(), 0.01);
    }

    @Test
    void testReportingDepthRule_TooDeep() {
        // Arrange
        Employee employee = new Employee("1", "John", "Employee", 50000.0, null);
        EmployeeNode node = new EmployeeNode(employee);
        
        // Create a deep hierarchy (5 levels deep)
        EmployeeNode level1 = new EmployeeNode(new Employee("2", "Level1", "Manager", 60000.0, null));
        EmployeeNode level2 = new EmployeeNode(new Employee("3", "Level2", "Manager", 70000.0, null));
        EmployeeNode level3 = new EmployeeNode(new Employee("4", "Level3", "Manager", 80000.0, null));
        EmployeeNode level4 = new EmployeeNode(new Employee("5", "Level4", "Manager", 90000.0, null));
        EmployeeNode level5 = new EmployeeNode(new Employee("6", "Level5", "Manager", 100000.0, null));
        
        level1.addChild(level2);
        level2.addChild(level3);
        level3.addChild(level4);
        level4.addChild(level5);
        level5.addChild(node);

        // Act
        EmployeeValidator.ValidationResult result = depthRule.validate(node);

        // Assert
        assertFalse(result.isValid());
        assertEquals("Reporting line too deep", result.getMessage());
        assertEquals(1.0, result.getAmount(), 0.01); // 5 levels - 4 = 1 level too deep
    }

    @Test
    void testValidateAllManagerSalaries_NoViolations() {
        // Arrange
        Employee manager = new Employee("1", "John", "Manager", 60000.0, null);
        Employee subordinate1 = new Employee("2", "Alice", "Sub", 40000.0, "1");
        Employee subordinate2 = new Employee("3", "Bob", "Sub", 50000.0, "1");
        
        EmployeeNode managerNode = new EmployeeNode(manager);
        EmployeeNode sub1Node = new EmployeeNode(subordinate1);
        EmployeeNode sub2Node = new EmployeeNode(subordinate2);
        
        managerNode.addChild(sub1Node);
        managerNode.addChild(sub2Node);
        
        List<EmployeeNode> allNodes = Arrays.asList(managerNode, sub1Node, sub2Node);

        // Act
        List<EmployeeValidator.ValidationResult> results = EmployeeValidator.validateAllManagerSalaries(allNodes);

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testValidateAllManagerSalaries_WithViolations() {
        // Arrange
        Employee manager = new Employee("1", "John", "Manager", 50000.0, null);
        Employee subordinate1 = new Employee("2", "Alice", "Sub", 40000.0, "1");
        Employee subordinate2 = new Employee("3", "Bob", "Sub", 50000.0, "1");
        
        EmployeeNode managerNode = new EmployeeNode(manager);
        EmployeeNode sub1Node = new EmployeeNode(subordinate1);
        EmployeeNode sub2Node = new EmployeeNode(subordinate2);
        
        managerNode.addChild(sub1Node);
        managerNode.addChild(sub2Node);
        
        List<EmployeeNode> allNodes = Arrays.asList(managerNode, sub1Node, sub2Node);

        // Act
        List<EmployeeValidator.ValidationResult> results = EmployeeValidator.validateAllManagerSalaries(allNodes);

        // Assert
        assertEquals(1, results.size());
        assertFalse(results.get(0).isValid());
        assertEquals("Manager is underpaid", results.get(0).getMessage());
    }

    @Test
    void testValidateAllReportingDepths_NoViolations() {
        // Arrange
        Employee employee1 = new Employee("1", "John", "Employee", 50000.0, null);
        Employee employee2 = new Employee("2", "Alice", "Employee", 60000.0, "1");
        
        EmployeeNode node1 = new EmployeeNode(employee1);
        EmployeeNode node2 = new EmployeeNode(employee2);
        node1.addChild(node2);
        
        List<EmployeeNode> allNodes = Arrays.asList(node1, node2);

        // Act
        List<EmployeeValidator.ValidationResult> results = EmployeeValidator.validateAllReportingDepths(allNodes);

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testValidateAllReportingDepths_WithViolations() {
        // Arrange
        Employee employee1 = new Employee("1", "Level1", "Manager", 60000.0, null);
        Employee employee2 = new Employee("2", "Level2", "Manager", 70000.0, "1");
        Employee employee3 = new Employee("3", "Level3", "Manager", 80000.0, "2");
        Employee employee4 = new Employee("4", "Level4", "Manager", 90000.0, "3");
        Employee employee5 = new Employee("5", "Level5", "Manager", 100000.0, "4");
        Employee employee6 = new Employee("6", "Level6", "Employee", 50000.0, "5");
        
        EmployeeNode node1 = new EmployeeNode(employee1);
        EmployeeNode node2 = new EmployeeNode(employee2);
        EmployeeNode node3 = new EmployeeNode(employee3);
        EmployeeNode node4 = new EmployeeNode(employee4);
        EmployeeNode node5 = new EmployeeNode(employee5);
        EmployeeNode node6 = new EmployeeNode(employee6);
        
        node1.addChild(node2);
        node2.addChild(node3);
        node3.addChild(node4);
        node4.addChild(node5);
        node5.addChild(node6);
        
        List<EmployeeNode> allNodes = Arrays.asList(node1, node2, node3, node4, node5, node6);

        // Act
        List<EmployeeValidator.ValidationResult> results = EmployeeValidator.validateAllReportingDepths(allNodes);

        // Assert
        assertEquals(1, results.size()); // Only node6 is too deep
        assertFalse(results.get(0).isValid());
    }

    @Test
    void testValidationResult_ConstructorAndGetters() {
        // Arrange & Act
        Employee testEmployee = new Employee("1", "Test", "Employee", 50000.0, null);
        EmployeeValidator.ValidationResult result = new EmployeeValidator.ValidationResult(false, "Test message", 100.5, testEmployee);

        // Assert
        assertFalse(result.isValid());
        assertEquals("Test message", result.getMessage());
        assertEquals(100.5, result.getAmount(), 0.01);
        assertEquals(testEmployee, result.getEmployee());
    }
} 