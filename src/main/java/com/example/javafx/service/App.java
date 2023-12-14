package com.example.javafx.service;

import com.example.javafx.dao.MessageDaoImpl;
import com.example.javafx.dao.UserDaoImpl;
import com.example.javafx.dao.entities.Message;
import com.example.javafx.dao.entities.User;

import java.util.List;

public class App {

    public static void main(String[] args) {
        IMessageService service = new IServiceMessageImpl(new MessageDaoImpl());

        IUserService userService = new IserviceUserImpl(new UserDaoImpl());
      /*  Message message=new Message();
        message.setContent("Hello rachida");
        message.setSender("rechida");
        message.setReceiver("Aafaf");

        service.addMessage(message);*/

        /*List<Message> messages = service.getAllMessages();
        for(Message msg:messages){
            System.out.println(msg.getContent());
        }
        User user=new User();
        user.setNom("Hello ");
        user.setPassword("0000");
        user.setEmail("hello@gmail.com");

        userService.addUser(user);*/

        User user1 = userService.login("rachida@gmail.com", "1234");
        System.out.println(user1);
    }
}
