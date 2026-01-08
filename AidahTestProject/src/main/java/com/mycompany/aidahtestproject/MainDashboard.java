package com.mycompany.aidahtestproject;

import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {
    
    // Store the full Employee object
    private final Employee currentUser;

    // Constructor accepts the Employee object
    public MainDashboard(Employee user) {
        this.currentUser = user;

        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception e) {}

        setTitle("C60 Outlet Management System - Logged in as: " + user.getName());
        setSize(550, 500); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout(10, 10));

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        JLabel lblTitle = new JLabel("Main Menu", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel lblUser = new JLabel("User: " + user.getName() + " | Role: " + user.getRole(), SwingConstants.CENTER);
        
        headerPanel.add(lblTitle);
        headerPanel.add(lblUser);
        headerPanel.add(new JSeparator());
        add(headerPanel, BorderLayout.NORTH);

        // --- BUTTON PANEL ---
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        JButton btnAttendance = new JButton("Attendance Log");
        JButton btnSales = new JButton("Sales System");
        JButton btnStockMove = new JButton("Stock Movement");
        JButton btnStockCount = new JButton("Stock Count");
        JButton btnSearchEdit = new JButton("Search & Edit Data"); 
        JButton btnAnalytics = new JButton("Analytics Report");
        JButton btnExit = new JButton("Logout"); 

        buttonPanel.add(btnAttendance);
        buttonPanel.add(btnSales);
        buttonPanel.add(btnStockMove);
        buttonPanel.add(btnStockCount);
        buttonPanel.add(btnSearchEdit);
        buttonPanel.add(btnAnalytics);
        buttonPanel.add(btnExit);

        add(buttonPanel, BorderLayout.CENTER);

        // --- NAVIGATION LOGIC ---
        
        // 1. Attendance
        btnAttendance.addActionListener(e -> new LoginAttendance(currentUser).setVisible(true));
        
        // 2. Sales
        btnSales.addActionListener(e -> new SalesSystemGUI(currentUser).setVisible(true));
        
        // 3. Stock Movement (THIS IS THE FIX)
        // We MUST pass 'currentUser' here, otherwise it defaults to "Test Staff"
        btnStockMove.addActionListener(e -> new StockMovementGUI(currentUser).setVisible(true));
        
        // 4. Stock Count
        btnStockCount.addActionListener(e -> new StockCountGUI().setVisible(true));
        
        // 5. Search & Edit (Manager Only)
        btnSearchEdit.addActionListener(e -> {
            if (currentUser.getRole().equalsIgnoreCase("Manager")) {
                new SearchEditGUI(currentUser).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Access Denied: Managers Only.");
            }
        });

        // 6. Analytics (Manager Only)
        btnAnalytics.addActionListener(e -> {
            if (currentUser.getRole().equalsIgnoreCase("Manager")) {
                new SalesAnalytics(currentUser).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Access Denied: Managers Only.");
            }
        });

        // 7. Logout Logic
        btnExit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose(); 
                new LoginFrame().setVisible(true); 
            }
        });
    }

    // Default constructor for testing
    public MainDashboard() {
        this(new Employee("ADMIN01", "Default Admin", "Manager", "admin123"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainDashboard().setVisible(true));
    }
}