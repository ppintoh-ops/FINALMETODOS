package com.proyectomsw.database;

import com.proyectomsw.core.Proyecto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProyectoDAO {


    public static boolean insertar(Proyecto proyecto) {
        String sql = "INSERT INTO Proyecto(nombre, descripcion) VALUES(?, ?)";


        Connection conn = ConexionDB.conectar();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, proyecto.getNombre());
            pstmt.setString(2, proyecto.getDescripcion());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println(" Error al insertar el proyecto: " + e.getMessage());
            return false;
        }
    }


    public static List<Proyecto> obtenerTodos() {
        List<Proyecto> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, fecha_creacion FROM Proyecto";

        Connection conn = ConexionDB.conectar();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Proyecto p = new Proyecto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("fecha_creacion")
                );
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println(" Error al obtener los proyectos: " + e.getMessage());
        }
        return lista;
    }


    public static boolean eliminar(int id) {
        String sql = "DELETE FROM Proyecto WHERE id = ?";

        Connection conn = ConexionDB.conectar();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println(" Error al eliminar el proyecto: " + e.getMessage());
            return false;
        }
    }
}