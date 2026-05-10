package com.example.tournamentapp.data.model;

public class JugadorActa {
    public int id_usuario;
    public String nombre;
    public String imagen;

    // Variables locales para que Android cuente en pantalla (no vienen de Flask)
    public int goles = 0;
    public int amarillas = 0;
    public int rojas = 0;
}