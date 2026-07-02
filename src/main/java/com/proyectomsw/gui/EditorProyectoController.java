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
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

public class EditorProyectoController {

    @FXML
    private Label tituloProyecto;

    @FXML
    private Pane lienzo;

    private double offsetArrastreX;
    private double offsetArrastreY;

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
    @FXML
    public void clicEnLienzo(MouseEvent event) {
        if (event.isConsumed()){
            return;
        }
        double posicionX = event.getX();
        double posicionY = event.getY();
        Circle nuevoEstado = new Circle(posicionX, posicionY, 20);
        nuevoEstado.setFill(Color.web("#3498db"));
        nuevoEstado.setStroke(Color.web("#2980b9"));
        nuevoEstado.setStrokeWidth(2);
        nuevoEstado.setCursor(javafx.scene.Cursor.HAND);

        nuevoEstado.setOnMousePressed(e -> {
            offsetArrastreX = nuevoEstado.getCenterX() - e.getX();
            offsetArrastreY = nuevoEstado.getCenterY() - e.getY();
            e.consume();
        });
        nuevoEstado.setOnMouseDragged(e -> {
                    nuevoEstado.setCenterX(e.getX() + offsetArrastreX);
                    nuevoEstado.setCenterY(e.getY() + offsetArrastreY);
                    e.consume();
                });

        nuevoEstado.setOnMouseClicked(e -> {

            e.consume();
        });

        lienzo.getChildren().add(nuevoEstado);
    }
}