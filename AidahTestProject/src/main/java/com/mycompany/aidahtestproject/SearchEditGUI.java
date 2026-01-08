package com.mycompany.aidahtestproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class SearchEditGUI extends JFrame {

    private final Employee currentUser;
    
    // File Paths
    private final String STOCK_FILE = "model.csv"; 
    private final String SALES_FILE = "sales.csv";

    // --- GUI COMPONENTS: STOCK ---
    private JComboBox<String> cmbModelSelect; 
    private JTable stockTable;
    private DefaultTableModel stockModel;
    
    // Edit fields
    private JTextField txtEditModel, txtEditPrice, txtEditQty, txtEditOutlet;

    // --- GUI COMPONENTS: SALES ---
    private JTextField txtSalesSearch;
    private JTable salesTable;
    private DefaultTableModel salesModel;
    private JTextField txtEditDate, txtEditCust, txtEditItem, txtEditSaleQty, txtEditTotal, txtEditPay, txtEditStaff;

    // Data Caches
    private List<String[]> stockDataCache = new ArrayList<>();
    private List<String> stockHeaders = new ArrayList<>(); // To store "Model, Price, C60, C61..."
    private List<String[]> salesDataCache = new ArrayList<>();

    public SearchEditGUI(Employee user) {
        this.currentUser = user;
        setTitle("Search & Edit System - " + user.getName());
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // Load data first
        loadStockData(); 
        
        tabbedPane.addTab("📦 Stock Management (Matrix View)", createStockPanel());
        tabbedPane.addTab("💰 Sales Management", createSalesPanel());

        add(tabbedPane);
        
        // Initial Views
        refreshStockTable();
        refreshSalesData("");
    }

    public SearchEditGUI() {
        this(new Employee("ADMIN", "Admin", "Manager", "pass"));
    }

    // =================================================================
    // TAB 1: STOCK MANAGEMENT
    // =================================================================
    private JPanel createStockPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // 1. Top Panel: Dropdown
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cmbModelSelect = new JComboBox<>();
        cmbModelSelect.setPreferredSize(new Dimension(200, 30));
        populateModelDropdown();

        JButton btnRefresh = new JButton("🔄 Refresh Data");
        
        topPanel.add(new JLabel("Search Model Name:"));
        topPanel.add(cmbModelSelect);
        topPanel.add(btnRefresh);

        // 2. Table
        stockModel = new DefaultTableModel();
        stockTable = new JTable(stockModel);
        stockTable.setRowHeight(25);
        // Enable Auto Resize off to scroll horizontally if many outlets exist
        stockTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane scrollPane = new JScrollPane(stockTable);

        // 3. Edit Form (Bottom)
        JPanel editPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        editPanel.setBorder(BorderFactory.createTitledBorder("Edit Selected Cell"));
        
        txtEditModel = new JTextField(); txtEditModel.setEditable(false); 
        txtEditPrice = new JTextField();
        txtEditQty = new JTextField();
        txtEditOutlet = new JTextField(); txtEditOutlet.setEditable(false); 
        
        JButton btnUpdateStock = new JButton("SAVE CHANGES");
        btnUpdateStock.setBackground(new Color(144, 238, 144)); 

        editPanel.add(new JLabel("Model ID")); 
        editPanel.add(new JLabel("Unit Price (RM)")); 
        editPanel.add(new JLabel("Outlet (Selected)"));
        editPanel.add(new JLabel("Quantity")); 
        editPanel.add(new JLabel("Action"));
        
        editPanel.add(txtEditModel); 
        editPanel.add(txtEditPrice); 
        editPanel.add(txtEditOutlet); 
        editPanel.add(txtEditQty); 
        editPanel.add(btnUpdateStock);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(editPanel, BorderLayout.SOUTH);

        // --- LISTENERS ---
        
        cmbModelSelect.addActionListener(e -> refreshStockTable());

        btnRefresh.addActionListener(e -> {
            loadStockData();
            populateModelDropdown();
            refreshStockTable();
        });

        // SMART SELECTION: Check which column was clicked
        stockTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = stockTable.rowAtPoint(evt.getPoint());
                int col = stockTable.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    handleStockTableClick(row, col);
                }
            }
        });

        btnUpdateStock.addActionListener(e -> updateStockFile());

        return panel;
    }

    // =================================================================
    // LOGIC: STOCK READING (Based on model.csv format)
    // =================================================================
    
    private void loadStockData() {
        stockDataCache.clear();
        stockHeaders.clear();
        
        File file = new File(STOCK_FILE);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "model.csv not found!");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line != null) {
                // Header: Model, Price, C60, C61, ...
                String[] headers = line.split(",");
                for (String h : headers) stockHeaders.add(h.trim());
            }

            while ((line = br.readLine()) != null) {
                if(line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                // Ensure array length matches header length to avoid IndexOutOfBounds
                // If CSV line is shorter (e.g. empty trailing commas), pad it
                if (parts.length < stockHeaders.size()) {
                    String[] padded = new String[stockHeaders.size()];
                    System.arraycopy(parts, 0, padded, 0, parts.length);
                    for(int i=parts.length; i<stockHeaders.size(); i++) padded[i] = "0"; // Default to 0
                    stockDataCache.add(padded);
                } else {
                    stockDataCache.add(parts);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void refreshStockTable() {
        // 1. Set Columns from Header
        Vector<String> columnNames = new Vector<>(stockHeaders);
        
        // 2. Build Rows
        Vector<Vector<Object>> dataVector = new Vector<>();
        String filterModel = (String) cmbModelSelect.getSelectedItem();

        for (String[] row : stockDataCache) {
            // Row[0] is Model
            if (filterModel != null && !filterModel.equals("All Models") && !row[0].equals(filterModel)) {
                continue;
            }
            
            Vector<Object> vec = new Vector<>();
            for(String s : row) vec.add(s);
            dataVector.add(vec);
        }

        stockModel.setDataVector(dataVector, columnNames);
        
        // Adjust column widths for better view
        if (stockTable.getColumnCount() > 0) {
            stockTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Model
            stockTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Price
            // Outlet columns
            for(int i=2; i<stockTable.getColumnCount(); i++) {
                stockTable.getColumnModel().getColumn(i).setPreferredWidth(50);
            }
        }
    }

    private void handleStockTableClick(int row, int col) {
        // Data Structure: [0]Model, [1]Price, [2...] Outlets
        if (row >= stockTable.getRowCount()) return;

        String modelId = stockTable.getValueAt(row, 0).toString();
        String price = stockTable.getValueAt(row, 1).toString();
        
        txtEditModel.setText(modelId);
        txtEditPrice.setText(price);

        String colName = stockTable.getColumnName(col);
        
        // If user clicked Model or Price
        if (col == 0 || col == 1) {
            txtEditOutlet.setText("-");
            txtEditQty.setText("-");
            txtEditQty.setEditable(false);
            txtEditPrice.setEditable(true); // Allow price edit
        } else {
            // User clicked an Outlet Column
            txtEditOutlet.setText(colName);
            Object val = stockTable.getValueAt(row, col);
            txtEditQty.setText(val == null ? "0" : val.toString());
            txtEditQty.setEditable(true);
            txtEditPrice.setEditable(false); // Lock price when editing qty to prevent mistakes
        }
    }

    private void updateStockFile() {
        String targetModel = txtEditModel.getText();
        String targetOutlet = txtEditOutlet.getText();
        String newQty = txtEditQty.getText();
        String newPrice = txtEditPrice.getText();

        if (targetModel.isEmpty()) return;

        boolean updated = false;

        for (String[] row : stockDataCache) {
            if (row[0].equals(targetModel)) {
                // Update Price
                row[1] = newPrice;
                
                // Update Quantity if an outlet is selected
                if (!targetOutlet.equals("-")) {
                    // Find which index this outlet corresponds to
                    int outletIndex = stockHeaders.indexOf(targetOutlet);
                    if (outletIndex != -1) {
                        row[outletIndex] = newQty;
                    }
                }
                updated = true;
                break;
            }
        }

        if (updated) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(STOCK_FILE))) {
                // Write Header
                bw.write(String.join(",", stockHeaders));
                bw.newLine();
                
                // Write Data
                for (String[] row : stockDataCache) {
                    bw.write(String.join(",", row));
                    bw.newLine();
                }
                JOptionPane.showMessageDialog(this, "Stock Updated!");
                refreshStockTable();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving: " + e.getMessage());
            }
        }
    }

    private void populateModelDropdown() {
        cmbModelSelect.removeAllItems();
        cmbModelSelect.addItem("All Models");
        for(String[] row : stockDataCache) {
            cmbModelSelect.addItem(row[0]);
        }
    }
    
    // =================================================================
    // TAB 2: SALES MANAGEMENT (Standard)
    // =================================================================
    private JPanel createSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSalesSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search Customer");
        JButton btnRefresh = new JButton("Show All");

        searchPanel.add(new JLabel("Customer Name:"));
        searchPanel.add(txtSalesSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        String[] columns = {"Ref/Date", "Customer", "Model", "Qty", "Total", "Method", "Staff"};
        salesModel = new DefaultTableModel(columns, 0);
        salesTable = new JTable(salesModel);
        JScrollPane scrollPane = new JScrollPane(salesTable);

        JPanel editPanel = new JPanel(new GridLayout(2, 8, 5, 5));
        editPanel.setBorder(BorderFactory.createTitledBorder("Edit Sale"));

        txtEditDate = new JTextField(); txtEditDate.setEditable(false);
        txtEditCust = new JTextField(); txtEditItem = new JTextField();
        txtEditSaleQty = new JTextField(); txtEditTotal = new JTextField();
        txtEditPay = new JTextField(); txtEditStaff = new JTextField();
        JButton btnUpdateSale = new JButton("UPDATE");
        btnUpdateSale.setBackground(new Color(135, 206, 250));

        editPanel.add(new JLabel("Ref")); editPanel.add(new JLabel("Customer")); editPanel.add(new JLabel("Model")); 
        editPanel.add(new JLabel("Qty")); editPanel.add(new JLabel("Total")); editPanel.add(new JLabel("Method")); editPanel.add(new JLabel("Staff")); editPanel.add(new JLabel("Action"));
        
        editPanel.add(txtEditDate); editPanel.add(txtEditCust); editPanel.add(txtEditItem); 
        editPanel.add(txtEditSaleQty); editPanel.add(txtEditTotal); editPanel.add(txtEditPay); editPanel.add(txtEditStaff); editPanel.add(btnUpdateSale);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(editPanel, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> refreshSalesData(txtSalesSearch.getText().trim()));
        btnRefresh.addActionListener(e -> { txtSalesSearch.setText(""); refreshSalesData(""); });

        salesTable.getSelectionModel().addListSelectionListener(e -> {
            int row = salesTable.getSelectedRow();
            if (row != -1) {
                txtEditDate.setText(salesModel.getValueAt(row, 0).toString());
                txtEditCust.setText(salesModel.getValueAt(row, 1).toString());
                txtEditItem.setText(salesModel.getValueAt(row, 2).toString());
                txtEditSaleQty.setText(salesModel.getValueAt(row, 3).toString());
                txtEditTotal.setText(salesModel.getValueAt(row, 4).toString());
                txtEditPay.setText(salesModel.getValueAt(row, 5).toString());
                txtEditStaff.setText(salesModel.getValueAt(row, 6).toString());
            }
        });

        btnUpdateSale.addActionListener(e -> updateSalesFile());
        return panel;
    }

    private void refreshSalesData(String query) {
        salesModel.setRowCount(0);
        salesDataCache.clear();
        File file = new File(SALES_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); 
            if (line != null) salesDataCache.add(line.split(",")); 

            while ((line = br.readLine()) != null) {
                if(line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                String ref, cust, model, qty, total, pay, staff;
                if (parts.length >= 8) {
                    ref = parts[0]; cust = parts[2]; model = parts[3]; qty = parts[4]; 
                    total = parts[5]; pay = parts[6]; staff = parts[7];
                } else if (parts.length >= 7) {
                    ref = parts[0]; cust = parts[1]; model = parts[2]; qty = parts[3];
                    total = parts[4]; pay = parts[5]; staff = parts[6];
                } else { continue; }

                salesDataCache.add(parts);
                if (query.isEmpty() || cust.toLowerCase().contains(query.toLowerCase())) {
                    salesModel.addRow(new Object[]{ref, cust, model, qty, total, pay, staff});
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateSalesFile() {
        if (txtEditDate.getText().isEmpty()) return;
        String targetRef = txtEditDate.getText();
        List<String> newLines = new ArrayList<>();
        boolean headerSkipped = false;
        for (String[] row : salesDataCache) {
            if (!headerSkipped && (row[0].equalsIgnoreCase("RefNo") || row[0].equalsIgnoreCase("Date"))) {
                newLines.add(String.join(",", row));
                headerSkipped = true;
                continue;
            }
            if (row[0].equals(targetRef)) {
                if (row.length >= 8) {
                    row[2] = txtEditCust.getText(); row[3] = txtEditItem.getText();
                    row[4] = txtEditSaleQty.getText(); row[5] = txtEditTotal.getText();
                    row[6] = txtEditPay.getText(); row[7] = txtEditStaff.getText();
                } else {
                    row[1] = txtEditCust.getText(); row[2] = txtEditItem.getText();
                    row[3] = txtEditSaleQty.getText(); row[4] = txtEditTotal.getText();
                    row[5] = txtEditPay.getText(); row[6] = txtEditStaff.getText();
                }
            }
            newLines.add(String.join(",", row));
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SALES_FILE))) {
            for (String line : newLines) { bw.write(line); bw.newLine(); }
            JOptionPane.showMessageDialog(this, "Sale Record Updated!");
            refreshSalesData(txtSalesSearch.getText());
        } catch (IOException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SearchEditGUI().setVisible(true));
    }
}