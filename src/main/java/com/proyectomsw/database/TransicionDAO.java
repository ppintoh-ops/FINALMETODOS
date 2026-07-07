package com.proyectomsw.database;

import com.proyectomsw.core.Transicion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransicionDAO {

    public static int insertar(Transicion transicion) {
        String sql = "INSERT INTO Transicion (proyecto_id, estado_origen_id, estado_destino_id, evento, condicion_disparo) VALUES (?, ?, ?, ?, ?)";
        int idGenerado = -1;

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, transicion.getProyectoId());
            pstmt.setInt(2, transicion.getEstadoOrigenId());
            pstmt.setInt(3, transicion.getEstadoDestinoId());
            pstmt.setString(4, transicion.getEvento());
            pstmt.setString(5, transicion.getCondicionDisparo());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                    transicion.setId(idGenerado);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar Transición: " + e.getMessage());
        }
        return idGenerado;
    }

    public static List<Transicion> obtenerTransicionesPorProyecto(int proyectoId) {
        List<Transicion> transiciones = new ArrayList<>();
        String sql = "SELECT * FROM Transicion WHERE proyecto_id = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, proyectoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transicion t = new Transicion();
                    t.setId(rs.getInt("id"));
                    t.setProyectoId(rs.getInt("proyecto_id"));
                    t.setEstadoOrigenId(rs.getInt("estado_origen_id"));
                    t.setEstadoDestinoId(rs.getInt("estado_destino_id"));
                    t.setEvento(rs.getString("evento"));
                    t.setCondicionDisparo(rs.getString("condicion_disparo"));
                    transiciones.add(t);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener transiciones: " + e.getMessage());
        }
        return transiciones;
    }
}