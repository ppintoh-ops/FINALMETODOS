package com.proyectomsw.gui;

import com.proyectomsw.core.Proyecto;
import com.proyectomsw.database.ProyectoDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;

public class VentanaPrincipalController {

    @FXML
    private Label textoBienvenida;

    @FXML
    public void initialize() {
        textoBienvenida.setText("¡El motor de simulación está conectado y listo!");
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
}