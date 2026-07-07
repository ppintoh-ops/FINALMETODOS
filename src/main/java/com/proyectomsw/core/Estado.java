package com.proyectomsw.core;

public class Estado {
    private int id;
    private int proyectoId;
    private String nombre;
    private String descripcion;
    private boolean esInicial;
    private String propiedadesJson;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProyectoId() { return proyectoId; }
    public void setProyectoId(int proyectoId) { this.proyectoId = proyectoId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isEsInicial() { return esInicial; }
    public void setEsInicial(boolean esInicial) { this.esInicial = esInicial; }

    public String getPropiedadesJson() { return propiedadesJson; }
    public void setPropiedadesJson(String propiedadesJson) { this.propiedadesJson = propiedadesJson; }
}