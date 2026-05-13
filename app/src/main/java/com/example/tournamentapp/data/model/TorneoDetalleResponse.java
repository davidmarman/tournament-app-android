package com.example.tournamentapp.data.model;
import java.util.List;

public class TorneoDetalleResponse {

    public TorneoInfo info;
    public List<ClasificacionItem> clasificacion;
    public int jornada_actual;
    public int max_jornadas;
    public List<PartidoItem> partidos;

    // Sub-clase para la cabecera
    public static class TorneoInfo {
        public int id;
        public String nombre;
        public String logo;
        public String descripcion;
        public String codigo;
        public String estado;
    }
}