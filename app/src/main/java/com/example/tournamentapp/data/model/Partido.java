package com.example.tournamentapp.data.model;

public class Partido {
    private String equipoLocal;
    private String equipoVisitante;
    private String nombreTorneo;
    private String fecha;

    public Partido(String equipoLocal, String equipoVisitante, String nombreTorneo, String fecha) {
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.nombreTorneo = nombreTorneo;
        this.fecha = fecha;
    }

    // Getters
    public String getEquipoLocal() { return equipoLocal; }
    public String getEquipoVisitante() { return equipoVisitante; }
    public String getNombreTorneo() { return nombreTorneo; }
    public String getFecha() { return fecha; }
}
