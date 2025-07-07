package com.example.employeedirectory;

import com.example.employeedirectory.model.Employee;
import com.example.employeedirectory.model.EmployeeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Represents a tree structure of employees with bidirectional parent-child relationships.
 * This class focuses only on tree structure and navigation, not business logic.
 */
public class EmployeeTree {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeTree.class);
    
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
                    logger.warn("Manager with ID {} not found for employee {}", managerId, employee.getId());
                    rootNodes.add(node);
                }
            }
        }
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
        logger.debug("Employee Tree Structure:");
        logger.debug("========================");
        
        if (rootNodes.isEmpty()) {
            logger.debug("No employees found.");
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
        
        logger.debug("{}├─ {} {} (ID: {}, Salary: ${})", 
                    indent, employee.getFirstName(), employee.getLastName(), 
                    employee.getId(), employee.getSalary());
        
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
} 