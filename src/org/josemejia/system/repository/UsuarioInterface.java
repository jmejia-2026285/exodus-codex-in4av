
package org.josemejia.system.repository;

import org.josemejia.system.model.Usuario;

public interface UsuarioInterface {
    void crear(Usuario usuario);
    Usuario buscarPorUsuarioYPassword(String usuario, String password);
}