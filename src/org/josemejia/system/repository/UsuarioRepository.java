package org.josemejia.system.repository;

import org.josemejia.system.config.ConexionDB;
import org.josemejia.system.model.Usuario;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

public class UsuarioRepository implements UsuarioInterface {

    private static final String SP_CREAR = "{call sp_agregar_usuario(?, ?, ?, ?, ?, ?, ?)}";
    private static final String SP_LOGIN = "{call sp_login_usuario(?, ?)}";

    @Override
    public void crear(Usuario usuario) {
        Connection conexion = ConexionDB.getInstanciaConexionDB().getConnection();
        try (CallableStatement sentencia = conexion.prepareCall(SP_CREAR)) {

            String nuevoId = obtenerSiguienteId(conexion);

            sentencia.setString(1, nuevoId);
            sentencia.setString(2, usuario.getNombre());
            sentencia.setString(3, usuario.getApellido());
            sentencia.setString(4, usuario.getCorreo());
            sentencia.setString(5, usuario.getUsuario());
            sentencia.setString(6, usuario.getPassword());
            sentencia.setString(7, usuario.getRol());

            sentencia.executeUpdate();
            usuario.setIdUsuario(nuevoId);

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalStateException("Ya existe una cuenta con ese correo o usuario.", e);
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo registrar el usuario.", e);
        }
    }

    @Override
    public Usuario buscarPorUsuarioYPassword(String usuario, String password) {
        Connection conexion = ConexionDB.getInstanciaConexionDB().getConnection();
        try (CallableStatement sentencia = conexion.prepareCall(SP_LOGIN)) {

            sentencia.setString(1, usuario);
            sentencia.setString(2, password);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    Usuario encontrado = new Usuario();
                    encontrado.setIdUsuario(resultado.getString("id_usuario"));
                    encontrado.setNombre(resultado.getString("nombre"));
                    encontrado.setApellido(resultado.getString("apellido"));
                    encontrado.setCorreo(resultado.getString("correo"));
                    encontrado.setUsuario(resultado.getString("usuario"));
                    encontrado.setRol(resultado.getString("rol"));
                    return encontrado;
                }
                return null;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo validar el usuario.", e);
        }
    }

    private String obtenerSiguienteId(Connection conexion) throws SQLException {
        try (Statement statement = conexion.createStatement();
             ResultSet resultado = statement.executeQuery(
                 "SELECT COALESCE(MAX(CAST(id_usuario AS UNSIGNED)), 0) + 1 AS siguiente FROM USUARIOS")) {
            resultado.next();
            return String.valueOf(resultado.getInt("siguiente"));
        }
    }
}