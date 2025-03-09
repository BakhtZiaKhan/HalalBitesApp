package com.example.halalbites.models;

public class Users {
    private String username;
    private String email;
    private String password;
    private String phone; // ✅ Added phone field

    // Constructor
    public Users(String username, String email, String password, String phone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    // Getters
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; } // ✅ Added getter for phone

    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; } // ✅ Added setter for phone
}
