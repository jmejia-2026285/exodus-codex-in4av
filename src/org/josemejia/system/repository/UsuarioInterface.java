package org.josemejia.system.repository;

import java.util.List;
import org.josemejia.system.model.Usuario;

public interface UsuarioInterface {

    void crear(Usuario usuario);

    Usuario buscarPorUsuarioYPassword(String usuario, String password);

    List<Usuario> listar();

    Usuario buscarPorId(String idUsuario);
    void actualizar(Usuario usuario);
    void eliminar(String idUsuario);
}
