package org.josemejia.system.service;

import org.josemejia.system.model.Usuario;
import org.josemejia.system.repository.UsuarioRepository;
import java.util.List;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    public void registrarBibliotecario(Usuario nuevoBibliotecario, Usuario usuarioActual) {
        verificarEsJefe(usuarioActual);
        nuevoBibliotecario.setRol("Bibliotecario");
        usuarioRepository.crear(nuevoBibliotecario);
    }

    public void actualizarBibliotecario(Usuario usuario, Usuario usuarioActual) {
        verificarEsJefe(usuarioActual);
        usuarioRepository.actualizar(usuario);
    }

    public List<Usuario> listarBibliotecarios(Usuario usuarioActual) {
        verificarEsJefe(usuarioActual);
        return usuarioRepository.listar();
    }

    public Usuario buscarPorId(String idUsuario, Usuario usuarioActual) {
        verificarEsJefe(usuarioActual);
        return usuarioRepository.buscarPorId(idUsuario);
    }

    public void eliminarBibliotecario(String idUsuario, Usuario usuarioActual) {
        verificarEsJefe(usuarioActual);

        Usuario objetivo = usuarioRepository.buscarPorId(idUsuario);
        if (objetivo == null) {
            throw new IllegalStateException("Ese bibliotecario ya no existe.");
        }
        if (objetivo.esBibliotecarioJefe()) {
            throw new IllegalStateException("No se puede eliminar a un Bibliotecario Jefe.");
        }

        usuarioRepository.eliminar(idUsuario);
    }

    private void verificarEsJefe(Usuario usuarioActual) {
        if (usuarioActual == null || !usuarioActual.esBibliotecarioJefe()) {
            throw new IllegalStateException("Solo el Bibliotecario Jefe puede registrar nuevas cuentas.");
        }
    }
}
