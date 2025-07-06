package com.example.employeedirectory;

import com.example.employeedirectory.model.Employee;
import com.example.employeedirectory.model.EmployeeNode;

import java.util.*;

/**
 * Represents a tree structure of employees with bidirectional parent-child relationships.
 */
public class EmployeeTree {
    private List<EmployeeNode> rootNodes;
    private Map<String, EmployeeNode> employeeNodeMap;
    
    public EmployeeTree() {
        this.rootNodes = new ArrayList<>();
        this.employeeNodeMap = new HashMap<>();
    }
    
    /**
     * Builds the employee tree from a list of employees.
     * @param employees the list of employees to build the tree from
     */
    public void buildTree(List<Employee> employees) {
        // Clear existing tree
        rootNodes.clear();
        employeeNodeMap.clear();
        
        // First pass: Create nodes for all employees
        for (Employee employee : employees) {
            EmployeeNode node = new EmployeeNode(employee);
            employeeNodeMap.put(employee.getId(), node);
        }
        
        // Second pass: Establish parent-child relationships
        for (Employee employee : employees) {
            EmployeeNode node = employeeNodeMap.get(employee.getId());
            String managerId = employee.getManagerId();
            
            if (managerId == null || managerId.isEmpty()) {
                // This is a root node (no manager)
                rootNodes.add(node);
            } else {
                // This employee has a manager
                EmployeeNode parentNode = employeeNodeMap.get(managerId);
                if (parentNode != null) {
                    parentNode.addChild(node);
                } else {
                    // Manager not found, treat as root node
                    System.err.println("Warning: Manager with ID " + managerId + " not found for employee " + employee.getId());
                    rootNodes.add(node);
                }
            }
        }
    }
    
    /**
     * Validates salary requirements for all managers.
     * Managers should earn at least 20% more than average salary of direct subordinates,
     * but not more than 50% more than average salary of direct subordinates.
     */
    public void validateManagerSalaries() {
        System.out.println("Salary Validation Results:");
        System.out.println("=========================");
        
        boolean hasViolations = false;
        
        for (EmployeeNode node : employeeNodeMap.values()) {
            if (!node.isLeaf()) { // Only check managers (nodes with children)
                List<EmployeeNode> directReports = node.getChildren();
                if (!directReports.isEmpty()) {
                    double averageSubordinateSalary = calculateAverageSalary(directReports);
                    double managerSalary = node.getEmployee().getSalary();
                    
                    double minRequiredSalary = averageSubordinateSalary * 1.20; // 20% more
                    double maxAllowedSalary = averageSubordinateSalary * 1.50; // 50% more
                    
                    if (managerSalary < minRequiredSalary || managerSalary > maxAllowedSalary) {
                        hasViolations = true;
                        Employee manager = node.getEmployee();
                        
                        System.out.println("❌ VIOLATION: " + manager.getFirstName() + " " + manager.getLastName() + " (ID: " + manager.getId() + ")");
                        System.out.println("   Manager Salary: $" + String.format("%.2f", managerSalary));
                        System.out.println("   Average Subordinate Salary: $" + String.format("%.2f", averageSubordinateSalary));
                        System.out.println("   Required Range: $" + String.format("%.2f", minRequiredSalary) + " - $" + String.format("%.2f", maxAllowedSalary));
                        
                        System.out.println("   Direct Reports:");
                        for (EmployeeNode report : directReports) {
                            Employee emp = report.getEmployee();
                            System.out.println("     - " + emp.getFirstName() + " " + emp.getLastName() + " (ID: " + emp.getId() + ", Salary: $" + emp.getSalary() + ")");
                        }
                        System.out.println();
                    }
                }
            }
        }
        
        if (!hasViolations) {
            System.out.println("✅ All managers meet the salary requirements!");
        }
    }
    
    /**
     * Calculates the average salary of a list of employee nodes.
     * @param nodes the list of employee nodes
     * @return the average salary
     */
    private double calculateAverageSalary(List<EmployeeNode> nodes) {
        if (nodes.isEmpty()) {
            return 0.0;
        }
        
        double totalSalary = 0.0;
        for (EmployeeNode node : nodes) {
            totalSalary += node.getEmployee().getSalary();
        }
        
        return totalSalary / nodes.size();
    }
    
    /**
     * Gets all root nodes (employees with no manager).
     * @return list of root nodes
     */
    public List<EmployeeNode> getRootNodes() {
        return new ArrayList<>(rootNodes);
    }
    
    /**
     * Gets a node by employee ID.
     * @param employeeId the employee ID
     * @return the employee node or null if not found
     */
    public EmployeeNode getNodeById(String employeeId) {
        return employeeNodeMap.get(employeeId);
    }
    
    /**
     * Gets all nodes in the tree.
     * @return list of all employee nodes
     */
    public List<EmployeeNode> getAllNodes() {
        return new ArrayList<>(employeeNodeMap.values());
    }
    
    /**
     * Gets the total number of employees in the tree.
     * @return the total count
     */
    public int getTotalEmployeeCount() {
        return employeeNodeMap.size();
    }
    
    /**
     * Gets the number of root nodes.
     * @return the number of root nodes
     */
    public int getRootNodeCount() {
        return rootNodes.size();
    }
    
    /**
     * Prints the tree structure in a hierarchical format.
     */
    public void printTree() {
        System.out.println("Employee Tree Structure:");
        System.out.println("========================");
        
        if (rootNodes.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }
        
        for (EmployeeNode rootNode : rootNodes) {
            printNode(rootNode, 0);
        }
    }
    
    /**
     * Recursively prints a node and its children with proper indentation.
     * @param node the node to print
     * @param depth the current depth in the tree
     */
    private void printNode(EmployeeNode node, int depth) {
        String indent = "  ".repeat(depth);
        Employee employee = node.getEmployee();
        
        System.out.println(indent + "├─ " + employee.getFirstName() + " " + employee.getLastName() + 
                          " (ID: " + employee.getId() + ", Salary: $" + employee.getSalary() + ")");
        
        List<EmployeeNode> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
            EmployeeNode child = children.get(i);
            if (i == children.size() - 1) {
                // Last child
                printNode(child, depth + 1);
            } else {
                // Not the last child
                printNode(child, depth + 1);
            }
        }
    }
    
    /**
     * Gets all direct reports (children) of a specific employee.
     * @param employeeId the employee ID
     * @return list of direct reports
     */
    public List<EmployeeNode> getDirectReports(String employeeId) {
        EmployeeNode node = employeeNodeMap.get(employeeId);
        if (node != null) {
            return node.getChildren();
        }
        return new ArrayList<>();
    }
    
    /**
     * Gets all subordinates (children and their descendants) of a specific employee.
     * @param employeeId the employee ID
     * @return list of all subordinates
     */
    public List<EmployeeNode> getAllSubordinates(String employeeId) {
        List<EmployeeNode> subordinates = new ArrayList<>();
        EmployeeNode node = employeeNodeMap.get(employeeId);
        if (node != null) {
            collectSubordinates(node, subordinates);
        }
        return subordinates;
    }
    
    /**
     * Recursively collects all subordinates of a node.
     * @param node the node to collect subordinates from
     * @param subordinates the list to add subordinates to
     */
    private void collectSubordinates(EmployeeNode node, List<EmployeeNode> subordinates) {
        for (EmployeeNode child : node.getChildren()) {
            subordinates.add(child);
            collectSubordinates(child, subordinates);
        }
    }
    
    /**
     * Prints all employees who have more than 4 managers between them and the CEO (root node).
     */
    public void printEmployeesWithMoreThan4Managers() {
        System.out.println("Employees with more than 4 managers between them and the CEO:");
        System.out.println("============================================================");
        boolean found = false;
        for (EmployeeNode node : employeeNodeMap.values()) {
            int depth = node.getDepth();
            if (depth > 4) {
                Employee emp = node.getEmployee();
                found = true;
                System.out.println(emp.getFirstName() + " " + emp.getLastName() + " (ID: " + emp.getId() + ", Depth: " + depth + ")");
            }
        }
        if (!found) {
            System.out.println("No employees have more than 4 managers between them and the CEO.");
        }
        System.out.println();
    }
} 