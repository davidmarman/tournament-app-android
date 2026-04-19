package com.example.tournamentapp.data.model;

import java.util.List;

public class PerfilResponse {
    public String nombre;
    public String username;
    public String imagen;
    public List<ItemSimple> equipos;
    public List<ItemSimple> torneos;
    public Stats stats;

    public static class Stats {
        public int goles;
        public int faltas;
    }
}
