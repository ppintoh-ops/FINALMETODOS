package com.proyectomsw.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class VentanaPrincipal extends Application {

    @Override
    public void start(Stage escenarioPrincipal) {
        Label mensaje = new Label("Bienvenido a FormalModel Sim");
        StackPane raiz = new StackPane();
        raiz.getChildren().add(mensaje);
        Scene escena = new Scene(raiz, 800, 600);
        escenarioPrincipal.setTitle("FormalModel Sim - Inicio");
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show();
    }
}