package edu.eci.proyecto.dto;

public class Userdto {
    private String name;
    private String email;

    public Userdto(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
