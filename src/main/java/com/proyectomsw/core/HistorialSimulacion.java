package com.proyectomsw.core;

public class HistorialSimulacion {
    private int id;
    private int proyectoId;
    private String fechaSimulacion;
    private String logJson;

    public HistorialSimulacion(int proyectoId, String logJson) {
        this.proyectoId = proyectoId;
        this.logJson = logJson;
    }

    public HistorialSimulacion() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProyectoId() { return proyectoId; }
    public void setProyectoId(int proyectoId) { this.proyectoId = proyectoId; }

    public String getFechaSimulacion() { return fechaSimulacion; }
    public void setFechaSimulacion(String fechaSimulacion) { this.fechaSimulacion = fechaSimulacion; }

    public String getLogJson() { return logJson; }
    public void setLogJson(String logJson) { this.logJson = logJson; }
}