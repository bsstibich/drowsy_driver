package com.example.drowsy_driver;

public class Account {
    private String email;
    private String userName;
    private String fullName;
    private String password;
    private String vehicleInfo;
    public Account(){}
    public Account(String email, String fullName, String password, String vehicleInfo) {
        this.email = email;
        //this.userName = userName;
        this.fullName = fullName;
        this.password = password;
        this.vehicleInfo = vehicleInfo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(String vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }
}