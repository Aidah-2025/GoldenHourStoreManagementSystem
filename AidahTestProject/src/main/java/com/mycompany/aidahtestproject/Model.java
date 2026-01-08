/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aidahtestproject;

/**
 *
 * @author nuraidahmaisarahbintiazeman
 */
import java.util.Arrays;

public class Model {
    private String modelId;
    private double price; // Changed to double for RM/Currency
    private int[] stockQuantity;
    
    public Model (String modelId, double price, int[] stockQuantity){
        this.modelId = modelId;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
    
    // Getters
    public String getModelId (){ return modelId; }
    public double getPrice (){ return price; }
    public int[] getStockQuantity (){ return stockQuantity; }
    
    // Fixed Setter: Now accepts an array to actually update stock
    public void setStockQuantity (int[] stockQuantity){
        this.stockQuantity = stockQuantity;
    }

    // Helper for GUI Tables: Returns stock as a simple string
    public String getStockDisplay() {
        return Arrays.toString(stockQuantity).replace("[", "").replace("]", "");
    }
    
    @Override
    public String toString (){
        return String.format("%s | Price: RM%.2f | Stock: %s", 
            modelId, price, Arrays.toString(stockQuantity));
    }
}
