package com.example.javafx.dao;

import com.example.javafx.dao.entities.User;
import org.bson.conversions.Bson;

import java.util.List;

public interface UserDao extends Dao<User, String>{
    List<User> searchProductByQuery(String query);
    User login(String email, String password);

    Boolean emailExists(String email);
    void updateImage(String userId, byte[] newImageData);


}
