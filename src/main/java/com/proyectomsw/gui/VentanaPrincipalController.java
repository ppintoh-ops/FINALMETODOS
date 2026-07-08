package com.proyectomsw.gui;

import com.proyectomsw.core.AppSession;
import com.proyectomsw.core.Proyecto;
import com.proyectomsw.database.ProyectoDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;
import java.util.Optional;

public class VentanaPrincipalController {

    @FXML private Label textoBienvenida;
    @FXML private ListView<Proyecto> listaProyectos;

    @FXML private Button btnAbrir;
    @FXML private Button btnEliminar;

    @FXML
    public void initialize() {
        cargarProyectosEnLista();

        listaProyectos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean haySeleccion = (newValue != null);
            btnAbrir.setDisable(!haySeleccion);
            btnEliminar.setDisable(!haySeleccion);
        });

        listaProyectos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && listaProyectos.getSelectionModel().getSelectedItem() != null) {
                abrirProyecto(null);
            }
        });
    }

    @FXML
    public void crearProyecto(ActionEvent event) {
        TextInputDialog dialogo = new TextInputDialog("Nuevo Proyecto");
        dialogo.setTitle("Crear Proyecto");
        dialogo.setHeaderText("Inicializar nuevo modelo formal");
        dialogo.setContentText("Por favor, ingresa el nombre del proyecto:");

        Optional<String> resultado = dialogo.showAndWait();

        resultado.ifPresent(nombre -> {
            Proyecto nuevoProyecto = new Proyecto();
            nuevoProyecto.setNombre(nombre);
            nuevoProyecto.setDescripcion("Proyecto creado desde la interfaz gráfica.");



            if (ProyectoDAO.insertar(nuevoProyecto)) {
                AppSession.setCurrentProyecto(nuevoProyecto);

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditorProyecto.fxml"));
                    Parent raiz = loader.load();

                    Stage escenario = (Stage) btnAbrir.getScene().getWindow();
                    escenario.setScene(new Scene(raiz, 800, 600));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                textoBienvenida.setText(" Error al guardar el proyecto.");
                textoBienvenida.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
            }
        });
    }

    @FXML
    public void eliminarProyecto(ActionEvent event) {
        Proyecto seleccionado = listaProyectos.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {

            if (ProyectoDAO.eliminar(seleccionado.getId())) {

                if (AppSession.getCurrentProyecto() != null && AppSession.getCurrentProyecto().getId() == seleccionado.getId()) {
                    AppSession.setCurrentProyecto(null);
                }

                textoBienvenida.setText(" Proyecto '" + seleccionado.getNombre() + "' eliminado.");
                textoBienvenida.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
                cargarProyectosEnLista();
            }
        }
    }

    @FXML
    public void abrirProyecto(ActionEvent event) {
        Proyecto seleccionado = listaProyectos.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            AppSession.setCurrentProyecto(seleccionado);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditorProyecto.fxml"));
                Parent raiz = loader.load();

                Stage escenario = (Stage) btnAbrir.getScene().getWindow();
                escenario.setScene(new Scene(raiz, 800, 600));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarProyectosEnLista() {
        listaProyectos.getItems().clear();
        List<Proyecto> proyectosGuardados = ProyectoDAO.obtenerTodos();
        listaProyectos.getItems().addAll(proyectosGuardados);
    }
}