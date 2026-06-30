package com.proyectomsw.core;

public class Propiedad {
    private int id;
    private int proyectoId;
    private String nombrePropiedad;
    private String expresion;
    private Integer estadoEspecificoId;

    public Propiedad() {
    }

    public Propiedad(int id, int proyectoId, String nombrePropiedad, String expresion, Integer estadoEspecificoId) {
        this.id = id;
        this.proyectoId = proyectoId;
        this.nombrePropiedad = nombrePropiedad;
        this.expresion = expresion;
        this.estadoEspecificoId = estadoEspecificoId;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProyectoId() { return proyectoId; }
    public void setProyectoId(int proyectoId) { this.proyectoId = proyectoId; }

    public String getNombrePropiedad() { return nombrePropiedad; }
    public void setNombrePropiedad(String nombrePropiedad) { this.nombrePropiedad = nombrePropiedad; }

    public String getExpresion() { return expresion; }
    public void setExpresion(String expresion) { this.expresion = expresion; }

    public Integer getEstadoEspecificoId() { return estadoEspecificoId; }
    public void setEstadoEspecificoId(Integer estadoEspecificoId) { this.estadoEspecificoId = estadoEspecificoId; }
}
