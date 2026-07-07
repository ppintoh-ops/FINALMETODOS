package com.proyectomsw.core;

public class AppSession {
    private static Proyecto currentProyecto;

    public static void setCurrentProyecto(Proyecto proyecto) {
        currentProyecto = proyecto;
    }

    public static Proyecto getCurrentProyecto() {
        return currentProyecto;
    }
}