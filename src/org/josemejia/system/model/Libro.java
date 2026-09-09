package org.josemejia.system.model;

public class Libro {

    private int idLibro;
    private String isbn;
    private String titulo;
    private String autorPrincipal;
    private String editorial;
    private int anioPublicacion;
    private int copiasDisponibles;
    private String portada;

    public Libro() {
    }

    public Libro(String isbn, String titulo, String autorPrincipal, String editorial,
            int anioPublicacion, int copiasDisponibles, String portada) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autorPrincipal = autorPrincipal;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.copiasDisponibles = copiasDisponibles;
        this.portada = portada;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutorPrincipal() {
        return autorPrincipal;
    }

    public void setAutorPrincipal(String autorPrincipal) {
        this.autorPrincipal = autorPrincipal;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public int getCopiasDisponibles() {
        return copiasDisponibles;
    }

    public void setCopiasDisponibles(int copiasDisponibles) {
        this.copiasDisponibles = copiasDisponibles;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }
}
