package org.josemejia.system.repository;

import org.josemejia.system.model.Libro;

import java.util.List;

public interface LibroInterface {
    void crear(Libro libro);
    List<Libro> listar();
    List<Libro> buscar(String termino);
    void actualizar(Libro libro);
    void eliminar(int idLibro);
}