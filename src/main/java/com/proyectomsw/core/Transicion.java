package com.proyectomsw.core;

public class Transicion {
    private int id;
    private int proyectoId;
    private int estadoOrigenId;
    private int estadoDestinoId;
    private String evento;
    private String condicionDisparo;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProyectoId() { return proyectoId; }
    public void setProyectoId(int proyectoId) { this.proyectoId = proyectoId; }

    public int getEstadoOrigenId() { return estadoOrigenId; }
    public void setEstadoOrigenId(int estadoOrigenId) { this.estadoOrigenId = estadoOrigenId; }

    public int getEstadoDestinoId() { return estadoDestinoId; }
    public void setEstadoDestinoId(int estadoDestinoId) { this.estadoDestinoId = estadoDestinoId; }

    public String getEvento() { return evento; }
    public void setEvento(String evento) { this.evento = evento; }

    public String getCondicionDisparo() { return condicionDisparo; }
    public void setCondicionDisparo(String condicionDisparo) { this.condicionDisparo = condicionDisparo; }
}