package com.example.employeedirectory.model;

/**
 * Represents an employee record from the CSV file.
 */
public class Employee {
    private String id;
    private String firstName;
    private String lastName;
    private double salary;
    private String managerId;

    public Employee(String id, String firstName, String lastName, double salary, String managerId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.managerId = managerId;
    }

    // Getters
    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public double getSalary() { return salary; }
    public String getManagerId() { return managerId; }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", salary=" + salary +
                ", managerId='" + managerId + '\'' +
                '}';
    }
} 