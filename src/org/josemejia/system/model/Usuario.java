package org.josemejia.system.model;

public class Usuario {

    private String idUsuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String usuario;
    private String password;
    private String rol;

    public Usuario() {
    }

    public Usuario(String idUsuario, String nombre, String apellido, String correo, String usuario, String password, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.usuario = usuario;
        this.password = password;
        this.rol = rol;
    }
    
    public boolean esBibliotecarioJefe() {
        return rol != null && rol.trim().equalsIgnoreCase("Bibliotecario Jefe");
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

}
