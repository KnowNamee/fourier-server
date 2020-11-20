package com.example.demo.model.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

public class User {

    private Long id;
    private String email;
    private String username;
    private String password;
    private Set<SimpleGrantedAuthority> authorities;

    public User(String email, String username, String password,
                Set<SimpleGrantedAuthority> authorities) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<SimpleGrantedAuthority> authorities) {
        this.authorities = authorities;
    }

}
