package com.example.tournamentapp.data.model;
import java.util.List;

public class FinalizarPartidoRequest {
    public int goles_local;
    public int goles_visitante;
    public List<EventoJugador> eventos;

    public FinalizarPartidoRequest(int goles_local, int goles_visitante, List<EventoJugador> eventos) {
        this.goles_local = goles_local;
        this.goles_visitante = goles_visitante;
        this.eventos = eventos;
    }
}