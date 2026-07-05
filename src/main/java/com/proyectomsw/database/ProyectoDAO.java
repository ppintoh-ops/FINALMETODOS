package com.proyectomsw.database;

import com.proyectomsw.core.Proyecto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProyectoDAO {


    public static boolean insertar(Proyecto proyecto) {
        String sql = "INSERT INTO proyecto (id, nombre, descripcion) VALUES (?, ?, ?)";


        Connection conn = ConexionDB.conectar();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, proyecto.getId());
            pstmt.setString(2, proyecto.getNombre());
            pstmt.setString(3, proyecto.getDescripcion());
            int filasAfectadas = pstmt.executeUpdate();

            pstmt.close();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar: " + e.getMessage());
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

    public static int obtenerSiguienteIdLibre() {

        String sql = "SELECT COALESCE( " +
                "  (SELECT 1 WHERE NOT EXISTS (SELECT 1 FROM Proyecto WHERE id = 1)), " +
                "  (SELECT MIN(id + 1) FROM Proyecto WHERE (id + 1) NOT IN (SELECT id FROM Proyecto)) " +
                ") AS siguiente_id";

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int id = rs.getInt("siguiente_id");
                rs.close();
                stmt.close();
                return id;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
}