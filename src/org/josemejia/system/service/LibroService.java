package org.josemejia.system.service;

import org.josemejia.system.model.Libro;
import org.josemejia.system.model.Usuario;
import org.josemejia.system.repository.LibroRepository;

import java.util.List;

public class LibroService {

    private final LibroRepository libroRepository = new LibroRepository();

    public List<Libro> listar() {
        return libroRepository.listar();
    }

    public List<Libro> buscar(String termino) {
        if (termino == null || termino.isBlank()) {
            return libroRepository.listar();
        }
        return libroRepository.buscar(termino.trim());
    }

    public void crear(Libro libro, Usuario usuarioActual) {
        verificarSesionActiva(usuarioActual);
        libroRepository.crear(libro);
    }

    public void actualizar(Libro libro, Usuario usuarioActual) {
        verificarSesionActiva(usuarioActual);
        libroRepository.actualizar(libro);
    }

    public void eliminar(Libro libro, Usuario usuarioActual) {
        verificarSesionActiva(usuarioActual);
        libroRepository.eliminar(libro.getIdLibro());
    }

    private void verificarSesionActiva(Usuario usuarioActual) {
        if (usuarioActual == null) {
            throw new IllegalStateException("Debes iniciar sesión para modificar el acervo.");
        }
    }
}
