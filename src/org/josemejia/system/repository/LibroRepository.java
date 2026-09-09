package org.josemejia.system.repository;

import org.josemejia.system.config.ConexionDB;
import org.josemejia.system.model.Libro;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class LibroRepository implements LibroInterface {

    private static final String SP_CREAR = "{call sp_agregar_libro(?, ?, ?, ?, ?, ?, ?)}";
    private static final String SP_LISTAR = "{call sp_consultar_libros()}";
    private static final String SP_BUSCAR = "{call sp_buscar_libro(?)}";
    private static final String SP_ACTUALIZAR = "{call sp_actualizar_libro(?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final String SP_ELIMINAR = "{call sp_eliminar_libro(?)}";

    @Override
    public void crear(Libro libro) {
        Connection conexion = ConexionDB.getInstanciaConexionDB().getConnection();
        try (CallableStatement sentencia = conexion.prepareCall(SP_CREAR)) {

            sentencia.setString(1, libro.getIsbn());
            sentencia.setString(2, libro.getTitulo());
            sentencia.setString(3, libro.getAutorPrincipal());
            sentencia.setString(4, libro.getEditorial());
            sentencia.setInt(5, libro.getAnioPublicacion());
            sentencia.setInt(6, libro.getCopiasDisponibles());
            sentencia.setString(7, libro.getPortada());

            sentencia.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalStateException("Ya existe un libro registrado con ese ISBN.", e);
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo registrar el libro.", e);
        }
    }

    @Override
    public List<Libro> listar() {
        List<Libro> libros = new ArrayList<>();
        Connection conexion = ConexionDB.getInstanciaConexionDB().getConnection();

        try (CallableStatement sentencia = conexion.prepareCall(SP_LISTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                libros.add(mapearLibro(resultado));
            }

        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar los libros.", e);
        }

        return libros;
    }

    @Override
    public List<Libro> buscar(String termino) {
        List<Libro> libros = new ArrayList<>();
        Connection conexion = ConexionDB.getInstanciaConexionDB().getConnection();

        try (CallableStatement sentencia = conexion.prepareCall(SP_BUSCAR)) {

            sentencia.setString(1, termino);

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    libros.add(mapearLibro(resultado));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo realizar la búsqueda.", e);
        }

        return libros;
    }

    @Override
    public void actualizar(Libro libro) {
        Connection conexion = ConexionDB.getInstanciaConexionDB().getConnection();
        try (CallableStatement sentencia = conexion.prepareCall(SP_ACTUALIZAR)) {

            sentencia.setInt(1, libro.getIdLibro());
            sentencia.setString(2, libro.getIsbn());
            sentencia.setString(3, libro.getTitulo());
            sentencia.setString(4, libro.getAutorPrincipal());
            sentencia.setString(5, libro.getEditorial());
            sentencia.setInt(6, libro.getAnioPublicacion());
            sentencia.setInt(7, libro.getCopiasDisponibles());
            sentencia.setString(8, libro.getPortada());

            sentencia.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo actualizar el libro.", e);
        }
    }

    @Override
    public void eliminar(int idLibro) {
        Connection conexion = ConexionDB.getInstanciaConexionDB().getConnection();
        try (CallableStatement sentencia = conexion.prepareCall(SP_ELIMINAR)) {

            sentencia.setInt(1, idLibro);
            sentencia.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo eliminar el libro.", e);
        }
    }

    private Libro mapearLibro(ResultSet resultado) throws SQLException {
        Libro libro = new Libro();
        libro.setIdLibro(resultado.getInt("id_libro"));
        libro.setIsbn(resultado.getString("isbn"));
        libro.setTitulo(resultado.getString("titulo"));
        libro.setAutorPrincipal(resultado.getString("autor_principal"));
        libro.setEditorial(resultado.getString("editorial"));
        libro.setAnioPublicacion(resultado.getInt("anio_publicacion"));
        libro.setCopiasDisponibles(resultado.getInt("copias_disponibles"));
        libro.setPortada(resultado.getString("portada"));
        return libro;
    }
}