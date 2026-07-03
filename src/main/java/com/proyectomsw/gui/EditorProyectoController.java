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
import javafx.scene.shape.Polygon;
import javafx.beans.value.ChangeListener;

public class EditorProyectoController {

    @FXML
    private Label tituloProyecto;

    @FXML
    private Pane lienzo;

    private double offsetArrastreX;
    private double offsetArrastreY;
    private java.util.HashSet<Integer> idsUsados = new java.util.HashSet<>();
    private Group estadoOrigenParaTransicion = null;


    private Proyecto proyectoActual;

    private int obtenerSiguienteId() {
        int id = 1;
        // Mientras el ID ya esté en uso, probamos con el siguiente número
        while (idsUsados.contains(id)) {
            id++;
        }
        return id;
    }

    private void crearTransicion(Group origen, Group destino) {
        Line linea = new Line();
        linea.setStroke(Color.web("#2c3e50"));
        linea.setStrokeWidth(2);

        Polygon puntaFlecha = new Polygon();
        puntaFlecha.setFill(Color.web("#2c3e50"));

        ChangeListener<Number> actualizadorFlecha = (observable, oldValue, newValue) -> {
            double inicioX = origen.getLayoutX();
            double inicioY = origen.getLayoutY();
            double finX = destino.getLayoutX();
            double finY = destino.getLayoutY();

            double distancia = Math.hypot(finX - inicioX, finY - inicioY);


            if (distancia < 40) return;


            double theta = Math.atan2(finY - inicioY, finX - inicioX);
            double radio = 20.0;

            double puntaX = finX - radio * Math.cos(theta);
            double puntaY = finY - radio * Math.sin(theta);


            double largoFlecha = 12.0;
            double anchoFlecha = 6.0;

            double baseMediaX = puntaX - largoFlecha * Math.cos(theta);
            double baseMediaY = puntaY - largoFlecha * Math.sin(theta);


            double esq1X = baseMediaX + anchoFlecha * Math.cos(theta + Math.PI / 2);
            double esq1Y = baseMediaY + anchoFlecha * Math.sin(theta + Math.PI / 2);

            double esq2X = baseMediaX + anchoFlecha * Math.cos(theta - Math.PI / 2);
            double esq2Y = baseMediaY + anchoFlecha * Math.sin(theta - Math.PI / 2);


            puntaFlecha.getPoints().setAll(
                    puntaX, puntaY,
                    esq1X, esq1Y,
                    esq2X, esq2Y
            );


            linea.setStartX(inicioX + radio * Math.cos(theta));
            linea.setStartY(inicioY + radio * Math.sin(theta));
            linea.setEndX(baseMediaX);
            linea.setEndY(baseMediaY);
        };


        origen.layoutXProperty().addListener(actualizadorFlecha);
        origen.layoutYProperty().addListener(actualizadorFlecha);
        destino.layoutXProperty().addListener(actualizadorFlecha);
        destino.layoutYProperty().addListener(actualizadorFlecha);


        actualizadorFlecha.changed(null, null, null);

        linea.getProperties().put("origen", origen);
        linea.getProperties().put("destino", destino);
        puntaFlecha.getProperties().put("origen", origen);
        puntaFlecha.getProperties().put("destino", destino);

        lienzo.getChildren().add(0, puntaFlecha);
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
        if (event.isConsumed() || event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        double posicionX = event.getX();
        double posicionY = event.getY();
        double radio = 20.0;

        if (posicionX < radio || posicionX > lienzo.getWidth() - radio ||
                posicionY < radio || posicionY > lienzo.getHeight() - radio) {
            return;
        }

        Group nodoEstado = new Group();

        Circle circulo = new Circle(0, 0, radio);
        circulo.setFill(Color.web("#3498db"));
        circulo.setStroke(Color.web("#2980b9"));
        circulo.setStrokeWidth(2);

        Circle circuloInterno = new Circle(0, 0, radio - 5);
        circuloInterno.setFill(Color.TRANSPARENT);
        circuloInterno.setStroke(Color.web("#2980b9"));
        circuloInterno.setStrokeWidth(2);
        circuloInterno.setVisible(false);
        circuloInterno.setMouseTransparent(true);

        int idActual = obtenerSiguienteId();
        idsUsados.add(idActual);

        Text texto = new Text("E" + idActual);
        texto.setFont(Font.font("System", FontWeight.BOLD, 12));
        texto.setFill(Color.WHITE);
        texto.setX(-7);
        texto.setY(4);

        nodoEstado.getChildren().addAll(circulo, circuloInterno, texto);

        nodoEstado.getProperties().put("idEstado", idActual);

        if (idActual == 1) {
            Line flechaInicioLinea = new Line(-45, 0, -20, 0);
            flechaInicioLinea.setStroke(Color.web("#2c3e50"));
            flechaInicioLinea.setStrokeWidth(2);


            Polygon puntaInicio = new Polygon(
                    -20, 0,
                    -28, 5,
                    -28, -5
            );
            puntaInicio.setFill(Color.web("#2c3e50"));

            nodoEstado.getChildren().addAll(flechaInicioLinea, puntaInicio);
        }



        nodoEstado.setLayoutX(posicionX);
        nodoEstado.setLayoutY(posicionY);

        nodoEstado.setCursor(javafx.scene.Cursor.HAND);

        nodoEstado.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                offsetArrastreX = nodoEstado.getLayoutX() - e.getSceneX();
                offsetArrastreY = nodoEstado.getLayoutY() - e.getSceneY();
            }
            else if (e.getButton() == MouseButton.SECONDARY) {
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
            e.consume();
        });

        nodoEstado.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                double nuevaX = e.getSceneX() + offsetArrastreX;
                double nuevaY = e.getSceneY() + offsetArrastreY;


                double limiteDerecho = lienzo.getWidth() - radio;
                double limiteInferior = lienzo.getHeight() - radio;


                nuevaX = Math.max(radio, Math.min(nuevaX, limiteDerecho));
                nuevaY = Math.max(radio, Math.min(nuevaY, limiteInferior));

                nodoEstado.setLayoutX(nuevaX);
                nodoEstado.setLayoutY(nuevaY);
            }
            e.consume();
        });

        nodoEstado.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.MIDDLE) {

                int idLiberado = (int) nodoEstado.getProperties().get("idEstado");
                idsUsados.remove(idLiberado);

                lienzo.getChildren().removeIf(nodo ->
                        nodoEstado.equals(nodo.getProperties().get("origen")) ||
                                nodoEstado.equals(nodo.getProperties().get("destino"))
                );
                lienzo.getChildren().remove(nodoEstado);
                if (estadoOrigenParaTransicion == nodoEstado) {
                    estadoOrigenParaTransicion = null;
                }
            }
            else if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                boolean esFinal = !circuloInterno.isVisible();
                circuloInterno.setVisible(esFinal);

                nodoEstado.getProperties().put("esFinal", esFinal);
            }
            e.consume();
        });


        lienzo.getChildren().add(nodoEstado);
    }

    @FXML
    public void limpiarLienzo() {

        lienzo.getChildren().clear();


        idsUsados.clear();


        estadoOrigenParaTransicion = null;
    }
}