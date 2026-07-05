package com.proyectomsw.database;

import com.proyectomsw.core.Transicion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransicionDAO {

    public static int insertar(Transicion transicion) {
        String sql = "INSERT INTO Transicion (proyecto_id, estado_origen_id, estado_destino_id, evento, condicion_disparo) VALUES (?, ?, ?, ?, ?)";
        int idGenerado = -1;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, transicion.getProyectoId());
            pstmt.setInt(2, transicion.getEstadoOrigenId());
            pstmt.setInt(3, transicion.getEstadoDestinoId());
            pstmt.setString(4, transicion.getEvento());
            pstmt.setString(5, transicion.getCondicionDisparo());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idGenerado = generatedKeys.getInt(1);
                    transicion.setId(idGenerado);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar Transición en BD: " + e.getMessage());
        }
        return idGenerado;
    }
}