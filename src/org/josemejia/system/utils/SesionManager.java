/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.josemejia.system.utils;

/**
 *
 * @author mejia
 */

import org.josemejia.system.model.Usuario;

public class SesionManager {

    private static SesionManager instanciaSessionManager;
    private Usuario usuarioActual;

    private SesionManager() {
    }

    public static SesionManager getInstanciaSessionManager() {
        if (instanciaSessionManager == null) {
            instanciaSessionManager = new SesionManager();
        }
        return instanciaSessionManager;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }
}