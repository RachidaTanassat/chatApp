package com.example.javafx.service;

import com.example.javafx.dao.entities.Message;
import com.example.javafx.dao.entities.User;

import java.util.List;

public interface IUserService {

    public void addUser(User user);
    public void deleteUserById(String id);
    public List<User> getAllUsers();
    public void updateUser(User user);
    public List<User> searchUserByQuery(String query);
    public User login(String email, String password);
    public Boolean emailExists(String email);
}
