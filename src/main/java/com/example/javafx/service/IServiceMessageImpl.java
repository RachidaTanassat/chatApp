package com.example.javafx.service;

import com.example.javafx.dao.MessageDao;
import com.example.javafx.dao.entities.Message;
import com.example.javafx.dao.entities.User;

import java.util.ArrayList;
import java.util.List;

public class IServiceMessageImpl implements IMessageService{
    MessageDao messageDao;

    public IServiceMessageImpl(MessageDao messageDao) {
        this.messageDao = messageDao;
    }


    @Override
    public void addMessage(Message message) {
        messageDao.save(message);
    }

    @Override
    public void deleteMessageById(String id) {
        messageDao.removeById(id);
    }

    @Override
    public List<Message> getAllMessages() {
        return messageDao.getAll();
    }

    @Override
    public void updateMessage(Message message) {
          messageDao.update(message);
    }

    @Override
    public List<Message> searchMessageByQuery(String query) {
        return messageDao.searchProductByQuery(query);
    }



}
