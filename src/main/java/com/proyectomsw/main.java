package com.proyectomsw;
import com.proyectomsw.database.ConexionDB;
public class main {
    public static void main(String[] args){
     System.out.println("INICIANDO SIM");

     ConexionDB.conectar();

     System.out.println("ENTORNO LISTO");

     ConexionDB.desconectar();
    }
}
