package com.example.javafx.dao;

import com.example.javafx.dao.entities.Message;
import com.example.javafx.dao.entities.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import static com.mongodb.client.model.Filters.eq;

public class UserDaoImpl implements UserDao{

    MongoCollection<Document> collection = DBConnection.getDatabase().getCollection("users");

    @Override
    public void save(User o) {
        UUID id = UUID.randomUUID();
        String userId = id.toString();
        String hashedPassword = BCrypt.hashpw(o.getPassword(), BCrypt.gensalt());


        Document user = new Document("_id", new ObjectId())
                .append("user_id", userId)
                .append("nom", o.getNom())
                .append("password", hashedPassword)
                .append("email", o.getEmail());


        collection.insertOne(user);
    }

    @Override
    public void removeById(String id) {
        Bson query = eq("user_id", id);
        collection.deleteOne(query);
    }

    @Override
    public User getById(String id) {
        User user = null;

        Document doc = collection.find(Filters.eq("user_id", id)).first();
        if(doc != null){
            user = new User();
            user.setUser_id(doc.getString("user_id"));
            user.setNom(doc.getString("nom"));
            user.setPassword(doc.getString("password"));
            user.setEmail(doc.getString("email"));

        }

        return user;
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        try(MongoCursor<Document> cursor = collection.find().iterator())
        {
            while(cursor.hasNext()) {
                Document contenu = cursor.next();
                User user = new User();
                user.setUser_id(contenu.getString("user_id"));
                user.setNom(contenu.getString("nom"));
                user.setPassword(contenu.getString("password"));
                user.setEmail(contenu.getString("email"));


                users.add(user);
            }
        }
        return users;
    }

    @Override
    public void update(User o) {
        Bson query  = Filters.eq("user_id", o.getUser_id());
        Bson updates  = Updates.combine(
                Updates.set("nom", o.getNom()),
                Updates.set("password", o.getPassword()),
                Updates.set("email", o.getEmail()));

        collection.updateOne(query, updates);

    }

    @Override
    public List<User> searchProductByQuery(String query) {
        return null;
    }

    @Override
    public User login(String email, String password) {
        User user = null;

        Document doc = collection.find(Filters.and(
                Filters.eq("email", email),
                Filters.eq("password", password)
        )).first();
        if(doc != null){
            user = new User();
            user.setUser_id(doc.getString("user_id"));
            user.setNom(doc.getString("nom"));
            user.setPassword(doc.getString("password"));
            user.setEmail(doc.getString("email"));

        }

        return user;
    }

    @Override
    public Boolean emailExists(String email) {


        Document doc = collection.find(Filters.eq("email", email)).first();
        if(doc != null) return true;
        else return false;


    }
}
