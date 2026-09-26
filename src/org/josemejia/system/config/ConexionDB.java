package org.josemejia.system.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static ConexionDB instanciaConexionDB;
    private Connection connection;

    private ConexionDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + Enviroment.LOCATION_SERVICE + "/" + Enviroment.DATA_BASE
                    + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                    Enviroment.USER,
                    Enviroment.PASSWORD);

        } catch (ClassNotFoundException classNotFound) {
            System.out.println("Error clase no encontrada");

        } catch (SQLException sqlException) {
            System.out.println("Error de conexion a db: " + sqlException.getMessage());

        } catch (Exception e) {
            System.out.println("Error padre " + e.getMessage());
        }
    }

    public static ConexionDB getInstanciaConexionDB() {
        if (instanciaConexionDB == null) {
            instanciaConexionDB = new ConexionDB();
        }

        return instanciaConexionDB;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {

                Class.forName("com.mysql.cj.jdbc.Driver");

                connection = DriverManager.getConnection(
                        "jdbc:mysql://" + Enviroment.LOCATION_SERVICE + "/" + Enviroment.DATA_BASE
                        + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                        Enviroment.USER,
                        Enviroment.PASSWORD);
            }

        } catch (SQLException sqlException) {
            throw new RuntimeException(
                    "Error al validar/reconectar: " + sqlException.getMessage(),
                    sqlException
            );

        } catch (ClassNotFoundException classNotFound) {
            System.out.println("Error clase no encontrada");
        }

        return connection;
    }

    public void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error al cerrar conexion: " + e.getMessage()
            );
        }
    }
}