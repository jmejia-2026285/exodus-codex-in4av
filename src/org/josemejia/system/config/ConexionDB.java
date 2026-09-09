package org.josemejia.system.config;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.josemejia.system.config.Enviroment;

public class ConexionDB {

    private static ConexionDB instanciaConexionDB;
    private Connection connection;

    private ConexionDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + Enviroment.LOCATION_SERVICE + "/" + Enviroment.DATA_BASE
                    + "?useSSL=false&serverTimezone=UTC",
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
                        + "?useSSL=false&serverTimezone=UTC",
                        Enviroment.USER,
                        Enviroment.PASSWORD);
            }
        } catch (SQLException sqlException) {
            System.out.println("Error al validar/reconectar: " + sqlException.getMessage());
        } catch (ClassNotFoundException classNotFound) {
            System.out.println("Error clase no encontrada");
        }
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public CallableStatement prepararLlamada(String sqlCall) throws SQLException {
        return getConnection().prepareCall(sqlCall);
    }

    public void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar conexion: " + e.getMessage());
        }
    }
}
