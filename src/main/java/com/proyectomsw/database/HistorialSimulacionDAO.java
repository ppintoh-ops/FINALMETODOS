package com.proyectomsw.database;
import com.proyectomsw.core.HistorialSimulacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistorialSimulacionDAO {
    public static boolean insertar(HistorialSimulacion historial) {
        String sql = "INSERT INTO HistorialSimulacion (proyecto_id, log_json, fecha_simulacion) VALUES (?, ?, ?)";
        String fechaActual = java.time.LocalDateTime.now().toString();

        System.out.println("DEBUG SQL: proyectoId=" + historial.getProyectoId() +
                ", Log=" + historial.getLogJson() +
                ", Fecha=" + fechaActual);
        try (Connection conn = ConexionDB.getConexion();

             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, historial.getProyectoId());
            pstmt.setString(2, historial.getLogJson());
            pstmt.setString(3, LocalDateTime.now().toString());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar historial: " + e.getMessage());
            return false;
        }
    }
    public static List<HistorialSimulacion> obtenerPorProyecto(int proyectoId) {
        List<HistorialSimulacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM HistorialSimulacion WHERE proyecto_id = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, proyectoId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(new HistorialSimulacion(rs.getInt("proyecto_id"), rs.getString("log_json")));
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar historial: " + e.getMessage());
        }
        return lista;
    }
}