package com.mycompany.aidahtestproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class SalesHistoryGUI extends JFrame {
    
    public SalesHistoryGUI() {
        setTitle("Sales History & Reports");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Important: don't close the whole app
        
        // 1. Setup Table Columns
        String[] columns = {"Date", "Customer", "Model", "Qty", "Total (RM)", "Method", "Employee"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the table read-only
            }
        };
        JTable table = new JTable(model);
        
        // 2. Load Data and Calculate Total
        ArrayList<Sale> sales = SaleReader.loadSales();
        double grandTotal = 0;
        
        // Use a standard loop for clarity and safety
        for (Sale s : sales) {
            Object[] row = {
                s.getDate(),
                s.getCustomerName(),
                s.getModelId(),
                s.getQuantity(),
                String.format("%.2f", s.getTotalPrice()), // Formats price to 2 decimals
                s.getTransactionMethod(),
                s.getEmployeeName()
            };
            model.addRow(row);
            grandTotal += s.getTotalPrice();
        }
        
        // 3. Layout
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblTotal = new JLabel("Total Revenue: RM " + String.format("%.2f", grandTotal));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        bottomPanel.add(lblTotal);
        
        JButton refreshBtn = new JButton("Refresh");
        bottomPanel.add(refreshBtn);
        
        add(bottomPanel, BorderLayout.NORTH);
        
        // 4. Refresh Button Logic
        refreshBtn.addActionListener(e -> {
            dispose();
            new SalesHistoryGUI().setVisible(true);
        });
    }
}