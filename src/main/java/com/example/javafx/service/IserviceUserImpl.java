package com.example.javafx.service;

import com.example.javafx.dao.UserDao;
import com.example.javafx.dao.entities.User;

import java.util.List;

public class IserviceUserImpl implements IUserService{

    UserDao userDao;

    public IserviceUserImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void addUser(User user) {
        userDao.save(user);
    }

    @Override
    public void deleteUserById(String id) {
        userDao.removeById(id);
    }

    @Override
    public User getUserById(String id) {
        return userDao.getById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAll();
    }

    @Override
    public void updateUser(User user) {
     userDao.update(user);
    }

    @Override
    public List<User> searchUserByQuery(String query) {
        return userDao.searchProductByQuery(query);
    }

    @Override
    public User login(String email, String password) {
        return userDao.login(email, password);
    }

    @Override
    public Boolean emailExists(String email) {
        return userDao.emailExists(email);
    }

    @Override
    public void updateImage(String userId, byte[] newImageData) {
        userDao.updateImage(userId, newImageData);
    }


}
