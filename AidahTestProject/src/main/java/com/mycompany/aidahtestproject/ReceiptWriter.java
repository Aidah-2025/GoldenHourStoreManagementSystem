package com.mycompany.aidahtestproject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReceiptWriter {

    public static String saveReceiptToFile(Sale sale) {
        // 1. Get the date part only (yyyy-MM-dd) to be used as filename
        // Assumes sale.getDate() returns format "yyyy-MM-dd HH:mm"
        String dateOnly = sale.getDate().split(" ")[0]; 
        String fileName = "SalesReceipts_" + dateOnly + ".txt";
        
        File file = new File(fileName);

        // 2. Open FileWriter with 'true' to enable APPEND mode
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            
            // Add a separator if the file already exists (not the first receipt of the day)
            if (file.length() > 0) {
                bw.newLine();
                bw.write("------------------------------------------------------------");
                bw.newLine();
                bw.newLine();
            }

            // 3. Write the receipt content
            bw.write(sale.generateReceipt());
            
            return fileName; // Return filename to confirm success
            
        } catch (IOException e) {
            System.err.println("Error saving receipt: " + e.getMessage());
            return null;
        }
    }
}