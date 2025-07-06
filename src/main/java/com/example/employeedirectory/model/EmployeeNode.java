package com.example.employeedirectory.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in the employee tree with bidirectional parent-child relationships.
 */
public class EmployeeNode {
    private Employee employee;
    private EmployeeNode parent;
    private List<EmployeeNode> children;
    
    public EmployeeNode(Employee employee) {
        this.employee = employee;
        this.children = new ArrayList<>();
    }
    
    // Getters
    public Employee getEmployee() {
        return employee;
    }
    
    public EmployeeNode getParent() {
        return parent;
    }
    
    public List<EmployeeNode> getChildren() {
        return new ArrayList<>(children); // Return a copy to prevent external modification
    }
    
    // Setters
    public void setParent(EmployeeNode parent) {
        this.parent = parent;
    }
    
    /**
     * Adds a child to this node and sets this node as the child's parent.
     * @param child the child node to add
     */
    public void addChild(EmployeeNode child) {
        if (child != null) {
            children.add(child);
            child.setParent(this);
        }
    }
    
    /**
     * Checks if this node is a root node (has no parent).
     * @return true if this is a root node, false otherwise
     */
    public boolean isRoot() {
        return parent == null;
    }
    
    /**
     * Checks if this node is a leaf node (has no children).
     * @return true if this is a leaf node, false otherwise
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }
    
    /**
     * Gets the depth of this node in the tree (root has depth 0).
     * @return the depth of this node
     */
    public int getDepth() {
        if (isRoot()) {
            return 0;
        }
        return parent.getDepth() + 1;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EmployeeNode{");
        sb.append("employee=").append(employee);
        sb.append(", parentId=").append(parent != null ? parent.getEmployee().getId() : "null");
        sb.append(", childrenCount=").append(children.size());
        sb.append("}");
        return sb.toString();
    }
} 