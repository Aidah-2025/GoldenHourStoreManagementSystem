package com.mycompany.aidahtestproject;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class StoreGUI extends JFrame {
    private DataManager manager;
    private JTextArea outputArea;
    private Color goldColor = new Color(212, 175, 55); 

    // Dropdown components
    private JComboBox<String> comboSearchModel;
    private JComboBox<String> comboUpdateModel;
    private JComboBox<String> comboUpdateOutlet;

    public StoreGUI(DataManager manager) {
        this.manager = manager; 
        
        setTitle("GoldenHour Management System v2.0");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- TOP HEADER ---
        JPanel header = new JPanel();
        header.setBackground(goldColor);
        JLabel title = new JLabel("GOLDEN HOUR PREMIUM WATCHES");
        title.setFont(new Font("Serif", Font.BOLD, 22));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // --- CENTER TABBED INTERFACE ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Stock Management", createStockPanel());
        tabbedPane.addTab("Sales Records", createSalesPanel());
        add(tabbedPane, BorderLayout.CENTER);

        // --- BOTTOM OUTPUT LOG ---
        outputArea = new JTextArea(10, 50);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(outputArea);
        scroll.setBorder(BorderFactory.createTitledBorder("System Feedback & Results"));
        add(scroll, BorderLayout.SOUTH);
    }

    private JPanel createStockPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Get Data for Dropdowns
        String[] modelOptions = loadModelList();
        String[] outletOptions = loadOutletList();

        // --- Left: Search ---
        JPanel searchBox = new JPanel(new FlowLayout());
        searchBox.setBorder(BorderFactory.createTitledBorder("Search Inventory"));
        
        comboSearchModel = new JComboBox<>(modelOptions);
        JButton btnSearch = new JButton("Check Stock");
        
        searchBox.add(new JLabel("Select Model:"));
        searchBox.add(comboSearchModel);
        searchBox.add(btnSearch);

        // --- Right: Edit ---
        JPanel editBox = new JPanel(new GridLayout(4, 2, 5, 5));
        editBox.setBorder(BorderFactory.createTitledBorder("Update Stock Level"));
        
        comboUpdateModel = new JComboBox<>(modelOptions);
        comboUpdateOutlet = new JComboBox<>(outletOptions);
        JTextField txtEQty = new JTextField();
        JButton btnUpdate = new JButton("Update & Save");
        
        editBox.add(new JLabel("Select Model:")); 
        editBox.add(comboUpdateModel);
        
        editBox.add(new JLabel("Select Outlet:")); 
        editBox.add(comboUpdateOutlet);
        
        editBox.add(new JLabel("New Quantity:")); 
        editBox.add(txtEQty);
        
        editBox.add(new JLabel("")); 
        editBox.add(btnUpdate);

        panel.add(searchBox);
        panel.add(editBox);

        // --- Logic ---
        btnSearch.addActionListener(e -> {
            String selectedId = (String) comboSearchModel.getSelectedItem();
            Model m = manager.findModelById(selectedId);
            if (m != null) {
                outputArea.setText("Model Found: " + m.getModelId() + "\nPrice: RM" + m.getPrice());
            } else {
                outputArea.setText("Error: Model '" + selectedId + "' not found.");
            }
        });

        btnUpdate.addActionListener(e -> {
            try {
                String modelId = (String) comboUpdateModel.getSelectedItem();
                // Get the index from the selected outlet string (e.g., "0 - Kuala Lumpur City Centre")
                int index = comboUpdateOutlet.getSelectedIndex();
                int qty = Integer.parseInt(txtEQty.getText().trim());
                
                if (manager.updateStock(modelId, index, qty)) {
                    outputArea.setText("SUCCESS: " + modelId + " updated to " + qty + " units at Outlet " + index + ".");
                } else {
                    outputArea.setText("FAILED: Update operation rejected by DataManager.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for Quantity.");
            }
        });

        return panel;
    }

    // Helper to load Model IDs from model.csv
    private String[] loadModelList() {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("model.csv"))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0) list.add(parts[0]);
            }
        } catch (IOException e) {
            list.add("No Models Found");
        }
        return list.toArray(new String[0]);
    }

    // Helper to load Outlet Names from outlet.csv
    private String[] loadOutletList() {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("outlet.csv"))) {
            br.readLine(); // Skip header
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    // Display index and name for clarity
                    list.add(count + " - " + parts[1]);
                    count++;
                }
            }
        } catch (IOException e) {
            list.add("No Outlets Found");
        }
        return list.toArray(new String[0]);
    }

    private JPanel createSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Sales History Module Ready", SwingConstants.CENTER));
        return panel;
    }
}