package com.mycompany.aidahtestproject;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.List;

public class SalesAnalytics extends JFrame {

    private final Employee currentUser;
    
    // Data Structures for Analytics
    // Using TreeMap to keep dates sorted automatically
    private final Map<String, Double> salesByDay = new TreeMap<>();   
    private final Map<String, Double> salesByMonth = new TreeMap<>(); 
    private final Map<String, Double> salesByYear = new TreeMap<>();  
    
    private final Map<String, Map<String, Integer>> productsByMonth = new TreeMap<>();
    private final Map<String, Map<String, Integer>> productsByYear = new TreeMap<>();

    private JTabbedPane tabbedPane;

    public SalesAnalytics(Employee user) {
        this.currentUser = user;
        
        setTitle("Business Analytics Dashboard");
        // 1. ADJUSTMENT: Increased default window size for better visibility
        setSize(1100, 750); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Load Data & Setup GUI
        loadSalesData();
        initGUI();
    }
    
    public SalesAnalytics() {
        this(new Employee("ADMIN", "Admin", "Manager", "pass"));
    }

    private void initGUI() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        tabbedPane.addTab("📄 Summary Report", createSummaryPanel());
        tabbedPane.addTab("📅 Daily Sales Chart", createChartWrapper(salesByDay, "Daily Revenue (RM)"));
        tabbedPane.addTab("📊 Monthly Sales Chart", createChartWrapper(salesByMonth, "Monthly Revenue (RM)"));
        tabbedPane.addTab("📈 Yearly Sales Chart", createChartWrapper(salesByYear, "Yearly Revenue (RM)"));
        tabbedPane.addTab("🏆 Best Sellers Analysis", createBestSellersPanel());

        add(tabbedPane, BorderLayout.CENTER);

        JButton btnBack = new JButton("⬅ Back to Dashboard");
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setPreferredSize(new Dimension(100, 40));
        btnBack.addActionListener(e -> {
            this.dispose();
            if (currentUser != null) new MainDashboard(currentUser).setVisible(true);
        });
        add(btnBack, BorderLayout.SOUTH);
    }

    // ================= DATA LOADING (Unchanged) =================
    private void loadSalesData() {
        File file = new File("sales.csv");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Skip Header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                String dateStr = "", model = "";
                int qty = 0; double total = 0.0;

                if (data.length >= 8) { dateStr = data[1]; model = data[3]; qty = Integer.parseInt(data[4]); total = Double.parseDouble(data[5]); }
                else if (data.length == 7) { dateStr = data[0]; model = data[2]; qty = Integer.parseInt(data[3]); total = Double.parseDouble(data[4]); }
                else { continue; }

                String dayKey = dateStr.length() >= 10 ? dateStr.substring(0, 10) : "Unknown";
                String monthKey = dateStr.length() >= 7 ? dateStr.substring(0, 7) : "Unknown";
                String yearKey = dateStr.length() >= 4 ? dateStr.substring(0, 4) : "Unknown";

                salesByDay.put(dayKey, salesByDay.getOrDefault(dayKey, 0.0) + total);
                salesByMonth.put(monthKey, salesByMonth.getOrDefault(monthKey, 0.0) + total);
                salesByYear.put(yearKey, salesByYear.getOrDefault(yearKey, 0.0) + total);

                productsByMonth.computeIfAbsent(monthKey, k -> new HashMap<>()).merge(model, qty, Integer::sum);
                productsByYear.computeIfAbsent(yearKey, k -> new HashMap<>()).merge(model, qty, Integer::sum);
            }
        } catch (Exception e) { System.out.println("Error loading analytics: " + e.getMessage()); }
    }

    // ================= GUI PANELS =================

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 15));
        area.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        
        StringBuilder sb = new StringBuilder();
        sb.append("GENERAL SALES SUMMARY\n");
        sb.append("================================\n\n");
        double grandTotal = salesByYear.values().stream().mapToDouble(Double::doubleValue).sum();
        sb.append(String.format("Total Lifetime Revenue:  RM %,.2f\n\n", grandTotal));
        sb.append("Total Active Trading Days: " + salesByDay.size() + "\n");
        sb.append("Total Active Months:       " + salesByMonth.size() + "\n");
        sb.append("Total Active Years:        " + salesByYear.size() + "\n");
        
        area.setText(sb.toString());
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    // 2. ADJUSTMENT: Calculate required width dynamically based on data size
    private JScrollPane createChartWrapper(Map<String, Double> data, String yAxisLabel) {
        SimpleBarChart chart = new SimpleBarChart(data, yAxisLabel);
        
        // Calculate width: Give 100 pixels per data bar + 150px padding for axes
        // Ensure it's at least the width of the window (1050) so small charts don't look weird.
        int requiredWidth = Math.max(1050, (data.size() * 100) + 150);
        
        // Set preferred size so JScrollPane knows when to scroll
        chart.setPreferredSize(new Dimension(requiredWidth, 600));
        
        JScrollPane scrollPane = new JScrollPane(chart);
        // Speed up scrolling slightly
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        return scrollPane;
    }

    private JPanel createBestSellersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        StringBuilder sb = new StringBuilder();
        sb.append("🏆 MOST SOLD PRODUCTS BY MONTH\n");
        sb.append("===================================\n");
        productsByMonth.forEach((month, products) -> sb.append(String.format("📅 %s : %s\n", month, getBestProduct(products))));

        sb.append("\n\n🏆 MOST SOLD PRODUCTS BY YEAR\n");
        sb.append("===================================\n");
        productsByYear.forEach((year, products) -> sb.append(String.format("📅 %s : %s\n", year, getBestProduct(products))));

        area.setText(sb.toString());
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private String getBestProduct(Map<String, Integer> map) {
        return map.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey() + " (" + entry.getValue() + " units)")
                .orElse("None");
    }

    // ================= CUSTOM CHART COMPONENT (UPDATED) =================
    static class SimpleBarChart extends JPanel {
        private final Map<String, Double> data;
        private final String yLabel;
        // Define fixed sizes for consistent look, ScrollPane handles the rest
        final int BAR_WIDTH = 50;
        final int GAP = 50; // More space between bars
        // Increase bottom padding significantly for rotated labels
        final int PAD_TOP = 50, PAD_LEFT = 80, PAD_BOTTOM = 120, PAD_RIGHT = 50;


        public SimpleBarChart(Map<String, Double> data, String yLabel) {
            this.data = data;
            this.yLabel = yLabel;
            setBackground(Color.WHITE);
            setFont(new Font("SansSerif", Font.PLAIN, 11));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int graphHeight = height - PAD_TOP - PAD_BOTTOM;
            
            if (data.isEmpty()) {
                 g2.drawString("No data available for chart.", width/2 - 50, height/2); return;
            }

            double maxValue = data.values().stream().max(Double::compare).orElse(100.0);
            if(maxValue == 0) maxValue = 100; // Prevent division by zero

            // Draw Axes lines
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(PAD_LEFT, height - PAD_BOTTOM, width - PAD_RIGHT, height - PAD_BOTTOM); // X Axis
            g2.drawLine(PAD_LEFT, height - PAD_BOTTOM, PAD_LEFT, PAD_TOP); // Y Axis
            
            // Draw Y-Axis Title
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString(yLabel, PAD_LEFT - 20, PAD_TOP - 20);
            // Draw Max Value Marker on Y axis
            g2.drawString(String.format("RM %.0f", maxValue), 5, PAD_TOP + 5);

            // Draw Bars and Labels
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            int x = PAD_LEFT + GAP;

            AffineTransform originalTransform = g2.getTransform();

            for (Map.Entry<String, Double> entry : data.entrySet()) {
                String labelKey = entry.getKey();
                double value = entry.getValue();

                int barHeight = (int) ((value / maxValue) * graphHeight);
                int y = (height - PAD_BOTTOM) - barHeight;

                // Draw Bar
                g2.setColor(new Color(70, 130, 180)); // Steel Blue
                g2.fillRect(x, y, BAR_WIDTH, barHeight);
                g2.setColor(Color.DARK_GRAY);
                g2.drawRect(x, y, BAR_WIDTH, barHeight);

                // Draw Value on top of bar
                g2.setColor(Color.BLACK);
                String valueStr = String.format("%.0f", value);
                int valWidth = g2.getFontMetrics().stringWidth(valueStr);
                g2.drawString(valueStr, x + (BAR_WIDTH/2) - (valWidth/2), y - 5);

                // --- 3. ADJUSTMENT: ROTATE X-AXIS LABELS ---
                // Calculate center point below the bar for rotation
                double labelX = x + (BAR_WIDTH / 2.0);
                double labelY = height - PAD_BOTTOM + 20; // Start drawing below axis

                // Rotate -90 degrees (counter-clockwise) relative to the label position
                AffineTransform rotated = AffineTransform.getRotateInstance(Math.toRadians(-90), labelX, labelY);
                g2.setTransform(rotated);
                
                // Draw label (it will draw upwards now)
                g2.drawString(labelKey, (int)labelX, (int)labelY);
                
                // Restore original rotation for next iteration
                g2.setTransform(originalTransform);

                x += BAR_WIDTH + GAP;
            }
        }
    }
}