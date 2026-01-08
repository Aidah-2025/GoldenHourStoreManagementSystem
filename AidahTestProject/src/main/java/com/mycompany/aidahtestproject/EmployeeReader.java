package com.mycompany.aidahtestproject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class EmployeeReader {
    public static ArrayList<Employee> loadEmployees() {
        ArrayList<Employee> employees = new ArrayList<>();
        File file = new File("employee.csv");

        // Safety check: if file doesn't exist, return empty list instead of crashing
        if (!file.exists()) {
            System.err.println("Warning: employee.csv not found.");
            return employees;
        }

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                
                // Skip empty lines
                if (line.isEmpty()) continue;

                String[] data = line.split(",");
                
                // Ensure we have all 4 columns before creating the object
                if (data.length >= 4) {
                    // .trim() is the secret to fixing "Invalid Login" errors!
                    String EmployeeId = data[0].trim();
                    String EmployeeName = data[1].trim();
                    String Role = data[2].trim();
                    String Password = data[3].trim();
                    
                    Employee emp = new Employee(EmployeeId, EmployeeName, Role, Password);
                    employees.add(emp);
                }
            }
        } catch (IOException e) {
        }
        return employees;
    } 
}