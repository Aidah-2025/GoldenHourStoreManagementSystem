package com.mycompany.aidahtestproject;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class StockCountGUI extends JFrame {
    private ArrayList<Model> inventory;
    private int currentIndex = 0;
    
    // Stats
    private int tallyCorrect = 0;
    private int tallyMismatch = 0;

    // UI Components
    private JLabel lblHeader, lblModelName, lblSystemStock, lblStats;
    private JTextField txtUserCount;
    private JTextArea logArea;
    private JButton btnVerify;
    private JButton btnBack; // New Back Button

    public StockCountGUI() {
        // Load data using your ModelReader
        inventory = ModelReader.loadModels();

        // Window Setup
        setTitle("Inventory Audit - Outlet C60");
        setSize(500, 650); // Increased height for the new button
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Header Section ---
        LocalTime now = LocalTime.now();
        String session = now.isAfter(LocalTime.of(19, 0)) ? "Night" : "Morning";
        lblHeader = new JLabel("=== " + session + " Stock Count (" + LocalDate.now() + ") ===", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblHeader, BorderLayout.NORTH);

        // --- Input Section ---
        // Changed rows from 4 to 5 to accommodate the Back button
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Model Verification"));

        lblModelName = new JLabel("Model:");
        lblSystemStock = new JLabel("System Record:");
        txtUserCount = new JTextField();
        btnVerify = new JButton("Verify & Next");
        btnBack = new JButton("← Back to Dashboard");
        btnBack.setBackground(new Color(240, 240, 240));

        inputPanel.add(new JLabel("Current Model:"));
        inputPanel.add(lblModelName);
        inputPanel.add(new JLabel("Expected (System):"));
        inputPanel.add(lblSystemStock);
        inputPanel.add(new JLabel("Physical Count:"));
        inputPanel.add(txtUserCount);
        inputPanel.add(new JLabel("")); // Spacer
        inputPanel.add(btnVerify);
        
        // Add Back Button to UI
        inputPanel.add(new JLabel("")); // Spacer
        inputPanel.add(btnBack);

        // --- Log & Stats Section ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        lblStats = new JLabel("Correct: 0 | Mismatch: 0", SwingConstants.CENTER);
        lblStats.setFont(new Font("Arial", Font.ITALIC, 13));
        
        bottomPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        bottomPanel.add(lblStats, BorderLayout.SOUTH);

        add(inputPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Initialization
        displayCurrentModel();

        // --- LOGIC ---
        btnVerify.addActionListener(e -> handleVerification());
        txtUserCount.addActionListener(e -> handleVerification());

        // Back Button Logic: Closes audit and returns to Dashboard
        btnBack.addActionListener(e -> {
            this.dispose(); // Close the audit window
            new MainDashboard().setVisible(true); // Return to main dashboard
        });
        
        setLocationRelativeTo(null); // Center the window
    }

    private void displayCurrentModel() {
        if (currentIndex < inventory.size()) {
            Model m = inventory.get(currentIndex);
            lblModelName.setText(m.getModelId());
            lblSystemStock.setText(String.valueOf(m.getStockQuantity()[0])); // Outlet C60
            txtUserCount.setText("");
            txtUserCount.requestFocus();
        } else {
            finishAudit();
        }
    }

    private void handleVerification() {
        try {
            int userCount = Integer.parseInt(txtUserCount.getText().trim());
            Model m = inventory.get(currentIndex);
            int systemStock = m.getStockQuantity()[0];

            if (userCount == systemStock) {
                tallyCorrect++;
                logArea.append("[MATCH] " + m.getModelId() + " (Count: " + userCount + ")\n");
            } else {
                tallyMismatch++;
                int diff = Math.abs(userCount - systemStock);
                logArea.append("[MISMATCH] " + m.getModelId() + " (Diff: " + diff + ")\n");
            }

            updateStats();
            currentIndex++;
            displayCurrentModel();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid whole number.");
        }
    }

    private void updateStats() {
        lblStats.setText("Correct: " + tallyCorrect + " | Mismatch: " + tallyMismatch);
    }

    private void finishAudit() {
        btnVerify.setEnabled(false);
        txtUserCount.setEditable(false);
        lblModelName.setText("N/A");
        lblSystemStock.setText("N/A");
        
        JOptionPane.showMessageDialog(this, 
            "Audit Completed!\nTotal Checked: " + inventory.size() + 
            "\nMatches: " + tallyCorrect + 
            "\nMismatches: " + tallyMismatch, 
            "Audit Result", JOptionPane.INFORMATION_MESSAGE);
            
        if (tallyMismatch > 0) {
            logArea.append("\nWARNING: Verification Required for Mismatches!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StockCountGUI().setVisible(true));
    }
}