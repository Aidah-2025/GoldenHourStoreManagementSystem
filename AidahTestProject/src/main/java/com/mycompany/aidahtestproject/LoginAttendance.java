package com.mycompany.aidahtestproject;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginAttendance extends JFrame {
    
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private ArrayList<Employee> employeeList = new ArrayList<>();
    
    private final Employee currentUser; 
    private final HashMap<String, LocalDateTime> activeSessions = new HashMap<>(); 
    private final String ATTENDANCE_FILE = "attendance.csv";

    public LoginAttendance(Employee user) {
        this.currentUser = user; 

        try {
            employeeList = EmployeeReader.loadEmployees();
        } catch (Exception e) {
            System.out.println("Warning: EmployeeReader not found.");
        }

        setupGUI();
        
        setTitle("Attendance & Dashboard - Logged in as: " + currentUser.getName());
        setSize(600, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        checkForActiveSession();
    }

    // Default constructor
    public LoginAttendance() {
        this(new Employee("TEST", "Test User", "Manager", "pass"));
    }

    private void setupGUI() {
        JPanel dashPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        dashPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel roleLabel = new JLabel("Role: " + currentUser.getRole(), SwingConstants.CENTER);
        roleLabel.setForeground(Color.GRAY);
        
        JButton clockInBtn = new JButton("Clock In");
        JButton clockOutBtn = new JButton("Clock Out");
        
        // --- BUTTONS ---
        JButton regBtn = new JButton("👤 Register New Employee");
        JButton historyBtn = new JButton("📜 View Sales History");
        JButton btnBackToMain = new JButton("Back");
        btnBackToMain.setBackground(new Color(200, 220, 240));

        JButton salesBtn = new JButton("💰 Open Sales System");
        JButton stockCountBtn = new JButton("📋 Morning Stock Count");
        JButton stockMoveBtn = new JButton("📦 Stock In / Stock Out");
        JButton analyticsBtn = new JButton("📈 View Analytics Report");
        
        // --- ADDING COMPONENTS ---
        dashPanel.add(welcomeLabel);
        dashPanel.add(roleLabel);
        dashPanel.add(new JSeparator());
        
        dashPanel.add(new JLabel("Attendance Actions:", SwingConstants.CENTER));
        dashPanel.add(clockInBtn);
        dashPanel.add(clockOutBtn);
        dashPanel.add(new JSeparator());

        if (currentUser.getRole().equalsIgnoreCase("Manager")) {
            dashPanel.add(new JLabel("Manager Controls:", SwingConstants.CENTER));
            dashPanel.add(regBtn);
            dashPanel.add(historyBtn);
            dashPanel.add(new JSeparator());
        }

        dashPanel.add(new JLabel("Quick Access:", SwingConstants.CENTER));
        dashPanel.add(salesBtn);
        dashPanel.add(stockCountBtn);
        dashPanel.add(stockMoveBtn);
        dashPanel.add(analyticsBtn);
        dashPanel.add(new JSeparator());
        dashPanel.add(btnBackToMain);

        // Registration Panel
        JPanel regPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        regPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        JTextField newName = new JTextField();
        JTextField newId = new JTextField();
        JTextField newPass = new JTextField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Full-time", "Part-time", "Manager"});
        JButton saveUserBtn = new JButton("Save Employee");
        JButton backToDashBtn = new JButton("Back");

        regPanel.add(new JLabel("Name:")); regPanel.add(newName);
        regPanel.add(new JLabel("ID:")); regPanel.add(newId);
        regPanel.add(new JLabel("Password:")); regPanel.add(newPass);
        regPanel.add(new JLabel("Role:")); regPanel.add(roleBox);
        regPanel.add(saveUserBtn); regPanel.add(backToDashBtn);

        mainPanel.add(dashPanel, "DASH");
        mainPanel.add(regPanel, "REG");
        add(mainPanel);

        // --- LISTENERS ---

        btnBackToMain.addActionListener(e -> {
            this.dispose();
            new MainDashboard(currentUser).setVisible(true); 
        });

        clockInBtn.addActionListener(e -> {
            if (activeSessions.containsKey(currentUser.getId())) {
                JOptionPane.showMessageDialog(this, "You are already clocked in!");
                return;
            }
            Attendance session = new Attendance(currentUser.getId(), currentUser.getName());
            activeSessions.put(currentUser.getId(), session.getClockInTime());
            AttendanceWriter.saveAttendance(session);
            JOptionPane.showMessageDialog(this, "Clocked In at " + session.getClockInString());
        });

        clockOutBtn.addActionListener(e -> {
            if (!activeSessions.containsKey(currentUser.getId())) {
                JOptionPane.showMessageDialog(this, "Please Clock In first!");
                return;
            }
            Attendance session = new Attendance(currentUser.getId(), currentUser.getName());
            session.performClockOut(); 
            
            boolean success = updateAttendanceRecord(currentUser.getId(), session.getClockOutString());
            
            if (success) {
                activeSessions.remove(currentUser.getId());
                JOptionPane.showMessageDialog(this, "Clocked Out Successfully.\nHours: " + String.format("%.2f", session.getHoursWorked()));
            } else {
                JOptionPane.showMessageDialog(this, "Error updating attendance file.");
            }
        });

        salesBtn.addActionListener(e -> new SalesSystemGUI(this.currentUser).setVisible(true)); 
        stockCountBtn.addActionListener(e -> new StockCountGUI().setVisible(true));
        stockMoveBtn.addActionListener(e -> new StockMovementGUI().setVisible(true));
        analyticsBtn.addActionListener(e -> {
             if (currentUser.getRole().equalsIgnoreCase("Manager")) {
                new SalesAnalytics(currentUser).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Access Denied: Managers Only.");
            }
        });
        historyBtn.addActionListener(e -> new SalesHistory().setVisible(true)); 
        regBtn.addActionListener(e -> cardLayout.show(mainPanel, "REG"));
        backToDashBtn.addActionListener(e -> cardLayout.show(mainPanel, "DASH"));

        saveUserBtn.addActionListener(e -> {
            Employee newEmp = new Employee(newId.getText(), newName.getText(), (String)roleBox.getSelectedItem(), newPass.getText());
            employeeList.add(newEmp);
            EmployeeWriter.saveEmployee(newEmp);
            JOptionPane.showMessageDialog(this, "Employee Saved!");
            cardLayout.show(mainPanel, "DASH");
        });
    }

    // --- HELPER METHODS ---

    private boolean updateAttendanceRecord(String userId, String clockOutTimeOnly) {
        File file = new File(ATTENDANCE_FILE);
        List<String> lines = new ArrayList<>();
        boolean recordFound = false;
        DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                
                // NEW: Skip the header row so we don't crash calculating hours on words
                if (line.startsWith("Employee ID")) {
                    lines.add(line);
                    continue;
                }

                String[] parts = line.split(",");
                // ID[0], Name[1], Date[2], In[3], Out[4]
                if (parts.length >= 2 && parts[0].equals(userId)) {
                    String existingOut = (parts.length > 4) ? parts[4].trim() : "Active";
                    
                    if (existingOut.equals("Active") || existingOut.equals("00:00") || existingOut.equals("null")) {
                        try {
                            String name = parts[1];
                            String dateStr = parts[2];
                            String timeInStr = parts[3];
                            
                            LocalDateTime inTime = LocalDateTime.parse(dateStr + " " + timeInStr, fullFormatter);
                            LocalDateTime outTime = LocalDateTime.parse(dateStr + " " + clockOutTimeOnly, fullFormatter);
                            
                            if (outTime.isBefore(inTime)) outTime = outTime.plusDays(1);

                            long minutes = Duration.between(inTime, outTime).toMinutes();
                            double hours = minutes / 60.0;

                            String updatedLine = String.format("%s,%s,%s,%s,%s,%.2f", 
                                    userId, name, dateStr, timeInStr, clockOutTimeOnly, hours);
                            
                            lines.add(updatedLine);
                            recordFound = true;
                        } catch (Exception e) {
                            lines.add(line); 
                        }
                    } else {
                        lines.add(line);
                    }
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (recordFound) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (String s : lines) {
                    bw.write(s);
                    bw.newLine();
                }
                return true;
            } catch (IOException e) { return false; }
        }
        return false; 
    }
    
    private void checkForActiveSession() {
        File file = new File(ATTENDANCE_FILE);
        if(!file.exists()) return;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // NEW: Skip header
                if (line.startsWith("Employee ID")) continue;

                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].equals(currentUser.getId())) {
                     String existingOut = parts[4].trim();
                     if (existingOut.equals("Active") || existingOut.equals("00:00") || existingOut.equals("null")) {
                         activeSessions.put(currentUser.getId(), LocalDateTime.now()); 
                     }
                }
            }
        } catch (IOException e) {}
    }
}