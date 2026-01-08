/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aidahtestproject;

/**
 *
 * @author nuraidahmaisarahbintiazeman
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class SaleReader {
    /**
     * Loads all sales from the CSV file.Robust enough to handle empty lines and skip malformed data.
     * @return
     */
    public static ArrayList<Sale> loadSales() {
        ArrayList<Sale> sales = new ArrayList<>();
        File file = new File("sales.csv");

        // Safety check: Return empty list if file doesn't exist yet
        if (!file.exists()) {
            return sales;
        }

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                
                // Skip empty lines to prevent errors
                if (line.isEmpty()) continue;

                String[] data = line.split(",");
                
                // Check if the row has the expected number of columns (7)
                if (data.length < 7) {
                    System.err.println("Skipping incomplete row: " + line);
                    continue;
                }

                try {
                    String date = data[0];
                    String customerName = data[1];
                    String modelId = data[2];
                    int quantity = Integer.parseInt(data[3].trim());
                    double totalPrice = Double.parseDouble(data[4].trim());
                    String transactionMethod = data[5];
                    String employeeName = data[6];

                    sales.add(new Sale(date, customerName, modelId, quantity, totalPrice, transactionMethod, employeeName));
                } catch (NumberFormatException e) {
                    System.err.println("Skipping row with invalid numbers: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading sales.csv: " + e.getMessage());
        }
        
        return sales;
    }
}
