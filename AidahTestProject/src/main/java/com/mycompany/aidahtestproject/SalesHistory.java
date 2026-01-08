package com.mycompany.aidahtestproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SalesHistory extends JFrame {

    // Data Structure
    static class SaleRecord {
        String date, time, customer, model, payment, staff;
        int qty;
        double total;

        public SaleRecord(String d, String t, String c, String m, int q, double tot, String p, String s) {
            this.date = d; 
            this.time = t; 
            this.customer = c; 
            this.model = m;
            this.qty = q; 
            this.total = tot; 
            this.payment = p;
            this.staff = s; // New Field
        }
    }

    private List<SaleRecord> allRecords = new ArrayList<>();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtStartDate, txtEndDate;
    private JLabel lblGrandTotal;

    public SalesHistory() {
        allRecords = loadAllSales();

        setTitle("Sales History Analytics");
        setSize(1000, 600); // Made slightly wider for the extra column
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- TOP PANEL: Filtering ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter by Date Range"));

        filterPanel.add(new JLabel("Start (YYYY-MM-DD):"));
        txtStartDate = new JTextField(10);
        filterPanel.add(txtStartDate);

        filterPanel.add(new JLabel("End (YYYY-MM-DD):"));
        txtEndDate = new JTextField(10);
        filterPanel.add(txtEndDate);

        JButton btnFilter = new JButton("Apply Filter");
        JButton btnReset = new JButton("Show All");
        filterPanel.add(btnFilter);
        filterPanel.add(btnReset);

        // --- CENTER PANEL: Table ---
        // Added "Staff" to columns
        String[] columns = {"Date", "Time", "Customer", "Model", "Qty", "Total (RM)", "Payment", "Staff"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } 
        };
        table = new JTable(tableModel);
        
        // Enable automatic sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);

        // --- BOTTOM PANEL: Summary ---
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblGrandTotal = new JLabel("Total Sales: RM 0.00");
        lblGrandTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblGrandTotal.setForeground(new Color(0, 102, 0));
        summaryPanel.add(lblGrandTotal);

        // Add to Frame
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);

        // --- Button Actions ---
        btnFilter.addActionListener(e -> applyFilter());
        btnReset.addActionListener(e -> {
            txtStartDate.setText("");
            txtEndDate.setText("");
            refreshTable(allRecords);
        });

        // Initial Load
        refreshTable(allRecords);
        setLocationRelativeTo(null);
    }

    private void refreshTable(List<SaleRecord> records) {
        tableModel.setRowCount(0);
        double grandTotal = 0;
        for (SaleRecord r : records) {
            // Add Staff to row data
            Object[] row = {r.date, r.time, r.customer, r.model, r.qty, String.format("%.2f", r.total), r.payment, r.staff};
            tableModel.addRow(row);
            grandTotal += r.total;
        }
        lblGrandTotal.setText(String.format("Total Sales in View: RM %.2f", grandTotal));
    }

    private void applyFilter() {
        String start = txtStartDate.getText().trim();
        String end = txtEndDate.getText().trim();

        if (start.isEmpty() || end.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Start and End dates.");
            return;
        }

        List<SaleRecord> filtered = new ArrayList<>();
        for (SaleRecord r : allRecords) {
            if (r.date.compareTo(start) >= 0 && r.date.compareTo(end) <= 0) {
                filtered.add(r);
            }
        }
        refreshTable(filtered);
    }

    // --- UPDATED CSV READER ---
    public ArrayList<SaleRecord> loadAllSales() {
        ArrayList<SaleRecord> records = new ArrayList<>();
        File file = new File("sales.csv"); 
        
        if (!file.exists()) return records;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // Skip Header if exists
            
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                
                // Variables to extract
                String fullDate = "";
                String cust = "";
                String model = "";
                int qty = 0;
                double total = 0.0;
                String pay = "";
                String staff = "Unknown";

                try {
                    // CASE 1: New Format (8 Columns) - Includes RefNo at start
                    // RefNo, Date, Cust, Model, Qty, Total, Pay, Staff
                    if (p.length >= 8) {
                        fullDate = p[1];
                        cust = p[2];
                        model = p[3];
                        qty = Integer.parseInt(p[4]);
                        total = Double.parseDouble(p[5]);
                        pay = p[6];
                        staff = p[7];
                    } 
                    // CASE 2: Old Format (7 Columns) - Starts with Date
                    // Date, Cust, Model, Qty, Total, Pay, Staff
                    else if (p.length == 7) {
                        fullDate = p[0];
                        cust = p[1];
                        model = p[2];
                        qty = Integer.parseInt(p[3]);
                        total = Double.parseDouble(p[4]);
                        pay = p[5];
                        staff = p[6];
                    } 
                    else {
                        continue; // Skip invalid rows
                    }

                    // Split Date and Time (Assuming format "YYYY-MM-DD HH:mm:ss")
                    String datePart = fullDate;
                    String timePart = "";
                    if (fullDate.contains(" ")) {
                        String[] dt = fullDate.split(" ");
                        datePart = dt[0];
                        timePart = dt[1];
                    }

                    records.add(new SaleRecord(datePart, timePart, cust, model, qty, total, pay, staff));

                } catch (NumberFormatException e) {
                    // Skip corrupted lines
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading sales.csv: " + e.getMessage());
        }
        
        return records;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new SalesHistory().setVisible(true));
    }
}