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

public class ModelReader {
    
    /**
     * Loads models from the CSV file.Improved to handle variable stock lengths and prevent crashes.
     * @return
     */
    public static ArrayList<Model> loadModels() {
        ArrayList<Model> models = new ArrayList<>();
        File file = new File("model.csv");

        // Check if file exists first to avoid FileNotFoundException
        if (!file.exists()) {
            System.err.println("Critical Error: model.csv not found!");
            return models; 
        }

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                
                // Skip empty lines
                if (line.isEmpty()) continue;

                String[] data = line.split(",");
                
                try {
                    String modelId = data[0];
                    // Changed to Double.parseDouble to match the new Model class
                    double price = Double.parseDouble(data[1]);

                    // Dynamically calculate stock columns 
                    // This allows you to have more or fewer than 10 outlets
                    int stockCount = data.length - 2;
                    int[] stockQuantity = new int[stockCount];
                    
                    for (int i = 0; i < stockCount; i++) {
                        stockQuantity[i] = Integer.parseInt(data[i + 2].trim());
                    }

                    models.add(new Model(modelId, price, stockQuantity));
                    
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    // Skips only the "bad" line in the CSV instead of crashing the whole load
                    System.err.println("Skipping malformed data row: " + line);
                }
            }
        } catch (IOException e) {
        }
        
        return models;
    }
}