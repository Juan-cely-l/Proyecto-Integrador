package edu.eci.proyecto.entity;

import java.util.UUID;


public class User {
    private UUID id;
    private String name;
    private String email;

    public User(){}

    public User(UUID id , String name , String email){
        this.id=id;
        this.name=name;
        this.email=email;
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
}
