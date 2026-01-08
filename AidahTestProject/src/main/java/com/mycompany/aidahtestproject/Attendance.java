package com.mycompany.aidahtestproject;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class Attendance {
    
    private final String employeeId;
    private final String employeeName; // Added Name
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime; 
    private double hoursWorked;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // --- CONSTRUCTOR: NEW SESSION ---
    public Attendance(String employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.clockInTime = LocalDateTime.now(); 
        this.clockOutTime = null;               
        this.hoursWorked = 0.0;
    }
    
    // --- METHOD: CLOCK OUT ---
    // Sets the end time and calculates hours immediately
    public void performClockOut() {
        this.clockOutTime = LocalDateTime.now();
        this.hoursWorked = calculateHoursWorked();
    }

    private double calculateHoursWorked() {
        if (clockInTime == null || clockOutTime == null) return 0.0;
        Duration duration = Duration.between(clockInTime, clockOutTime);
        return duration.toMinutes() / 60.0;
    }
    
    // --- GETTERS FOR CSV WRITING ---
    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    
    public String getDateString() {
        return clockInTime.format(DATE_FORMATTER);
    }

    public String getClockInString() {
        return clockInTime.format(TIME_FORMATTER);
    }

    public String getClockOutString() {
        if (clockOutTime == null) return "00:00"; 
        return clockOutTime.format(TIME_FORMATTER);
    }
    
    public double getHoursWorked() { return hoursWorked; }
    
    // Getter for internal logic
    public LocalDateTime getClockInTime() { return clockInTime; }
}