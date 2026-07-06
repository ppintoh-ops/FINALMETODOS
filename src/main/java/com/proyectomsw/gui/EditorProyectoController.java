package com.proyectomsw.gui;

import com.proyectomsw.core.Proyecto;
import javafx.beans.InvalidationListener;
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
import com.proyectomsw.core.Estado;
import com.proyectomsw.core.Transicion;
import com.proyectomsw.database.EstadoDAO;
import com.proyectomsw.database.TransicionDAO;

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

        while (idsUsados.contains(id)) {
            id++;
        }
        return id;
    }

    private void crearTransicion(Group origen, Group destino) {
        Line linea = new Line();
        Polygon puntaFlecha = new Polygon();


        javafx.scene.control.TextField campoSimbolo = new javafx.scene.control.TextField("ε");
        campoSimbolo.setPrefWidth(40);
        campoSimbolo.setAlignment(javafx.geometry.Pos.CENTER);
        campoSimbolo.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 14px;");

        double radio = 25.0;

        InvalidationListener actualizadorFlecha = observable -> {
            double x1 = origen.getLayoutX();
            double y1 = origen.getLayoutY();
            double x2 = destino.getLayoutX();
            double y2 = destino.getLayoutY();

            double dx = x2 - x1;
            double dy = y2 - y1;
            double distancia = Math.sqrt(dx * dx + dy * dy);

            if (distancia == 0) return;


            double startX = x1 + (dx / distancia) * radio;
            double startY = y1 + (dy / distancia) * radio;
            double endX = x2 - (dx / distancia) * radio;
            double endY = y2 - (dy / distancia) * radio;

            double angulo = Math.atan2(dy, dx);
            double tamanoFlecha = 15.0;
            double anguloApertura = Math.toRadians(20);

            double xPunta1 = endX - tamanoFlecha * Math.cos(angulo - anguloApertura);
            double yPunta1 = endY - tamanoFlecha * Math.sin(angulo - anguloApertura);
            double xPunta2 = endX - tamanoFlecha * Math.cos(angulo + anguloApertura);
            double yPunta2 = endY - tamanoFlecha * Math.sin(angulo + anguloApertura);

            puntaFlecha.getPoints().setAll(
                    endX, endY,
                    xPunta1, yPunta1,
                    xPunta2, yPunta2
            );
            puntaFlecha.setFill(Color.web("#2c3e50"));

            double retroceso = tamanoFlecha * 0.8;
            linea.setStartX(startX);
            linea.setStartY(startY);
            linea.setEndX(endX - (dx / distancia) * retroceso);
            linea.setEndY(endY - (dy / distancia) * retroceso);

            linea.setStroke(Color.web("#2c3e50"));
            linea.setStrokeWidth(2);

            double medioX = (startX + endX) / 2;
            double medioY = (startY + endY) / 2;

            double nx = dy / distancia;
            double ny = -dx / distancia;
            if (ny > 0) {
                nx = -nx;
                ny = -ny;
            }
            double offsetX = nx * 20;
            double offsetY = ny * 20;

            campoSimbolo.setLayoutX(medioX + offsetX - 20);
            campoSimbolo.setLayoutY(medioY + offsetY - 15);
        };


        origen.layoutXProperty().addListener(actualizadorFlecha);
        origen.layoutYProperty().addListener(actualizadorFlecha);
        destino.layoutXProperty().addListener(actualizadorFlecha);
        destino.layoutYProperty().addListener(actualizadorFlecha);


        actualizadorFlecha.invalidated(null);


        linea.getProperties().put("origen", origen);
        linea.getProperties().put("destino", destino);
        puntaFlecha.getProperties().put("origen", origen);
        puntaFlecha.getProperties().put("destino", destino);
        campoSimbolo.getProperties().put("origen", origen);
        campoSimbolo.getProperties().put("destino", destino);

        campoSimbolo.setOnAction(event -> {
            if (this.proyectoActual != null) {
                Object objOrigen = origen.getProperties().get("idEstadoDB");
                Object objDestino = destino.getProperties().get("idEstadoDB");

                if (objOrigen != null && objDestino != null) {
                    Transicion nuevaTransicion = new Transicion();
                    nuevaTransicion.setProyectoId(this.proyectoActual.getId());
                    nuevaTransicion.setEstadoOrigenId((int) objOrigen);
                    nuevaTransicion.setEstadoDestinoId((int) objDestino);
                    nuevaTransicion.setEvento(campoSimbolo.getText());
                    nuevaTransicion.setCondicionDisparo("");

                    TransicionDAO.insertar(nuevaTransicion);

                    campoSimbolo.setStyle("-fx-background-color: transparent; -fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 14px;");
                    campoSimbolo.setEditable(false);
                }
            }
            lienzo.requestFocus();
        });


        lienzo.getChildren().add(0, puntaFlecha);
        lienzo.getChildren().add(0, linea);
        lienzo.getChildren().add(campoSimbolo);
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
            }else if (e.getButton() == MouseButton.PRIMARY && e.isShiftDown()) {
                javafx.scene.control.TextInputDialog dialogo = new javafx.scene.control.TextInputDialog();
                dialogo.setTitle("Propiedad Formal");
                dialogo.setHeaderText("Asignar propiedad a " + texto.getText());
                dialogo.setContentText("Escriba la regla lógica (ej. x > 0):");

                dialogo.showAndWait().ifPresent(propiedadIngresada -> {
                    nodoEstado.getProperties().put("propiedadFormal", propiedadIngresada);
                    texto.setFill(Color.web("#f1c40f"));

                    System.out.println("Propiedad asignada a " + texto.getText() + ": " + propiedadIngresada);
                });
            }
            e.consume();
        });
        if (this.proyectoActual != null) {
            Estado nuevoEstado = new Estado();
            nuevoEstado.setProyectoId(this.proyectoActual.getId());
            nuevoEstado.setNombre("E" + idActual);
            nuevoEstado.setDescripcion("Estado autogenerado");
            nuevoEstado.setEsInicial(idActual == 1);
            nuevoEstado.setPropiedadesJson("{}");

            int idGeneradoDb = EstadoDAO.insertar(nuevoEstado);

            nodoEstado.getProperties().put("idEstadoDB", idGeneradoDb);
        }

        lienzo.getChildren().add(nodoEstado);
    }

    @FXML
    public void detectarEstadosInalcanzables() {
        java.util.List<Group> todosLosEstados = new java.util.ArrayList<>();
        java.util.List<Line> todasLasTransiciones = new java.util.ArrayList<>();
        Group estadoInicial = null;

        for (Node nodo : lienzo.getChildren()) {
            if (nodo instanceof Group) {
                Group grupo = (Group) nodo;
                todosLosEstados.add(grupo);
                if (grupo.getProperties().get("idEstado") != null && (int) grupo.getProperties().get("idEstado") == 1) {
                    estadoInicial = grupo;
                }
            } else if (nodo instanceof Line) {
                Line linea = (Line) nodo;
                if (linea.getProperties().containsKey("origen") && linea.getProperties().containsKey("destino")) {
                    todasLasTransiciones.add(linea);
                }
            }
        }

        if (estadoInicial == null) {
            System.out.println("No hay estado inicial (E1) definido.");
            return;
        }

        java.util.Set<Group> estadosAlcanzables = new java.util.HashSet<>();
        java.util.Queue<Group> cola = new java.util.LinkedList<>();

        cola.add(estadoInicial);
        estadosAlcanzables.add(estadoInicial);

        while (!cola.isEmpty()) {
            Group estadoActual = cola.poll();

            for (Line transicion : todasLasTransiciones) {
                Group origen = (Group) transicion.getProperties().get("origen");
                Group destino = (Group) transicion.getProperties().get("destino");

                if (origen.equals(estadoActual) && !estadosAlcanzables.contains(destino)) {
                    estadosAlcanzables.add(destino);
                    cola.add(destino);
                }
            }
        }


        int inalcanzablesEncontrados = 0;
        for (Group estado : todosLosEstados) {
            Circle circuloPrincipal = (Circle) estado.getChildren().get(0);

            if (!estadosAlcanzables.contains(estado)) {
                circuloPrincipal.setFill(Color.web("#7f8c8d"));
                circuloPrincipal.setStroke(Color.web("#bdc3c7"));
                inalcanzablesEncontrados++;
            } else {
                circuloPrincipal.setFill(Color.web("#3498db"));
                circuloPrincipal.setStroke(Color.web("#2980b9"));
            }
        }

        System.out.println("Análisis completado. Se encontraron " + inalcanzablesEncontrados + " estados inalcanzables.");
    }


    @FXML
    public void limpiarLienzo() {

        lienzo.getChildren().clear();


        idsUsados.clear();


        estadoOrigenParaTransicion = null;
    }
}