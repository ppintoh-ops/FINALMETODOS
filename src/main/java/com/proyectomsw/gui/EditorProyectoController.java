package com.proyectomsw.gui;

import com.proyectomsw.core.*;
import com.proyectomsw.database.HistorialSimulacionDAO;
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
import com.proyectomsw.database.EstadoDAO;
import com.proyectomsw.database.TransicionDAO;
import com.proyectomsw.database.HistorialSimulacionDAO;

public class EditorProyectoController {

    @FXML
    private Label tituloProyecto;

    @FXML
    private Pane lienzo;

    private double offsetArrastreX;
    private double offsetArrastreY;
    private java.util.HashSet<Integer> idsUsados = new java.util.HashSet<>();
    private Group estadoOrigenParaTransicion = null;
    private Group estadoActualSimulacion = null;
    private StringBuilder bitacoraSimulacion = new StringBuilder();


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
            try {
                System.out.println("--- INICIANDO GUARDADO DE TRANSICIÓN ---");

                com.proyectomsw.core.Proyecto p = com.proyectomsw.core.AppSession.getCurrentProyecto();
                if (p == null) {
                    System.err.println("ERROR: No hay proyecto en sesión.");
                    return;
                }

                if (origen == null || destino == null) {
                    System.err.println("ERROR: Los nodos origen o destino no existen.");
                    return;
                }

                Object objOrigen = origen.getProperties().get("idEstadoDB");
                Object objDestino = destino.getProperties().get("idEstadoDB");

                if (objOrigen == null || objDestino == null) {
                    System.err.println("ERROR: Faltan IDs. Origen: " + objOrigen + ", Destino: " + objDestino);
                    return;
                }

                Transicion t = new Transicion();
                t.setProyectoId(p.getId());
                t.setEstadoOrigenId((int) objOrigen);
                t.setEstadoDestinoId((int) objDestino);
                t.setEvento(campoSimbolo.getText() != null ? campoSimbolo.getText() : "");
                t.setCondicionDisparo("");

                int idGuardado = TransicionDAO.insertar(t);

                if (idGuardado > 0) {
                    System.out.println("ÉXITO: Transición guardada con ID: " + idGuardado);
                    campoSimbolo.setStyle("-fx-background-color: transparent; -fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 14px;");
                    campoSimbolo.setEditable(false);
                } else {
                    System.err.println("ERROR: Falló el guardado en la Base de Datos (Devolvió 0).");
                }

            } catch (Exception e) {
                System.err.println("EXCEPCIÓN CRÍTICA al presionar Enter:");
                e.printStackTrace();
            } finally {
                lienzo.requestFocus();
            }
        });
        lienzo.getChildren().add(0, puntaFlecha);
        lienzo.getChildren().add(0, linea);
        lienzo.getChildren().add(campoSimbolo);
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyectoActual = proyecto;
        System.out.println("DEBUG: setProyecto ejecutado en instancia: " + this.hashCode());
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
            } else if (e.getButton() == MouseButton.SECONDARY) {
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
            } else if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                boolean esFinal = !circuloInterno.isVisible();
                circuloInterno.setVisible(esFinal);

                nodoEstado.getProperties().put("esFinal", esFinal);
            } else if (e.getButton() == MouseButton.PRIMARY && e.isShiftDown()) {
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
        com.proyectomsw.core.Proyecto proyectoEnSesion = com.proyectomsw.core.AppSession.getCurrentProyecto();

        if (proyectoEnSesion == null) {
            System.err.println("¡ADVERTENCIA! No hay proyecto en AppSession. El estado se dibujará pero NO se guardará en la base de datos.");
        } else {
            Estado nuevoEstado = new Estado();
            nuevoEstado.setProyectoId(proyectoEnSesion.getId());
            nuevoEstado.setNombre("E" + idActual);
            nuevoEstado.setDescripcion("Estado autogenerado");

            nuevoEstado.setEsInicial(idActual == 1);

            nuevoEstado.setPropiedadesJson("{}");

            int idGeneradoDb = EstadoDAO.insertar(nuevoEstado);

            if (idGeneradoDb > 0) {
                nodoEstado.getProperties().put("idEstadoDB", idGeneradoDb);
                System.out.println("Estado guardado exitosamente con ID: " + idGeneradoDb);
            } else {
                System.err.println("Error al insertar el estado en la base de datos.");
            }
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

    @FXML
    public void iniciarSimulacion() {
        if (lienzo.getChildren().isEmpty()) return;

        for (Node nodo : lienzo.getChildren()) {
            if (nodo instanceof Group) {
                Group grupo = (Group) nodo;
                if (grupo.getProperties().get("idEstado") != null && (int) grupo.getProperties().get("idEstado") == 1) {
                    estadoActualSimulacion = grupo;

                    Circle circulo = (Circle) grupo.getChildren().get(0);
                    circulo.setFill(Color.web("#2ecc71"));
                    circulo.setStroke(Color.web("#27ae60"));
                    bitacoraSimulacion.append("-> Simulación iniciada. Punto de partida: Estado Inicial.\n");
                    evaluarPropiedadActual();
                    break;
                }
            }
        }
        System.out.println("Simulación iniciada en E1.");
    }

    @FXML
    public void avanzarSimulacion() {
        if (estadoActualSimulacion == null) {
            System.out.println("Primero debes iniciar la simulación.");
            return;
        }
        javafx.scene.control.TextInputDialog dialogo = new javafx.scene.control.TextInputDialog();
        dialogo.setTitle("Simulador Paso a Paso");
        dialogo.setHeaderText("Estás en el estado actual.");
        dialogo.setContentText("Ingresa el evento para avanzar (ej. a, 1, ε):");

        dialogo.showAndWait().ifPresent(eventoIngresado -> {
            boolean caminoEncontrado = false;

            for (Node nodo : lienzo.getChildren()) {
                if (nodo instanceof javafx.scene.control.TextField) {
                    javafx.scene.control.TextField campo = (javafx.scene.control.TextField) nodo;
                    Group origen = (Group) campo.getProperties().get("origen");
                    Group destino = (Group) campo.getProperties().get("destino");

                    if (origen != null && origen.equals(estadoActualSimulacion) && campo.getText().equals(eventoIngresado)) {

                        Circle circuloViejo = (Circle) estadoActualSimulacion.getChildren().get(0);
                        circuloViejo.setFill(Color.web("#3498db"));
                        circuloViejo.setStroke(Color.web("#2980b9"));

                        estadoActualSimulacion = destino;
                        Circle circuloNuevo = (Circle) estadoActualSimulacion.getChildren().get(0);
                        circuloNuevo.setFill(Color.web("#2ecc71"));
                        circuloNuevo.setStroke(Color.web("#27ae60"));

                        caminoEncontrado = true;

                        Text textoDestino = (Text) destino.getChildren().get(2);
                        bitacoraSimulacion.append("-> Transición exitosa [").append(eventoIngresado).append("]\n");
                        bitacoraSimulacion.append("   Llegada al estado: ").append(textoDestino.getText()).append("\n");

                        evaluarPropiedadActual();
                        break;
                    }
                }
            }

            if (!caminoEncontrado) {
                javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alerta.setHeaderText("Camino no válido");
                alerta.setContentText("No existe ninguna transición desde este estado usando el evento: '" + eventoIngresado + "'");
                alerta.show();
                bitacoraSimulacion.append("-> Intento fallido de transición con evento [").append(eventoIngresado).append("]\n");
            }
        });
    }

    private void evaluarPropiedadActual() {
        if (estadoActualSimulacion.getProperties().containsKey("propiedadFormal")) {
            String regla = (String) estadoActualSimulacion.getProperties().get("propiedadFormal");

            javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            bitacoraSimulacion.append("   [VERIFICACIÓN] Se evaluó la regla: ").append(regla).append("\n");
            alerta.setTitle("Verificación Automática");
            alerta.setHeaderText("¡Estado con Propiedad Formal Detectado!");
            alerta.setContentText("El sistema debe cumplir la regla: [ " + regla + " ] en este punto.");
            alerta.show();
        }
    }

    @FXML
    private void finalizarSimulacion(ActionEvent event) {
        com.proyectomsw.core.Proyecto proyectoEnSesion = com.proyectomsw.core.AppSession.getCurrentProyecto();

        if (proyectoEnSesion == null) {
            System.err.println("¡ERROR! No hay proyecto guardado en AppSession.");
            return;
        }

        try {
            String textoHistorial = bitacoraSimulacion.toString();

            HistorialSimulacion historial = new HistorialSimulacion(proyectoEnSesion.getId(), textoHistorial);

            boolean guardado = HistorialSimulacionDAO.insertar(historial);

            if (guardado) {
                System.out.println("Historial guardado exitosamente.");
                bitacoraSimulacion.setLength(0);
            } else {
                System.err.println("Error al guardar en base de datos.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}