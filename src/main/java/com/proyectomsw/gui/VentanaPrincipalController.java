package com.proyectomsw.gui;

import com.proyectomsw.core.Proyecto;
import com.proyectomsw.database.ProyectoDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import java.util.List;
import java.util.Optional;

public class VentanaPrincipalController {

    @FXML
    private Label textoBienvenida;

    @FXML
    private ListView<Proyecto> listaProyectos;

    @FXML
    public void initialize() {
        cargarProyectosEnLista();
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

    private void cargarProyectosEnLista() {
        // Limpiamos la lista visual por si tenía algo antes
        listaProyectos.getItems().clear();

        // Pedimos todos los proyectos al DAO
        List<Proyecto> proyectosGuardados = ProyectoDAO.obtenerTodos();

        // Los metemos todos en la lista visual de JavaFX
        listaProyectos.getItems().addAll(proyectosGuardados);
    }

}