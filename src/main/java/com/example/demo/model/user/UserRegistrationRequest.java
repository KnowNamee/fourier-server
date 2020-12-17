package com.example.demo.model.user;

import com.sun.istack.NotNull;

public class UserRegistrationRequest {

    private String email;
    private String username;
    private String password;

    public UserRegistrationRequest() { }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
