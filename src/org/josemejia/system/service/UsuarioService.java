package org.josemejia.system.service;

import org.josemejia.system.model.Usuario;
import org.josemejia.system.repository.UsuarioRepository;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    public void registrarBibliotecario(Usuario nuevoBibliotecario, Usuario usuarioActual) {
        verificarEsJefe(usuarioActual);
        nuevoBibliotecario.setRol("Bibliotecario");
        usuarioRepository.crear(nuevoBibliotecario);
    }

    private void verificarEsJefe(Usuario usuarioActual) {
        if (usuarioActual == null || !usuarioActual.esBibliotecarioJefe()) {
            throw new IllegalStateException("Solo el Bibliotecario Jefe puede registrar nuevas cuentas.");
        }
    }
}