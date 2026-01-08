package com.mycompany.aidahtestproject;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class LoginFrame extends JFrame {
    private JTextField txtID;
    private JPasswordField txtPass;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("C60 System Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("  Employee ID:"));
        txtID = new JTextField();
        add(txtID);

        add(new JLabel("  Password:"));
        txtPass = new JPasswordField();
        add(txtPass);

        btnLogin = new JButton("Login");
        add(new JLabel("")); // Spacer
        add(btnLogin);

        btnLogin.addActionListener(e -> authenticate());
    }

    private void authenticate() {
        String inputID = txtID.getText().trim();
        String inputPass = new String(txtPass.getPassword());

        boolean success = false;
        
        // We will store the full user object here if login succeeds
        Employee loggedInUser = null; 

        try (BufferedReader br = new BufferedReader(new FileReader("employee.csv"))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                
                // Safety check to ensure line has enough columns
                if (data.length < 4) continue; 

                // data[0]=ID, data[1]=Name, data[2]=Role, data[3]=Password
                if (data[0].equals(inputID) && data[3].equals(inputPass)) {
                    success = true;
                    // CREATE THE FULL EMPLOYEE OBJECT
                    loggedInUser = new Employee(data[0], data[1], data[2], data[3]);
                    break;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading employee database.");
            return;
        }

        if (success && loggedInUser != null) {
            JOptionPane.showMessageDialog(this, "Welcome, " + loggedInUser.getName() + " (C60)");
            
            this.dispose(); // Close login window
            
            // PASS THE FULL OBJECT TO DASHBOARD
            new MainDashboard(loggedInUser).setVisible(true);
            
        } else {
            JOptionPane.showMessageDialog(this, "Invalid ID or Password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}