package com.example.javafx.dao;

import com.example.javafx.dao.entities.Message;

import java.util.List;

public interface MessageDao extends Dao<Message, String>{

    List<Message> searchProductByQuery(String query);
}
