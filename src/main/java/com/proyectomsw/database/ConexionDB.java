package com.proyectomsw.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {
    private static final String URL = "jdbc:sqlite:formalmodel.db";
    private static Connection conexion = null;

    public static Connection conectar(){
        if(conexion == null){
            try {
                conexion = DriverManager.getConnection(URL);
                System.out.println("Conexion a SQLite correcta");

                inicializarBaseDeDatos();

            } catch (SQLException e){
                System.out.println("Error al conectar con la base de datos");
                e.printStackTrace();
            }
        }
        return conexion;
    }
public static void desconectar(){
    try {
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
            System.out.println("Conexion a SQLite cerrada");
        }
    }catch (SQLException e){
        e.printStackTrace();
    }
}

private static void inicializarBaseDeDatos() throws SQLException {
    String sqlProyecto = """
            CREATE TABLE IF NOT EXISTS Proyecto (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT,
                fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP
            );
            """;

    String sqlEstado = """
            CREATE TABLE IF NOT EXISTS Estado (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                proyecto_id INTEGER,
                nombre TEXT NOT NULL,
                descripcion TEXT,
                es_inicial BOOLEAN DEFAULT 0,
                propiedades_json TEXT,
                FOREIGN KEY (proyecto_id) REFERENCES Proyecto(id) ON DELETE CASCADE
            );
            """;

    String sqlTransicion = """
            CREATE TABLE IF NOT EXISTS Transicion (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                proyecto_id INTEGER,
                estado_origen_id INTEGER,
                estado_destino_id INTEGER,
                evento TEXT NOT NULL,
                condicion_disparo TEXT,
                FOREIGN KEY (proyecto_id) REFERENCES Proyecto(id) ON DELETE CASCADE,
                FOREIGN KEY (estado_origen_id) REFERENCES Estado(id) ON DELETE CASCADE,
                FOREIGN KEY (estado_destino_id) REFERENCES Estado(id) ON DELETE CASCADE
            );
            """;

    String sqlPropiedad = """
            CREATE TABLE IF NOT EXISTS Propiedad (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                proyecto_id INTEGER,
                nombre_propiedad TEXT NOT NULL,
                expresion TEXT NOT NULL,
                estado_especifico_id INTEGER,
                FOREIGN KEY (proyecto_id) REFERENCES Proyecto(id) ON DELETE CASCADE,
                FOREIGN KEY (estado_especifico_id) REFERENCES Estado(id) ON DELETE SET NULL
            );
            """;

    String sqlHistorial = """
            CREATE TABLE IF NOT EXISTS HistorialSimulacion (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                proyecto_id INTEGER,
                fecha_simulacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                log_json TEXT,
                FOREIGN KEY (proyecto_id) REFERENCES Proyecto(id) ON DELETE CASCADE
            );
            """;
    try (Statement stmt = conexion.createStatement()){
        stmt.execute("PRAGMA foreign_keys =ON;");
        stmt.execute(sqlProyecto);
        stmt.execute(sqlEstado);
        stmt.execute(sqlTransicion);
        stmt.execute(sqlPropiedad);
        stmt.execute(sqlHistorial);

        System.out.println(" Tablas de la base de datos verificadas/creadas con éxito.");
    } catch (SQLException e) {
        System.out.println(" Error al inicializar las tablas de la base de datos.");
        e.printStackTrace();

}
}
}
