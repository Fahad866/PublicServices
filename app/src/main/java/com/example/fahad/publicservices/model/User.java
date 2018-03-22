package com.example.fahad.publicservices.model;

/**
 * Created by fahad on 03/03/2018 AD.
 */

public class User {

    private String email , password , name , phone , service;

    public User() {
    }

    public User(String email, String password, String name, String phone ,String service) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.service = service;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
