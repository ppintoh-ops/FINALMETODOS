package com.proyectomsw.database;

import com.proyectomsw.core.Estado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EstadoDAO {

    public static int insertar(Estado estado) {
        String sql = "INSERT INTO Estado (proyecto_id, nombre, descripcion, es_inicial, propiedades_json) VALUES (?, ?, ?, ?, ?)";
        int idGenerado = -1;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, estado.getProyectoId());
            pstmt.setString(2, estado.getNombre());
            pstmt.setString(3, estado.getDescripcion());
            pstmt.setInt(4, estado.isEsInicial() ? 1 : 0); // SQLite no tiene booleano, 1 es true, 0 es false
            pstmt.setString(5, estado.getPropiedadesJson());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idGenerado = generatedKeys.getInt(1);
                    estado.setId(idGenerado);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar Estado en BD: " + e.getMessage());
        }
        return idGenerado;
    }
}