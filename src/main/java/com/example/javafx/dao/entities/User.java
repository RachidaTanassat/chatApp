package com.example.javafx.dao.entities;

import java.io.Serializable;

public class User implements Serializable {

    private String user_id;
    private String nom;
    private String password;
    private String email;

    public User() {
    }

    public User(String user_id, String nom, String password, String email) {
        this.user_id = user_id;
        this.nom = nom;
        this.password = password;
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
