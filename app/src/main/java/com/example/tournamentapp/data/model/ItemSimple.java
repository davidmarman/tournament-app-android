package com.example.tournamentapp.data.model;

public class ItemSimple {
    public int id;
    public String nombre;
    public String logo; // Opcional, para que sirva tanto para torneos como equipos
    public String codigo; // Solo sirve para torneos

    // Constructor vacío para Retrofit
    public ItemSimple() {}

    // Constructor para mapeo rápido
    public ItemSimple(int id, String nombre, String logo) {
        this.id = id;
        this.nombre = nombre;
        this.logo = logo;
    }
}