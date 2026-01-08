package com.mycompany.aidahtestproject;

import java.util.ArrayList;
import java.util.stream.Collectors;

public final class DataManager {
    // Shared data lists
    private ArrayList<Model> inventory;
    private ArrayList<Sale> salesHistory;
    private ArrayList<Employee> employees;

    public DataManager() {
        // Automatically load all data on startup
        refreshAllData();
    }

    /**
     * Re-reads all CSV files. Use this to sync the UI with the files.
     */
    public void refreshAllData() {
        this.inventory = ModelReader.loadModels();
        this.salesHistory = SaleReader.loadSales();
        // Assuming you have an EmployeeReader, otherwise initialize empty
        this.employees = new ArrayList<>(); 
    }

    // --- SEARCH METHODS ---

    public Model findModelById(String id) {
        return inventory.stream()
                .filter(m -> m.getModelId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public ArrayList<Sale> getSalesByCustomer(String customerName) {
        return salesHistory.stream()
                .filter(s -> s.getCustomerName().equalsIgnoreCase(customerName))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // --- PERSISTENCE METHODS ---

    /**
     * Updates a model's stock and immediately saves to CSV.
     * @param modelId
     * @param outletIndex
     * @param newQty
     * @return 
     */
    public boolean updateStock(String modelId, int outletIndex, int newQty) {
        Model m = findModelById(modelId);
        if (m != null) {
            m.getStockQuantity()[outletIndex] = newQty;
            saveInventory();
            return true;
        }
        return false;
    }

    public void saveInventory() {
        // Reuse your SalesSystem.updateStockFile logic here
        SalesSystemGUI.updateStockFile(); 
    }

    // --- GETTERS ---
    public ArrayList<Model> getInventory() { return inventory; }
    public ArrayList<Sale> getSalesHistory() { return salesHistory; }
}

