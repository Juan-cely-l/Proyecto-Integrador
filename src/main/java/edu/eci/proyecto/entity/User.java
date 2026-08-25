package edu.eci.proyecto.entity;



import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection = "users")
public class User {
    @Id
    private UUID id;
    private String name;
    @Field("email_address")
    private String email;
    private String password;

    public User(){}

    public User(UUID id , String name , String email,String password){
        this.id=id;
        this.name=name;
        this.email=email;
        this.password=password;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
