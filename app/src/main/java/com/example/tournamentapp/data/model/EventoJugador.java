package com.example.tournamentapp.data.model;

public class EventoJugador {
    public int id_usuario;
    public int goles;
    public int amarillas;
    public int rojas;

    public EventoJugador(int id_usuario, int goles, int amarillas, int rojas) {
        this.id_usuario = id_usuario;
        this.goles = goles;
        this.amarillas = amarillas;
        this.rojas = rojas;
    }
}