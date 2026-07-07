package com.proyectomsw.gui;

import com.proyectomsw.core.AppSession;
import com.proyectomsw.core.Proyecto;
import com.proyectomsw.database.ProyectoDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.*;

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

            int idInteligente = ProyectoDAO.obtenerSiguienteIdLibre();
            nuevoProyecto.setId(idInteligente);

            if (ProyectoDAO.insertar(nuevoProyecto)) {
                textoBienvenida.setText("Proyecto guardado exitosamente.");
                textoBienvenida.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
                cargarProyectosEnLista();
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
            AppSession.setCurrentProyecto(seleccionado);
            if (ProyectoDAO.eliminar(seleccionado.getId())) {
                textoBienvenida.setText(" Proyecto '" + seleccionado.getNombre() + "' eliminado.");
                cargarProyectosEnLista();
            }
        }
    }

    @FXML
    public void abrirProyecto(ActionEvent event) {
        Proyecto seleccionado = listaProyectos.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {

            com.proyectomsw.core.AppSession.setCurrentProyecto(seleccionado);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditorProyecto.fxml"));
                Parent raiz = loader.load();

                javafx.stage.Stage escenario = (javafx.stage.Stage) btnAbrir.getScene().getWindow();
                escenario.setScene(new javafx.scene.Scene(raiz, 800, 600));
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