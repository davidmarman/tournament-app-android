package com.example.tournamentapp.data.model;

import java.util.List;

public class EquipoDetalleResponse {
    public int id;
    public String nombre;
    public String logo;
    public boolean es_capitan;
    public boolean soy_miembro;
    public ProximoPartido proximo_partido;
    public List<ItemSimple> torneos;
    public List<ItemSimple> jugadores;
    public List<PalmaresItem> palmares;
    public int id_capitan;
    public LiderStats lider_goles;
    public LiderStats lider_amarillas;
    public LiderStats lider_rojas;

    public static class LiderStats {
        public String username;
        public int goles;
        public int amarillas;
        public int rojas;
    }

    public static class ProximoPartido {
        public String rival_nombre;
        public String rival_logo;
        public String torneo_nombre;
        public String fecha;
    }
}