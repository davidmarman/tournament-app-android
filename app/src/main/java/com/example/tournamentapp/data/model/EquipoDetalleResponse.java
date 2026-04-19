package com.example.tournamentapp.data.model;

import java.util.List;

public class EquipoDetalleResponse {
    public int id;
    public String nombre;
    public String logo;
    public boolean es_capitan;
    public ProximoPartido proximo_partido;
    public List<ItemSimple> torneos;
    public List<String> palmares;

    public static class ProximoPartido {
        public String rival_nombre;
        public String rival_logo;
        public String torneo_nombre;
        public String fecha;
    }

    public static class TorneoSimple {
        public int id;
        public String nombre;
    }
}
