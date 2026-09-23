package org.josemejia.system.repository;

import org.josemejia.system.config.ConexionDB;
import org.josemejia.system.model.Usuario;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository implements UsuarioInterface {

    private static final String SP_CREAR = "{call sp_agregar_usuario(?, ?, ?, ?, ?, ?, ?)}";
    private static final String SP_LOGIN = "{call sp_login_usuario(?, ?)}";
    private static final String SP_LISTAR = "{call sp_consultar_usuarios()}";
    private static final String SP_BUSCAR_POR_ID = "{call sp_consultar_usuario_por_id(?)}";
    private static final String SP_ELIMINAR = "{call sp_eliminar_usuario(?)}";

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

    @Override
    public List<Usuario> listar() {
        List<Usuario> usuarios = new ArrayList<>();
        Connection conexion = ConexionDB.getInstanciaConexionDB().getConnection();

        try (CallableStatement sentencia = conexion.prepareCall(SP_LISTAR); ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                usuarios.add(mapearUsuario(resultado));
            }

        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo listar el personal.", e);
        }

        return usuarios;
    }

    @Override
    public Usuario buscarPorId(String idUsuario) {
        Connection conexion = ConexionDB.getInstanciaConexionDB().getConnection();
        try (CallableStatement sentencia = conexion.prepareCall(SP_BUSCAR_POR_ID)) {

            sentencia.setString(1, idUsuario);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearUsuario(resultado);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar el usuario.", e);
        }
    }

    @Override
    public void eliminar(String idUsuario) {
        Connection conexion = ConexionDB.getInstanciaConexionDB().getConnection();
        try (CallableStatement sentencia = conexion.prepareCall(SP_ELIMINAR)) {

            sentencia.setString(1, idUsuario);
            sentencia.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo eliminar el bibliotecario.", e);
        }
    }

    private Usuario mapearUsuario(ResultSet resultado) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(resultado.getString("id_usuario"));
        usuario.setNombre(resultado.getString("nombre"));
        usuario.setApellido(resultado.getString("apellido"));
        usuario.setCorreo(resultado.getString("correo"));
        usuario.setUsuario(resultado.getString("usuario"));
        usuario.setRol(resultado.getString("rol"));
        return usuario;
    }

    private String obtenerSiguienteId(Connection conexion) throws SQLException {
        try (Statement statement = conexion.createStatement(); ResultSet resultado = statement.executeQuery(
                "SELECT COALESCE(MAX(CAST(id_usuario AS UNSIGNED)), 0) + 1 AS siguiente FROM usuarios")) {
            resultado.next();
            return String.valueOf(resultado.getInt("siguiente"));
        }
    }
}
