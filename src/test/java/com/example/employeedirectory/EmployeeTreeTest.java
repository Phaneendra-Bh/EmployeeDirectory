package com.example.employeedirectory;

import com.example.employeedirectory.model.Employee;
import com.example.employeedirectory.model.EmployeeNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTreeTest {

    private EmployeeTree employeeTree;

    @BeforeEach
    void setUp() {
        employeeTree = new EmployeeTree();
    }

    @Test
    void testBuildTree_SingleEmployee() {
        // Arrange
        Employee employee = new Employee("1", "John", "Doe", 50000.0, null);
        List<Employee> employees = Arrays.asList(employee);

        // Act
        employeeTree.buildTree(employees);

        // Assert
        assertEquals(1, employeeTree.getTotalEmployeeCount());
        assertEquals(1, employeeTree.getRootNodeCount());
        assertNotNull(employeeTree.getNodeById("1"));
        assertTrue(employeeTree.getRootNodes().get(0).isRoot());
    }

    @Test
    void testBuildTree_ManagerWithSubordinates() {
        // Arrange
        Employee manager = new Employee("1", "John", "Manager", 60000.0, null);
        Employee subordinate1 = new Employee("2", "Alice", "Sub", 40000.0, "1");
        Employee subordinate2 = new Employee("3", "Bob", "Sub", 50000.0, "1");
        List<Employee> employees = Arrays.asList(manager, subordinate1, subordinate2);

        // Act
        employeeTree.buildTree(employees);

        // Assert
        assertEquals(3, employeeTree.getTotalEmployeeCount());
        assertEquals(1, employeeTree.getRootNodeCount());
        
        EmployeeNode managerNode = employeeTree.getNodeById("1");
        EmployeeNode sub1Node = employeeTree.getNodeById("2");
        EmployeeNode sub2Node = employeeTree.getNodeById("3");
        
        assertNotNull(managerNode);
        assertNotNull(sub1Node);
        assertNotNull(sub2Node);
        
        assertTrue(managerNode.isRoot());
        assertFalse(sub1Node.isRoot());
        assertFalse(sub2Node.isRoot());
        
        assertEquals(2, managerNode.getChildren().size());
        assertTrue(managerNode.getChildren().contains(sub1Node));
        assertTrue(managerNode.getChildren().contains(sub2Node));
        
        assertEquals(managerNode, sub1Node.getParent());
        assertEquals(managerNode, sub2Node.getParent());
    }

    @Test
    void testBuildTree_MultiLevelHierarchy() {
        // Arrange
        Employee ceo = new Employee("1", "CEO", "Boss", 100000.0, null);
        Employee manager = new Employee("2", "Manager", "Mid", 70000.0, "1");
        Employee employee = new Employee("3", "Employee", "Low", 50000.0, "2");
        List<Employee> employees = Arrays.asList(ceo, manager, employee);

        // Act
        employeeTree.buildTree(employees);

        // Assert
        assertEquals(3, employeeTree.getTotalEmployeeCount());
        assertEquals(1, employeeTree.getRootNodeCount());
        
        EmployeeNode ceoNode = employeeTree.getNodeById("1");
        EmployeeNode managerNode = employeeTree.getNodeById("2");
        EmployeeNode employeeNode = employeeTree.getNodeById("3");
        
        assertTrue(ceoNode.isRoot());
        assertEquals(1, ceoNode.getChildren().size());
        assertEquals(managerNode, ceoNode.getChildren().get(0));
        
        assertFalse(managerNode.isRoot());
        assertEquals(1, managerNode.getChildren().size());
        assertEquals(employeeNode, managerNode.getChildren().get(0));
        
        assertFalse(employeeNode.isRoot());
        assertTrue(employeeNode.isLeaf());
        assertEquals(0, employeeNode.getChildren().size());
        
        assertEquals(ceoNode, managerNode.getParent());
        assertEquals(managerNode, employeeNode.getParent());
    }

    @Test
    void testBuildTree_MissingManager() {
        // Arrange
        Employee employee = new Employee("1", "John", "Employee", 50000.0, "999"); // Non-existent manager
        List<Employee> employees = Arrays.asList(employee);

        // Act
        employeeTree.buildTree(employees);

        // Assert
        assertEquals(1, employeeTree.getTotalEmployeeCount());
        assertEquals(1, employeeTree.getRootNodeCount()); // Should be treated as root
        assertTrue(employeeTree.getNodeById("1").isRoot());
    }

    @Test
    void testBuildTree_EmptyList() {
        // Arrange
        List<Employee> employees = Collections.emptyList();

        // Act
        employeeTree.buildTree(employees);

        // Assert
        assertEquals(0, employeeTree.getTotalEmployeeCount());
        assertEquals(0, employeeTree.getRootNodeCount());
        assertTrue(employeeTree.getAllNodes().isEmpty());
    }

    @Test
    void testGetDirectReports_ValidManager() {
        // Arrange
        Employee manager = new Employee("1", "John", "Manager", 60000.0, null);
        Employee subordinate1 = new Employee("2", "Alice", "Sub", 40000.0, "1");
        Employee subordinate2 = new Employee("3", "Bob", "Sub", 50000.0, "1");
        List<Employee> employees = Arrays.asList(manager, subordinate1, subordinate2);
        employeeTree.buildTree(employees);

        // Act
        List<EmployeeNode> directReports = employeeTree.getDirectReports("1");

        // Assert
        assertEquals(2, directReports.size());
        assertTrue(directReports.stream().anyMatch(node -> node.getEmployee().getId().equals("2")));
        assertTrue(directReports.stream().anyMatch(node -> node.getEmployee().getId().equals("3")));
    }

    @Test
    void testGetDirectReports_NonManager() {
        // Arrange
        Employee employee = new Employee("1", "John", "Employee", 50000.0, null);
        List<Employee> employees = Arrays.asList(employee);
        employeeTree.buildTree(employees);

        // Act
        List<EmployeeNode> directReports = employeeTree.getDirectReports("1");

        // Assert
        assertTrue(directReports.isEmpty());
    }

    @Test
    void testGetDirectReports_NonExistentEmployee() {
        // Arrange
        Employee employee = new Employee("1", "John", "Employee", 50000.0, null);
        List<Employee> employees = Arrays.asList(employee);
        employeeTree.buildTree(employees);

        // Act
        List<EmployeeNode> directReports = employeeTree.getDirectReports("999");

        // Assert
        assertTrue(directReports.isEmpty());
    }

    @Test
    void testGetAllSubordinates_ValidManager() {
        // Arrange
        Employee ceo = new Employee("1", "CEO", "Boss", 100000.0, null);
        Employee manager = new Employee("2", "Manager", "Mid", 70000.0, "1");
        Employee employee1 = new Employee("3", "Employee1", "Low", 50000.0, "2");
        Employee employee2 = new Employee("4", "Employee2", "Low", 55000.0, "2");
        List<Employee> employees = Arrays.asList(ceo, manager, employee1, employee2);
        employeeTree.buildTree(employees);

        // Act
        List<EmployeeNode> allSubordinates = employeeTree.getAllSubordinates("1");

        // Assert
        assertEquals(3, allSubordinates.size());
        assertTrue(allSubordinates.stream().anyMatch(node -> node.getEmployee().getId().equals("2")));
        assertTrue(allSubordinates.stream().anyMatch(node -> node.getEmployee().getId().equals("3")));
        assertTrue(allSubordinates.stream().anyMatch(node -> node.getEmployee().getId().equals("4")));
    }

    @Test
    void testGetAllSubordinates_LeafNode() {
        // Arrange
        Employee employee = new Employee("1", "John", "Employee", 50000.0, null);
        List<Employee> employees = Arrays.asList(employee);
        employeeTree.buildTree(employees);

        // Act
        List<EmployeeNode> allSubordinates = employeeTree.getAllSubordinates("1");

        // Assert
        assertTrue(allSubordinates.isEmpty());
    }

    @Test
    void testGetNodeById_ExistingNode() {
        // Arrange
        Employee employee = new Employee("1", "John", "Employee", 50000.0, null);
        List<Employee> employees = Arrays.asList(employee);
        employeeTree.buildTree(employees);

        // Act
        EmployeeNode node = employeeTree.getNodeById("1");

        // Assert
        assertNotNull(node);
        assertEquals("1", node.getEmployee().getId());
        assertEquals("John", node.getEmployee().getFirstName());
    }

    @Test
    void testGetNodeById_NonExistentNode() {
        // Arrange
        Employee employee = new Employee("1", "John", "Employee", 50000.0, null);
        List<Employee> employees = Arrays.asList(employee);
        employeeTree.buildTree(employees);

        // Act
        EmployeeNode node = employeeTree.getNodeById("999");

        // Assert
        assertNull(node);
    }

    @Test
    void testGetAllNodes() {
        // Arrange
        Employee manager = new Employee("1", "John", "Manager", 60000.0, null);
        Employee subordinate1 = new Employee("2", "Alice", "Sub", 40000.0, "1");
        Employee subordinate2 = new Employee("3", "Bob", "Sub", 50000.0, "1");
        List<Employee> employees = Arrays.asList(manager, subordinate1, subordinate2);
        employeeTree.buildTree(employees);

        // Act
        List<EmployeeNode> allNodes = employeeTree.getAllNodes();

        // Assert
        assertEquals(3, allNodes.size());
        assertTrue(allNodes.stream().anyMatch(node -> node.getEmployee().getId().equals("1")));
        assertTrue(allNodes.stream().anyMatch(node -> node.getEmployee().getId().equals("2")));
        assertTrue(allNodes.stream().anyMatch(node -> node.getEmployee().getId().equals("3")));
    }

    @Test
    void testGetRootNodes() {
        // Arrange
        Employee ceo1 = new Employee("1", "CEO1", "Boss", 100000.0, null);
        Employee ceo2 = new Employee("2", "CEO2", "Boss", 95000.0, null);
        Employee manager = new Employee("3", "Manager", "Mid", 70000.0, "1");
        List<Employee> employees = Arrays.asList(ceo1, ceo2, manager);
        employeeTree.buildTree(employees);

        // Act
        List<EmployeeNode> rootNodes = employeeTree.getRootNodes();

        // Assert
        assertEquals(2, rootNodes.size());
        assertTrue(rootNodes.stream().anyMatch(node -> node.getEmployee().getId().equals("1")));
        assertTrue(rootNodes.stream().anyMatch(node -> node.getEmployee().getId().equals("2")));
    }

    @Test
    void testBuildTree_ClearExistingTree() {
        // Arrange
        Employee employee1 = new Employee("1", "John", "Employee", 50000.0, null);
        List<Employee> employees1 = Arrays.asList(employee1);
        employeeTree.buildTree(employees1);
        
        assertEquals(1, employeeTree.getTotalEmployeeCount());

        // Act
        Employee employee2 = new Employee("2", "Jane", "Employee", 60000.0, null);
        List<Employee> employees2 = Arrays.asList(employee2);
        employeeTree.buildTree(employees2);

        // Assert
        assertEquals(1, employeeTree.getTotalEmployeeCount());
        assertNull(employeeTree.getNodeById("1"));
        assertNotNull(employeeTree.getNodeById("2"));
    }
} 