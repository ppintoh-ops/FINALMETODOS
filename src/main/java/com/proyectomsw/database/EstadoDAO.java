package com.proyectomsw.database;

import com.proyectomsw.core.Estado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EstadoDAO {


    public static int insertar(Estado estado) {
        String sql = "INSERT INTO Estado (proyecto_id, nombre, descripcion, es_inicial, propiedades_json) VALUES (?, ?, ?, ?, ?)";
        int idGenerado = -1;

        System.out.println("DEBUG GUARDADO: Guardando estado: " + estado.getNombre() + " | Desc: " + estado.getDescripcion());

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, estado.getProyectoId());
            pstmt.setString(2, estado.getNombre());
            pstmt.setString(3, estado.getDescripcion());

            pstmt.setInt(4, estado.isEsInicial() ? 1 : 0);

            pstmt.setString(5, estado.getPropiedadesJson() != null ? estado.getPropiedadesJson() : "{}");

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                    estado.setId(idGenerado);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL en EstadoDAO.insertar: " + e.getMessage());
        }

        return idGenerado;
    }

    public static List<Estado> obtenerPorProyecto(int idProyecto) {
        List<Estado> lista = new ArrayList<>();
        String sql = "SELECT * FROM Estado WHERE proyecto_id = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProyecto);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Estado e = new Estado();
                    e.setId(rs.getInt("id"));
                    e.setProyectoId(rs.getInt("proyecto_id"));
                    e.setNombre(rs.getString("nombre"));
                    e.setDescripcion(rs.getString("descripcion"));

                    e.setEsInicial(rs.getInt("es_inicial") == 1);

                    e.setPropiedadesJson(rs.getString("propiedades_json"));

                    lista.add(e);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error SQL en EstadoDAO.obtenerPorProyecto: " + ex.getMessage());
        }

        return lista;
    }


    public static boolean eliminar(int idEstado) {
        String sql = "DELETE FROM Estado WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idEstado);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error SQL en EstadoDAO.eliminar: " + e.getMessage());
            return false;
        }
    }
}