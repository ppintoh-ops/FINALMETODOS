package com.proyectomsw.database;

import com.proyectomsw.core.Proyecto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProyectoDAO {
    
    public static List<Proyecto> obtenerTodos() {
        List<Proyecto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Proyecto";
        try (Connection conn = ConexionDB.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Proyecto p = new Proyecto();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setFechaCreacion(rs.getString("fecha_creacion"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos: " + e.getMessage());
        }
        return lista;
    }

    public static int obtenerSiguienteIdLibre() {
        String sql = "SELECT MAX(id) + 1 AS next_id FROM Proyecto";
        try (Connection conn = ConexionDB.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("next_id");
        } catch (SQLException e) {
            System.err.println("Error al obtener ID: " + e.getMessage());
        }
        return 1;
    }

    public static boolean insertar(Proyecto p) {
        String sql = "INSERT INTO Proyecto (nombre, descripcion) VALUES (?, ?)";

        try (java.sql.Connection conn = ConexionDB.getConexion();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, p.getNombre());
            pstmt.setString(2, p.getDescripcion());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                try (java.sql.ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al insertar Proyecto: " + e.getMessage());
        }
        return false;
    }

    public static boolean eliminar(int id) {
        String sql = "DELETE FROM Proyecto WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }
}