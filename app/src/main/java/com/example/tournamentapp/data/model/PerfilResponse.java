package com.example.tournamentapp.data.model;

import java.util.List;

public class PerfilResponse {
    public int id;
    public String nombre;
    public String apellido; // ¡AÑADIDO!
    public String username;
    public String imagen; // ¡UNIFICADO EL NOMBRE!
    public List<ItemSimple> equipos;
    public List<ItemSimple> torneos;
    public Stats stats;

    public static class Stats {
        public int goles;
        public int amarillas;
        public int rojas;
    }
}