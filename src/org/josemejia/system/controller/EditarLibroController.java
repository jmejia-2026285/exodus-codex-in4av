package org.josemejia.system.controller;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.josemejia.system.model.Libro;
import org.josemejia.system.model.Usuario;
import org.josemejia.system.service.LibroService;
import org.josemejia.system.utils.AlertUtils;
import org.josemejia.system.utils.AlertUtils.TipoNotificacion;
import org.josemejia.system.utils.AnimationUtils;
import org.josemejia.system.utils.ImagenUtils;
import org.josemejia.system.utils.SesionManager;
import org.josemejia.system.utils.ValidationsUtils;

/**
 * Controlador del pop-up "Editar libro". Antes este formulario vivía pegado
 * al lado del catálogo (LibroView); ahora se abre como una ventana aparte
 * (mismo patrón que ComprobantePrestamoController) cuando se presiona
 * "Editar" en una tarjeta del catálogo.
 */
public class EditarLibroController {

    @FXML
    private TextField txtTitulo, txtAutor, txtEditorial, txtAnio, txtIsbn, txtCopias;
    @FXML
    private ImageView imgPortadaPreview;
    @FXML
    private Button btnSeleccionarPortada, btnGuardar, btnEliminar, btnCancelar;

    private final LibroService libroService = new LibroService();
    private Libro libro;
    private Stage stage;
    private String rutaPortadaActual;
    private Runnable alGuardarOEliminar;

    @FXML
    private void initialize() {
        AnimationUtils.aplicarEfectoHover(btnGuardar);
        AnimationUtils.aplicarEfectoHover(btnEliminar);
        AnimationUtils.aplicarEfectoHover(btnCancelar);
        AnimationUtils.aplicarFocoAnimado(txtTitulo);
        AnimationUtils.aplicarFocoAnimado(txtAutor);
        AnimationUtils.aplicarFocoAnimado(txtEditorial);
        AnimationUtils.aplicarFocoAnimado(txtAnio);
        AnimationUtils.aplicarFocoAnimado(txtIsbn);
        AnimationUtils.aplicarFocoAnimado(txtCopias);
        ImagenUtils.aplicarEsquinasRedondeadas(imgPortadaPreview, 10);
    }

    /**
     * Carga los datos del libro seleccionado en el catálogo dentro del
     * formulario. Debe llamarse antes de mostrar la ventana.
     */
    public void setLibro(Libro libro) {
        this.libro = libro;
        this.rutaPortadaActual = libro.getPortada();
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutorPrincipal());
        txtEditorial.setText(libro.getEditorial());
        txtAnio.setText(String.valueOf(libro.getAnioPublicacion()));
        txtIsbn.setText(libro.getIsbn());
        txtCopias.setText(String.valueOf(libro.getCopiasDisponibles()));
        imgPortadaPreview.setImage(ImagenUtils.cargarPortadaDesdeRuta(libro.getPortada()));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Callback que el catálogo usa para refrescar la grilla de tarjetas
     * después de guardar cambios o eliminar el libro.
     */
    public void setAlGuardarOEliminar(Runnable alGuardarOEliminar) {
        this.alGuardarOEliminar = alGuardarOEliminar;
    }

    @FXML
    private void handleSeleccionarPortada() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar portada del libro");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File archivo = selector.showOpenDialog(stage);
        if (archivo != null) {
            try {
                rutaPortadaActual = ImagenUtils.copiarPortadaAAppData(archivo);
                imgPortadaPreview.setImage(ImagenUtils.cargarPortadaDesdeRuta(rutaPortadaActual));
            } catch (RuntimeException e) {
                mostrarError(e);
            }
        }
    }

    @FXML
    private void handleGuardar() {
        String error = validarFormulario();
        if (error != null) {
            AlertUtils.mostrarAlertaPersonalizada("Datos incompletos", error, TipoNotificacion.ADVERTENCIA);
            return;
        }
        try {
            Usuario usuario = SesionManager.getInstanciaSessionManager().getUsuarioActual();
            construirLibro();
            libroService.actualizar(libro, usuario);
            AlertUtils.mostrarAlertaPersonalizada("Catálogo", "El libro se actualizó correctamente.", TipoNotificacion.LIBRO_GUARDADO);
            notificarCambioYCerrar();
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    @FXML
    private void handleEliminar() {
        try {
            Usuario usuario = SesionManager.getInstanciaSessionManager().getUsuarioActual();
            libroService.eliminar(libro, usuario);
            AlertUtils.mostrarAlertaPersonalizada("Catálogo", "El libro se eliminó del acervo.", TipoNotificacion.LIBRO_ELIMINADO);
            notificarCambioYCerrar();
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    @FXML
    private void handleCancelar() {
        stage.close();
    }

    private void notificarCambioYCerrar() {
        if (alGuardarOEliminar != null) {
            alGuardarOEliminar.run();
        }
        stage.close();
    }

    private String validarFormulario() {
        if (ValidationsUtils.esCampoVacio(txtTitulo.getText())) {
            return "El título es obligatorio.";
        }
        if (ValidationsUtils.esCampoVacio(txtAutor.getText())) {
            return "El autor principal es obligatorio.";
        }
        if (ValidationsUtils.esCampoVacio(txtIsbn.getText())) {
            return "El ISBN es obligatorio.";
        }
        String anioStr = txtAnio.getText().trim();
        if (!ValidationsUtils.esEnteroPositivoValido(anioStr) || !ValidationsUtils.esAnioValido(Integer.parseInt(anioStr))) {
            return "El año de publicación no es válido.";
        }
        if (!ValidationsUtils.esEnteroPositivoValido(txtCopias.getText())) {
            return "Las copias deben ser un número entero positivo.";
        }
        return null;
    }

    private void construirLibro() {
        libro.setTitulo(txtTitulo.getText().trim());
        libro.setAutorPrincipal(txtAutor.getText().trim());
        libro.setEditorial(txtEditorial.getText().trim());
        libro.setAnioPublicacion(Integer.parseInt(txtAnio.getText().trim()));
        libro.setIsbn(txtIsbn.getText().trim());
        libro.setCopiasDisponibles(Integer.parseInt(txtCopias.getText().trim()));
        libro.setPortada(rutaPortadaActual);
    }

    private void mostrarError(RuntimeException e) {
        String mensaje = e.getMessage();
        TipoNotificacion tipo = mensaje != null && mensaje.contains("iniciar sesión")
                ? TipoNotificacion.ACCESO_DENEGADO
                : TipoNotificacion.ERROR;
        AlertUtils.mostrarAlertaPersonalizada("Catálogo", mensaje, tipo);
    }
}
