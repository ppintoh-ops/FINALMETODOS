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
        String sql = "INSERT INTO Proyecto (id, nombre, descripcion) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getId());
            pstmt.setString(2, p.getNombre());
            pstmt.setString(3, p.getDescripcion());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar: " + e.getMessage());
            return false;
        }
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