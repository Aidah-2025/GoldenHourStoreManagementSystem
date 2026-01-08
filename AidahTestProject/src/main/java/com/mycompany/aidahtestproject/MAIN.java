package com.mycompany.aidahtestproject;

import javax.swing.SwingUtilities;

public class MAIN {
    public static void main(String[] args) {
        // We start with the LoginFrame. 
        // Once the user logs in successfully, the LoginFrame 
        // will automatically open the MainDashboard for us.
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}