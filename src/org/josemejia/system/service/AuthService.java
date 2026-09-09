package org.josemejia.system.service;

import org.josemejia.system.model.Usuario;
import org.josemejia.system.repository.UsuarioRepository;

public class AuthService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    public Usuario login(String usuario, String password) {
        return usuarioRepository.buscarPorUsuarioYPassword(usuario, password);
    }
}