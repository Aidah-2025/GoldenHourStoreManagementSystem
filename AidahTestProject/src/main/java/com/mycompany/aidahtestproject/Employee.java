
package com.mycompany.aidahtestproject;

public class Employee {
    
    private final String id;
    private String name;
    private String role;
    private String password;
    
    public Employee (String employeeId, String EmployeeName, String Role, String Password){
        this.id = employeeId;
        this.name = EmployeeName;
        this.role = Role;
        this.password = Password;
    }
    
    public String getId (){
        return id;
    }
    
    public String getName (){
        return name;
    }
    
    public String getRole (){
        return role;
    }
    
    public String getPassword (){
        return password;
    }
    
    //Setters (update private fields if needed)
    public void setName (String name){
        this.name = name;
    }
    
    public void setRole (String role){
        this.role = role;
    }
    
    public void setPassword (String password){
        this.password = password;
    }
    
    @Override
    public String toString (){
        return id + "-" + name + " (" + role + ")";
    }
}

