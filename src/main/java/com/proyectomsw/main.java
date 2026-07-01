package com.proyectomsw;
import com.proyectomsw.core.Proyecto;
import com.proyectomsw.database.ConexionDB;
import com.proyectomsw.database.ProyectoDAO;
import java.util.List;

public class main {
    public static void main(String[] args){
     System.out.println("INICIANDO SIM");
     Proyecto nuevoProyecto = new Proyecto();
     nuevoProyecto.setNombre("Simulación Red de Petri v1");
     nuevoProyecto.setDescripcion("Modelo formal para evaluar concurrencia y evitar bloqueos.");

        System.out.println("\n Guardando proyecto en SQLite...");
        boolean guardadoExitoso = ProyectoDAO.insertar(nuevoProyecto);

        if (guardadoExitoso) {
            System.out.println(" Proyecto guardado con éxito");
        }
        System.out.println("\n Listando proyectos existentes en la Base de Datos:");
        List<Proyecto> proyectos = ProyectoDAO.obtenerTodos();

        for (Proyecto p : proyectos) {
            System.out.println("-------------------------------------------------");
            System.out.println("ID: " + p.getId());
            System.out.println("Nombre: " + p.getNombre());
            System.out.println("Descripción: " + p.getDescripcion());
            System.out.println("Creado el: " + p.getFechaCreacion());
        }
        System.out.println("-------------------------------------------------");


     ConexionDB.desconectar();
    }
}
