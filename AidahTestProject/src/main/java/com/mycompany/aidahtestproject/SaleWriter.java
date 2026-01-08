package com.mycompany.aidahtestproject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SaleWriter {
    
    private static final String FILE_NAME = "sales.csv";

    public static void saveSale(Sale sale) {
        File file = new File(FILE_NAME);
        boolean fileExists = file.exists();

        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {

            // 1. If file didn't exist, write the NEW header with Reference No
            if (!fileExists) {
                pw.println("ReferenceNo,Date,Customer,Model,Quantity,TotalPrice,PaymentMethod,Staff");
            }

            // 2. Format the data line (Reference No is first)
            // We use String.format to ensure safe comma separation
            String line = String.format("%s,%s,%s,%s,%d,%.2f,%s,%s",
                    sale.getReferenceNo(), // New Field
                    sale.getDate(),
                    sale.getCustomerName(),
                    sale.getModelId(),
                    sale.getQuantity(),
                    sale.getTotalPrice(),
                    sale.getTransactionMethod(),
                    sale.getEmployeeName()
            );

            // 3. Write to file
            pw.println(line);
            
            System.out.println("Sale saved successfully: " + sale.getReferenceNo());

        } catch (IOException e) {
            System.err.println("Error saving sale: " + e.getMessage());
        }
    }
}