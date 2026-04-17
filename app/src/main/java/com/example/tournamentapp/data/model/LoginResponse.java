package com.example.tournamentapp.data.model;

public class LoginResponse {
    private String mensaje;
    private String token;
    private String rol;
    private int id_usuario;

    // Getters
    public String getMensaje() { return mensaje; }
    public String getToken() { return token; }
    public String getRol() { return rol; }
    public int getIdUsuario() { return id_usuario; }
}
