package com.mycompany.aidahtestproject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class AttendanceWriter {
    
    private static final String FILE_NAME = "attendance.csv";

    public static void saveAttendance(Attendance att) {
        
        File file = new File(FILE_NAME);
        boolean isNewFile = !file.exists() || file.length() == 0;

        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            
            // 1. Write Header if file is new
            if (isNewFile) {
                pw.println("Employee ID,Employee Name,Date,Clock In,Clock Out,Hours Worked");
            }
            
            // 2. Write Data
            // Format: ID, Name, Date, In, Out, Hours
            pw.println(att.getEmployeeId() + "," + 
                       att.getEmployeeName() + "," + 
                       att.getDateString() + "," +
                       att.getClockInString() + "," + 
                       att.getClockOutString() + "," + 
                       String.format("%.2f", att.getHoursWorked()));
                       
        } catch (IOException e) {
            System.out.println("Error saving attendance: " + e.getMessage());
        }
    }
}