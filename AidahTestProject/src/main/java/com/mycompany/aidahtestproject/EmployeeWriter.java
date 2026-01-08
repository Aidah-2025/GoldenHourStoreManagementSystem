/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aidahtestproject;

/**
 *
 * @author nuraidahmaisarahbintiazeman
 */
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

public class EmployeeWriter {
    /**
     * Saves an employee to the CSV file.Returns true if successful, false if an error occurred.
     * @param emp
     * @return 
     */
    public static boolean saveEmployee(Employee emp) {
        // Using try-with-resources ensures the file is closed even if an error occurs
        try (PrintWriter pw = new PrintWriter(new FileWriter("employee.csv", true))) {
            
            pw.println(emp.getId() + "," + 
                       emp.getName() + "," + 
                       emp.getRole() + "," + 
                       emp.getPassword());
            
            return true; 
        } catch (IOException e) {
            return false;
        }
    }
}