package com.proyectomsw.gui;

import com.proyectomsw.core.Proyecto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

public class EditorProyectoController {

    @FXML
    private Label tituloProyecto;

    private Proyecto proyectoActual;
    public void setProyecto(Proyecto proyecto) {
        this.proyectoActual = proyecto;
        tituloProyecto.setText("Lienzo de Modelado: " + proyecto.getNombre());
    }
    @FXML
    public void volverAlMenu(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VentanaPrincipal.fxml"));
            Parent raiz = loader.load();


            Stage escenario = (Stage) ((Node) event.getSource()).getScene().getWindow();
            escenario.setScene(new Scene(raiz, 800, 600));
        } catch (IOException e) {
            System.out.println("Error al volver al menú: " + e.getMessage());
        }
    }
}