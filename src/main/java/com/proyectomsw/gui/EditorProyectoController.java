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
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Line;
import javafx.scene.input.MouseButton;

public class EditorProyectoController {

    @FXML
    private Label tituloProyecto;

    @FXML
    private Pane lienzo;

    private double offsetArrastreX;
    private double offsetArrastreY;
    private int contadorEstados = 1;
    private Group estadoOrigenParaTransicion = null;


    private Proyecto proyectoActual;

    private void crearTransicion(Group origen, Group destino) {
        Line linea = new Line();
        linea.setStroke(Color.web("#2c3e50"));
        linea.setStrokeWidth(2);


        linea.startXProperty().bind(origen.layoutXProperty());
        linea.startYProperty().bind(origen.layoutYProperty());
        linea.endXProperty().bind(destino.layoutXProperty());
        linea.endYProperty().bind(destino.layoutYProperty());


        lienzo.getChildren().add(0, linea);
    }

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
        if (event.isConsumed()) {
            return;
        }
        double posicionX = event.getX();
        double posicionY = event.getY();

        Group nodoEstado = new Group();

        Circle circulo = new Circle(0, 0, 20);
        circulo.setFill(Color.web("#3498db"));
        circulo.setStroke(Color.web("#2980b9"));
        circulo.setStrokeWidth(2);

        Text texto = new Text("E" + contadorEstados++);
        texto.setFont(Font.font("System", FontWeight.BOLD, 12));
        texto.setFill(Color.WHITE);

        texto.setX(-7);
        texto.setY(4);

        nodoEstado.getChildren().addAll(circulo, texto);

        nodoEstado.setLayoutX(posicionX);
        nodoEstado.setLayoutY(posicionY);

        nodoEstado.setCursor(javafx.scene.Cursor.HAND);

        nodoEstado.setOnMousePressed(e -> {
            offsetArrastreX = nodoEstado.getLayoutX() - e.getSceneX();
            offsetArrastreY = nodoEstado.getLayoutY() - e.getSceneY();
            e.consume();
        });

        nodoEstado.setOnMouseDragged(e -> {
            nodoEstado.setLayoutX(e.getSceneX() + offsetArrastreX);
            nodoEstado.setLayoutY(e.getSceneY() + offsetArrastreY);
            e.consume();
        });

        nodoEstado.setOnMouseClicked(e -> {
            e.consume();
            if (e.getButton() == MouseButton.SECONDARY) {
                if (estadoOrigenParaTransicion == null) {
                    estadoOrigenParaTransicion = nodoEstado;
                    circulo.setStroke(Color.web("#e74c3c"));
                } else {
                    if (estadoOrigenParaTransicion != nodoEstado) {
                        crearTransicion(estadoOrigenParaTransicion, nodoEstado);
                    }
                    Circle circuloOrigen = (Circle) estadoOrigenParaTransicion.getChildren().get(0);
                    circuloOrigen.setStroke(Color.web("#2980b9"));
                    estadoOrigenParaTransicion = null;
                }
            }
        });

        lienzo.getChildren().add(nodoEstado);
    }
}