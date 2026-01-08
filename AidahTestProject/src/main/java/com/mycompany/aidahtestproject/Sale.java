package com.mycompany.aidahtestproject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sale {
    
    private final String referenceNo;
    private final String date;
    private final String customerName;
    private final String modelId;
    private int quantity;
    private double totalPrice;
    private String transactionMethod;
    private final String employeeName;
    
    public Sale (String date, String customerName, String modelId, int quantity, double totalPrice, String transactionMethod, String employeeName){
        // Generate a unique Reference No: REF + YYMMDD + HHMMSS (e.g., REF231025143000)
        this.referenceNo = "REF" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        
        this.date = date;
        this.customerName = customerName;
        this.modelId = modelId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.transactionMethod = transactionMethod;
        this.employeeName = employeeName;
    }
    
    // Getters
    public String getReferenceNo() { return referenceNo; }
    public String getDate (){ return date;}
    public String getCustomerName (){ return customerName;}
    public String getModelId (){ return modelId;}
    public int getQuantity (){ return quantity;}
    public double getTotalPrice (){ return totalPrice;}
    public String getTransactionMethod (){ return transactionMethod;}
    public String getEmployeeName (){ return employeeName;}
    
    public Object[] toTableRow() {
        return new Object[] {
            date, 
            customerName, 
            modelId, 
            quantity, 
            String.format("RM%.2f", totalPrice), 
            transactionMethod, 
            employeeName
        };
    }
    
    @Override
    public String toString(){
        return String.format("%s | %s | %s | Qty: %d | RM%.2f | %s | Staff: %s", 
                date, customerName, modelId, quantity, totalPrice, transactionMethod, employeeName);
    }

    // --- UPDATED RECEIPT GENERATION (Better Alignment) ---
    public String generateReceipt() {
        StringBuilder sb = new StringBuilder();
        
        // Define a standard width for the receipt (e.g., 42 chars wide)
        String line = "------------------------------------------\n";
        String doubleLine = "==========================================\n";

        sb.append(doubleLine);
        sb.append("        GOLDEN HOUR PREMIUM WATCHES       \n");
        sb.append("      Kuala Lumpur City Centre (C60)      \n");
        sb.append(doubleLine);
        
        // Metadata Section
        sb.append(String.format("Ref No:  %s\n", referenceNo)); 
        sb.append(String.format("Date:    %s\n", date));
        sb.append(String.format("Staff:   %s\n", employeeName));
        sb.append(String.format("Cust:    %s\n", customerName));
        sb.append(line);
        
        // Header for Items
        // Format: Item Name (Left), Qty (Center), Price (Right)
        sb.append(String.format("%-18s %5s %15s\n", "Item", "Qty", "Amount (RM)"));
        sb.append(line);
        
        // Item Details
        // We calculate unit price for display
        double unitPrice = totalPrice / quantity; 
        
        // If model name is too long, we might want to trim it, 
        // but %-18s will just push the rest to the right if it's huge.
        // Here we assume model IDs are reasonably short.
        sb.append(String.format("%-18s %5d %15.2f\n", modelId, quantity, totalPrice));
        sb.append(String.format("   (@ RM %.2f / unit)\n", unitPrice)); // Show unit price on next line
        
        sb.append(line);
        
        // Totals Section
        sb.append(String.format("TOTAL AMOUNT:        RM %15.2f\n", totalPrice));
        sb.append(String.format("Payment Method:      %18s\n", transactionMethod));
        
        sb.append(doubleLine);
        sb.append("       Thank you for your purchase!       \n");
        sb.append("       Goods sold are not refundable      \n");
        sb.append("              after 7 days.               \n");
        sb.append(doubleLine);
        
        return sb.toString();
    }
}