package com.proyectomsw;

import com.proyectomsw.database.ConexionDB;
import com.proyectomsw.gui.VentanaPrincipal;
import javafx.application.Application;

public class main {
    public static void main(String[] args) {
        System.out.println("Iniciando FormalModel Sim...");


        ConexionDB.conectar();


        System.out.println(" Abriendo ventana principal...");
        Application.launch(VentanaPrincipal.class, args);


        ConexionDB.desconectar();
        System.out.println(" Aplicación cerrada correctamente.");
    }
}