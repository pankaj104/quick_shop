package com.example.zeroq;

import java.util.ArrayList;

public class DataStorage {
String name, Email,password,purchase;



    public DataStorage(String name, String Email, String password,String purchase) {
        this.Email=Email;
        this.password=password;
        this.name=name;
        this.purchase=purchase;
    }

    public String getPurchase() {
        return purchase;
    }

    public void setPurchase(String purchase) {
        this.purchase = purchase;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
