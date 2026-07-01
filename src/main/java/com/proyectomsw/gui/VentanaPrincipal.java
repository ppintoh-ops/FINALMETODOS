package com.proyectomsw.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VentanaPrincipal extends Application {

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VentanaPrincipal.fxml"));
        Parent raiz = loader.load();
        Scene escena = new Scene(raiz, 800, 600);
        escenarioPrincipal.setTitle("FormalModel Sim");
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show();

    }

    }