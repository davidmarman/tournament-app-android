package com.example.tournamentapp.data.model;

public class RegisterRequest {
    private String nombre;
    private String apellido;
    private String username;
    private String email;
    private String password;
    private String rol;

    public RegisterRequest(String nombre, String apellido, String username, String email, String password, String rol){
        this.nombre = nombre;
        this.apellido = apellido;
        this.username = username;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }
}
