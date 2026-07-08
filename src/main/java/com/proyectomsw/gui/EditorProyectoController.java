package com.proyectomsw.gui;

import com.proyectomsw.core.*;
import com.proyectomsw.database.EstadoDAO;
import com.proyectomsw.database.TransicionDAO;
import com.proyectomsw.database.HistorialSimulacionDAO;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
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
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class EditorProyectoController {

    @FXML private Label tituloProyecto;
    @FXML private Pane lienzo;

    private double offsetArrastreX;
    private double offsetArrastreY;
    private HashSet<Integer> idsUsados = new HashSet<>();
    private Group estadoOrigenParaTransicion = null;
    private Group estadoActualSimulacion = null;
    private StringBuilder bitacoraSimulacion = new StringBuilder();

    @FXML
    public void initialize() {
        Platform.runLater(this::cargarGrafoDesdeBD);
    }

    private int obtenerSiguienteIdLogico() {
        int id = 1;
        while (idsUsados.contains(id)) {
            id++;
        }
        return id;
    }

    private void cargarGrafoDesdeBD() {
        Proyecto p = AppSession.getCurrentProyecto();
        if (p == null) return;

        lienzo.getChildren().clear();
        idsUsados.clear();
        estadoOrigenParaTransicion = null;
        estadoActualSimulacion = null;

        List<Estado> estados = EstadoDAO.obtenerPorProyecto(p.getId());
        List<Transicion> transiciones = TransicionDAO.obtenerTransicionesPorProyecto(p.getId());

        System.out.println("--- CARGANDO PROYECTO ---");
        System.out.println("Estados encontrados en BD: " + estados.size());
        System.out.println("Transiciones encontradas en BD: " + transiciones.size());

        HashMap<Integer, Group> mapaEstadosVisuales = new HashMap<>();

        double startX = 100;
        double startY = 150;

        for (Estado est : estados) {
            Group nodoVisual = dibujarEstado(est.getNombre(), est.getId(), startX, startY, est.isEsInicial());
            mapaEstadosVisuales.put(est.getId(), nodoVisual);

            int idLogico = 1;
            try {
                if (est.getNombre().startsWith("E")) {
                    idLogico = Integer.parseInt(est.getNombre().substring(1));
                }
            } catch (Exception e) {}

            nodoVisual.getProperties().put("idEstado", idLogico);
            idsUsados.add(idLogico);

            startX += 120;
            if (startX > 700) {
                startX = 100;
                startY += 120;
            }
        }

        for (Transicion t : transiciones) {
            Group origen = mapaEstadosVisuales.get(t.getEstadoOrigenId());
            Group destino = mapaEstadosVisuales.get(t.getEstadoDestinoId());

            if (origen != null && destino != null) {
                dibujarTransicionCargada(origen, destino, t);
            }
        }
    }

    @FXML
    public void clicEnLienzo(MouseEvent event) {
        if (event.isConsumed() || event.getButton() != MouseButton.PRIMARY) return;

        double posicionX = event.getX();
        double posicionY = event.getY();
        double radio = 20.0;

        if (posicionX < radio || posicionX > lienzo.getWidth() - radio ||
                posicionY < radio || posicionY > lienzo.getHeight() - radio) {
            return;
        }

        Proyecto p = AppSession.getCurrentProyecto();
        if (p == null) {
            System.err.println("Error: No hay proyecto activo en sesión.");
            return;
        }

        int idActual = obtenerSiguienteIdLogico();
        idsUsados.add(idActual);
        boolean esInicial = (idActual == 1);
        String nombreEstado = "E" + idActual;

        Estado nuevoEstado = new Estado();
        nuevoEstado.setProyectoId(p.getId());
        nuevoEstado.setNombre(nombreEstado);
        nuevoEstado.setDescripcion("Estado autogenerado");
        nuevoEstado.setEsInicial(esInicial);
        nuevoEstado.setPropiedadesJson("{}");

        int idGeneradoDb = EstadoDAO.insertar(nuevoEstado);

        if (idGeneradoDb > 0) {
            Group nodoVisual = dibujarEstado(nombreEstado, idGeneradoDb, posicionX, posicionY, esInicial);
            nodoVisual.getProperties().put("idEstado", idActual);
        } else {
            System.err.println("Error al insertar el estado en la base de datos.");
            idsUsados.remove(idActual);
        }
    }

    private Group dibujarEstado(String nombre, int idDB, double x, double y, boolean esInicial) {
        double radio = 20.0;
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

        Text texto = new Text(nombre);
        texto.setFont(Font.font("System", FontWeight.BOLD, 12));
        texto.setFill(Color.WHITE);
        texto.setX(-7);
        texto.setY(4);

        nodoEstado.getChildren().addAll(circulo, circuloInterno, texto);

        if (esInicial || nombre.equals("E1")) {
            Line flechaInicioLinea = new Line(-45, 0, -20, 0);
            flechaInicioLinea.setStroke(Color.web("#2c3e50"));
            flechaInicioLinea.setStrokeWidth(2);
            Polygon puntaInicio = new Polygon(-20, 0, -28, 5, -28, -5);
            puntaInicio.setFill(Color.web("#2c3e50"));
            nodoEstado.getChildren().addAll(flechaInicioLinea, puntaInicio);
        }

        nodoEstado.setLayoutX(x);
        nodoEstado.setLayoutY(y);
        nodoEstado.setCursor(javafx.scene.Cursor.HAND);

        nodoEstado.getProperties().put("idEstadoDB", idDB);
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
                nodoEstado.setLayoutX(Math.max(radio, Math.min(nuevaX, lienzo.getWidth() - radio)));
                nodoEstado.setLayoutY(Math.max(radio, Math.min(nuevaY, lienzo.getHeight() - radio)));
            }
            e.consume();
        });

        nodoEstado.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.MIDDLE) {
                Object objId = nodoEstado.getProperties().get("idEstado");
                if (objId != null) {
                    idsUsados.remove((int) objId);
                }

                lienzo.getChildren().removeIf(nodo ->
                        nodoEstado.equals(nodo.getProperties().get("origen")) ||
                                nodoEstado.equals(nodo.getProperties().get("destino"))
                );
                lienzo.getChildren().remove(nodoEstado);
                if (estadoOrigenParaTransicion == nodoEstado) estadoOrigenParaTransicion = null;

            } else if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                boolean esFinal = !circuloInterno.isVisible();
                circuloInterno.setVisible(esFinal);
                nodoEstado.getProperties().put("esFinal", esFinal);
            } else if (e.getButton() == MouseButton.PRIMARY && e.isShiftDown()) {
                TextInputDialog dialogo = new TextInputDialog();
                dialogo.setTitle("Propiedad Formal");
                dialogo.setHeaderText("Asignar propiedad a " + texto.getText());
                dialogo.setContentText("Escriba la regla lógica:");
                dialogo.showAndWait().ifPresent(prop -> {
                    nodoEstado.getProperties().put("propiedadFormal", prop);
                    texto.setFill(Color.web("#f1c40f"));
                });
            }
            e.consume();
        });

        lienzo.getChildren().add(nodoEstado);
        return nodoEstado;
    }

    private void crearTransicion(Group origen, Group destino) {
        Line linea = new Line();
        Polygon puntaFlecha = new Polygon();
        TextField campoSimbolo = new TextField("");
        campoSimbolo.setPromptText("ε");
        campoSimbolo.setPrefWidth(40);
        campoSimbolo.setAlignment(javafx.geometry.Pos.CENTER);
        campoSimbolo.setStyle("-fx-background-color: white; -fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-border-color: #e74c3c; -fx-border-radius: 5px;");

        configurarMatematicaFlecha(origen, destino, linea, puntaFlecha, campoSimbolo);


        campoSimbolo.setOnAction(e -> procesarGuardadoDeTransicion(campoSimbolo, origen, destino));

        campoSimbolo.focusedProperty().addListener((obs, viejo, nuevo) -> {
            if (!nuevo && campoSimbolo.isEditable()) {
                procesarGuardadoDeTransicion(campoSimbolo, origen, destino);
            }
        });

        lienzo.getChildren().add(0, puntaFlecha);
        lienzo.getChildren().add(0, linea);
        lienzo.getChildren().add(campoSimbolo);


        campoSimbolo.requestFocus();
    }

    private void procesarGuardadoDeTransicion(TextField campoSimbolo, Group origen, Group destino) {
        if (!campoSimbolo.isEditable()) return;

        Proyecto p = AppSession.getCurrentProyecto();
        if (p == null) return;

        Object objOrigen = origen.getProperties().get("idEstadoDB");
        Object objDestino = destino.getProperties().get("idEstadoDB");

        if (objOrigen != null && objDestino != null) {
            String evento = campoSimbolo.getText();
            if (evento == null || evento.trim().isEmpty()) evento = "ε";

            Transicion t = new Transicion();
            t.setProyectoId(p.getId());
            t.setEstadoOrigenId((int) objOrigen);
            t.setEstadoDestinoId((int) objDestino);
            t.setEvento(evento);
            t.setCondicionDisparo("");

            int idGuardado = TransicionDAO.insertar(t);

            if (idGuardado > 0) {
                campoSimbolo.setText(evento);
                campoSimbolo.setStyle("-fx-background-color: transparent; -fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 14px; -fx-border-color: transparent;");
                campoSimbolo.setEditable(false);
                System.out.println("Transición guardada exitosamente con ID: " + idGuardado);
            } else {
                System.err.println("Fallo al guardar la transición en la Base de Datos.");
            }
        }
        lienzo.requestFocus();
    }

    private void dibujarTransicionCargada(Group origen, Group destino, Transicion transicion) {
        Line linea = new Line();
        Polygon puntaFlecha = new Polygon();
        TextField campoSimbolo = new TextField(transicion.getEvento());
        campoSimbolo.setPrefWidth(40);
        campoSimbolo.setAlignment(javafx.geometry.Pos.CENTER);
        campoSimbolo.setStyle("-fx-background-color: transparent; -fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 14px;");
        campoSimbolo.setEditable(false);

        configurarMatematicaFlecha(origen, destino, linea, puntaFlecha, campoSimbolo);

        lienzo.getChildren().add(0, puntaFlecha);
        lienzo.getChildren().add(0, linea);
        lienzo.getChildren().add(campoSimbolo);

        Timeline reajusteTemporal = new Timeline(new KeyFrame(Duration.millis(150), e -> {
            linea.setVisible(false); linea.setVisible(true);
        }));
        reajusteTemporal.play();
    }

    private void configurarMatematicaFlecha(Group origen, Group destino, Line linea, Polygon puntaFlecha, TextField campo) {
        double radio = 25.0;

        InvalidationListener actualizador = observable -> {
            double x1 = origen.getLayoutX(); double y1 = origen.getLayoutY();
            double x2 = destino.getLayoutX(); double y2 = destino.getLayoutY();
            double dx = x2 - x1; double dy = y2 - y1;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist == 0) return;

            double startX = x1 + (dx / dist) * radio; double startY = y1 + (dy / dist) * radio;
            double endX = x2 - (dx / dist) * radio; double endY = y2 - (dy / dist) * radio;
            double angulo = Math.atan2(dy, dx);
            double tamFlecha = 15.0; double apertura = Math.toRadians(20);

            puntaFlecha.getPoints().setAll(
                    endX, endY,
                    endX - tamFlecha * Math.cos(angulo - apertura), endY - tamFlecha * Math.sin(angulo - apertura),
                    endX - tamFlecha * Math.cos(angulo + apertura), endY - tamFlecha * Math.sin(angulo + apertura)
            );
            puntaFlecha.setFill(Color.web("#2c3e50"));

            linea.setStartX(startX); linea.setStartY(startY);
            linea.setEndX(endX - (dx / dist) * (tamFlecha * 0.8));
            linea.setEndY(endY - (dy / dist) * (tamFlecha * 0.8));
            linea.setStroke(Color.web("#2c3e50")); linea.setStrokeWidth(2);

            double medioX = (startX + endX) / 2; double medioY = (startY + endY) / 2;
            double nx = dy / dist; double ny = -dx / dist;
            if (ny > 0) { nx = -nx; ny = -ny; }
            campo.setLayoutX(medioX + (nx * 20) - 20);
            campo.setLayoutY(medioY + (ny * 20) - 15);
        };

        origen.layoutXProperty().addListener(actualizador); origen.layoutYProperty().addListener(actualizador);
        destino.layoutXProperty().addListener(actualizador); destino.layoutYProperty().addListener(actualizador);
        actualizador.invalidated(null);

        linea.getProperties().put("origen", origen); linea.getProperties().put("destino", destino);
        campo.getProperties().put("origen", origen); campo.getProperties().put("destino", destino);
    }

    @FXML
    public void detectarEstadosInalcanzables() {
        List<Group> todos = new ArrayList<>();
        List<Line> transiciones = new ArrayList<>();
        Group estInicial = null;

        for (Node nodo : lienzo.getChildren()) {
            if (nodo instanceof Group) {
                Group g = (Group) nodo;
                todos.add(g);
                if (g.getProperties().get("idEstado") != null && (int) g.getProperties().get("idEstado") == 1) {
                    estInicial = g;
                }
            } else if (nodo instanceof Line) {
                if (nodo.getProperties().containsKey("origen") && nodo.getProperties().containsKey("destino")) {
                    transiciones.add((Line) nodo);
                }
            }
        }

        if (estInicial == null) return;

        Set<Group> alcanzables = new HashSet<>();
        Queue<Group> cola = new LinkedList<>();
        cola.add(estInicial);
        alcanzables.add(estInicial);

        while (!cola.isEmpty()) {
            Group actual = cola.poll();
            for (Line transicion : transiciones) {
                Group origen = (Group) transicion.getProperties().get("origen");
                Group destino = (Group) transicion.getProperties().get("destino");
                if (origen.equals(actual) && !alcanzables.contains(destino)) {
                    alcanzables.add(destino);
                    cola.add(destino);
                }
            }
        }

        for (Group estado : todos) {
            Circle c = (Circle) estado.getChildren().get(0);
            if (!alcanzables.contains(estado)) {
                c.setFill(Color.web("#7f8c8d")); c.setStroke(Color.web("#bdc3c7"));
            } else {
                c.setFill(Color.web("#3498db")); c.setStroke(Color.web("#2980b9"));
            }
        }
    }

    @FXML
    public void exportarProyecto() {
        Proyecto p = AppSession.getCurrentProyecto();
        if (p == null) return;

        List<Estado> estados = EstadoDAO.obtenerPorProyecto(p.getId());
        List<Transicion> transiciones = TransicionDAO.obtenerTransicionesPorProyecto(p.getId());
        Map<Integer, String> mapaNombres = new HashMap<>();
        for (Estado e : estados) mapaNombres.put(e.getId(), e.getNombre());

        StringBuilder reporte = new StringBuilder();
        reporte.append("--- MODELO FORMAL ---\nProyecto: ").append(p.getNombre()).append("\n\nESTADOS:\n");
        for (Estado e : estados) reporte.append("- ").append(e.getNombre()).append("\n");

        reporte.append("\nTRANSICIONES:\n");
        for (Transicion t : transiciones) {
            reporte.append("[ ").append(mapaNombres.getOrDefault(t.getEstadoOrigenId(), "?")).append(" ] --(")
                    .append(t.getEvento()).append(")--> [ ").append(mapaNombres.getOrDefault(t.getEstadoDestinoId(), "?")).append(" ]\n");
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar a TXT");
        chooser.setInitialFileName(p.getNombre().replaceAll("\\s+", "_") + ".txt");
        File archivo = chooser.showSaveDialog(lienzo.getScene().getWindow());
        if (archivo != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
                writer.print(reporte.toString());
            } catch (IOException ex) {}
        }
    }

    @FXML public void limpiarLienzo() { lienzo.getChildren().clear(); idsUsados.clear(); estadoOrigenParaTransicion = null; }

    @FXML
    public void volverAlMenu(ActionEvent event) {
        try {
            Parent raiz = new FXMLLoader(getClass().getResource("/fxml/VentanaPrincipal.fxml")).load();
            Stage escenario = (Stage) ((Node) event.getSource()).getScene().getWindow();
            escenario.setScene(new Scene(raiz, 800, 600));
        } catch (IOException e) {}
    }

    @FXML
    public void iniciarSimulacion() {
        if (lienzo.getChildren().isEmpty()) return;
        for (Node nodo : lienzo.getChildren()) {
            if (nodo instanceof Group) {
                Group g = (Group) nodo;
                if (g.getProperties().get("idEstado") != null && (int) g.getProperties().get("idEstado") == 1) {
                    estadoActualSimulacion = g;
                    Circle c = (Circle) g.getChildren().get(0);
                    c.setFill(Color.web("#2ecc71")); c.setStroke(Color.web("#27ae60"));
                    bitacoraSimulacion.append("Simulación iniciada en E1\n");
                    break;
                }
            }
        }
    }

    @FXML
    public void avanzarSimulacion() {
        if (estadoActualSimulacion == null) return;
        TextInputDialog d = new TextInputDialog(); d.setTitle("Avanzar"); d.setHeaderText("Ingresa el evento:");
        d.showAndWait().ifPresent(evento -> {
            boolean encontrado = false;
            for (Node nodo : lienzo.getChildren()) {
                if (nodo instanceof TextField) {
                    TextField campo = (TextField) nodo;
                    Group origen = (Group) campo.getProperties().get("origen");
                    Group destino = (Group) campo.getProperties().get("destino");
                    if (origen != null && origen.equals(estadoActualSimulacion) && campo.getText().equals(evento)) {
                        ((Circle) estadoActualSimulacion.getChildren().get(0)).setFill(Color.web("#3498db"));
                        estadoActualSimulacion = destino;
                        ((Circle) estadoActualSimulacion.getChildren().get(0)).setFill(Color.web("#2ecc71"));
                        bitacoraSimulacion.append("Transición a ").append(((Text) destino.getChildren().get(2)).getText()).append("\n");
                        encontrado = true;
                        break;
                    }
                }
            }
            if (!encontrado) {
                Alert a = new Alert(Alert.AlertType.WARNING); a.setContentText("Evento no válido"); a.show();
            }
        });
    }

    @FXML
    private void finalizarSimulacion(ActionEvent event) {
        Proyecto p = AppSession.getCurrentProyecto();
        if (p != null) {
            HistorialSimulacionDAO.insertar(new HistorialSimulacion(p.getId(), bitacoraSimulacion.toString()));
            bitacoraSimulacion.setLength(0);
        }
    }

    @FXML
    public void exportarProyectoCompleto() {
        Proyecto p = AppSession.getCurrentProyecto();
        if (p == null) return;

        List<Estado> estados = EstadoDAO.obtenerPorProyecto(p.getId());
        List<Transicion> transiciones = TransicionDAO.obtenerTransicionesPorProyecto(p.getId());
        List<HistorialSimulacion> historial = HistorialSimulacionDAO.obtenerPorProyecto(p.getId());

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append(" REPORTE COMPLETO: ").append(p.getNombre()).append("\n");
        sb.append("========================================\n\n");

        sb.append("1. MODELO DEFINIDO:\n");
        sb.append("--------------------\n");
        sb.append("ESTADOS:\n");
        for (Estado e : estados) {
            sb.append(" - ").append(e.getNombre()).append(" (").append(e.getDescripcion()).append(")");
            if (e.isEsInicial()) sb.append(" [INICIAL]");
            sb.append("\n");
        }

        sb.append("\nTRANSICIONES:\n");
        for (Transicion t : transiciones) {
            sb.append(" - ID ").append(t.getEstadoOrigenId()).append(" --> ").append(t.getEstadoDestinoId())
                    .append(" | Evento: ").append(t.getEvento()).append("\n");
        }

        sb.append("\n\n2. HISTORIAL DE SIMULACIONES:\n");
        sb.append("-----------------------------\n");
        if (historial.isEmpty()) {
            sb.append("No hay registros de simulación.\n");
        } else {
            int i = 1;
            for (HistorialSimulacion h : historial) {
                sb.append("\nSimulación #").append(i++).append(":\n");
                sb.append(h.getLogJson()).append("\n");
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte del Proyecto");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo de texto", "*.txt"));
        File file = fileChooser.showSaveDialog(lienzo.getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.write(sb.toString());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Reporte exportado con éxito.");
                alert.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}