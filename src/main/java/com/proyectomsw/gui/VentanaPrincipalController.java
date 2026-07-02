package com.proyectomsw.gui;

import com.proyectomsw.core.Proyecto;
import com.proyectomsw.database.ProyectoDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
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

            boolean exito = ProyectoDAO.insertar(nuevoProyecto);


            if (exito) {
                textoBienvenida.setText(" ¡Proyecto '" + nombre + "' guardado en la BD!");
                textoBienvenida.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #27ae60;"); // Color verde
            } else {
                textoBienvenida.setText(" Error al guardar el proyecto.");
                textoBienvenida.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;"); // Color rojo
            }
        });
    }

    @FXML
    public void eliminarProyecto(ActionEvent event) {

        Proyecto seleccionado = listaProyectos.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {

            if (ProyectoDAO.eliminar(seleccionado.getId())) {
                textoBienvenida.setText(" Proyecto '" + seleccionado.getNombre() + "' eliminado.");
                textoBienvenida.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e67e22;");
                cargarProyectosEnLista();
            }
        }
    }

    @FXML
    public void abrirProyecto(ActionEvent event) {
        Proyecto seleccionado = listaProyectos.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {

            textoBienvenida.setText(" Abriendo entorno para: " + seleccionado.getNombre());
            textoBienvenida.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #8e44ad;");


        }
    }

    private void cargarProyectosEnLista() {
        listaProyectos.getItems().clear();
        List<Proyecto> proyectosGuardados = ProyectoDAO.obtenerTodos();
        listaProyectos.getItems().addAll(proyectosGuardados);
    }

}