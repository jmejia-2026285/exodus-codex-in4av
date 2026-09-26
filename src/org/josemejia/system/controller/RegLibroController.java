package org.josemejia.system.controller;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.josemejia.system.model.Libro;
import org.josemejia.system.model.Usuario;
import org.josemejia.system.service.LibroService;
import org.josemejia.system.utils.AlertUtils;
import org.josemejia.system.utils.AlertUtils.TipoNotificacion;
import org.josemejia.system.utils.AnimationUtils;
import org.josemejia.system.utils.ImagenUtils;
import org.josemejia.system.utils.SceneManager;
import org.josemejia.system.utils.SesionManager;
import org.josemejia.system.utils.ValidationsUtils;
import org.josemejia.system.utils.ViewFactory;

public class RegLibroController {

    @FXML
    private VBox raiz;
    @FXML
    private TextField txtTitulo, txtAutor, txtEditorial, txtAnio, txtIsbn, txtCopias;
    @FXML
    private ImageView imgPortadaPreview;
    @FXML
    private Button btnSeleccionarPortada, btnRegistrar, btnCancelar;
    @FXML
    private Label lblError;

    private final LibroService libroService = new LibroService();
    private final ViewFactory viewFactory = new ViewFactory();
    private String rutaPortadaActual;

    @FXML
    private void initialize() {
        AnimationUtils.aplicarFadeIn(raiz);
        AnimationUtils.aplicarEfectoHover(btnRegistrar);
        AnimationUtils.aplicarEfectoHover(btnCancelar);
        AnimationUtils.aplicarEfectoHover(btnSeleccionarPortada);
        AnimationUtils.aplicarFocoAnimado(txtTitulo);
        AnimationUtils.aplicarFocoAnimado(txtAutor);
        AnimationUtils.aplicarFocoAnimado(txtEditorial);
        AnimationUtils.aplicarFocoAnimado(txtAnio);
        AnimationUtils.aplicarFocoAnimado(txtIsbn);
        AnimationUtils.aplicarFocoAnimado(txtCopias);
        ImagenUtils.aplicarEsquinasRedondeadas(imgPortadaPreview, 10);
        lblError.setText("");
    }

    @FXML
    private void handleSeleccionarPortada() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar portada del libro");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File archivo = selector.showOpenDialog(SceneManager.getInstanciaSceneManager().getStagePrincipal());
        if (archivo != null) {
            try {
                rutaPortadaActual = ImagenUtils.copiarPortadaAAppData(archivo);
                imgPortadaPreview.setImage(ImagenUtils.cargarPortadaDesdeRuta(rutaPortadaActual));
            } catch (RuntimeException e) {
                mostrarError(e.getMessage());
            }
        }
    }

    @FXML
    private void handleRegistrar() {
        String error = validarFormulario();
        if (error != null) {
            mostrarError(error);
            AlertUtils.mostrarAlertaPersonalizada("Datos incompletos", error, TipoNotificacion.ADVERTENCIA);
            return;
        }
        try {
            Usuario usuario = SesionManager.getInstanciaSessionManager().getUsuarioActual();
            Libro libro = construirLibro(new Libro());
            libroService.crear(libro, usuario);
            AlertUtils.mostrarAlertaPersonalizada("Catálogo", "El libro se registró correctamente.", TipoNotificacion.LIBRO_GUARDADO);
            handleCancelar();
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
            AlertUtils.mostrarAlertaPersonalizada("Catálogo", e.getMessage(), TipoNotificacion.ERROR);
        }
    }

    @FXML
    private void handleCancelar() {
        viewFactory.viewDashboard();
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

    private Libro construirLibro(Libro libro) {
        libro.setTitulo(txtTitulo.getText().trim());
        libro.setAutorPrincipal(txtAutor.getText().trim());
        libro.setEditorial(txtEditorial.getText().trim());
        libro.setAnioPublicacion(Integer.parseInt(txtAnio.getText().trim()));
        libro.setIsbn(txtIsbn.getText().trim());
        libro.setCopiasDisponibles(Integer.parseInt(txtCopias.getText().trim()));
        libro.setPortada(rutaPortadaActual);
        return libro;
    }

    private void limpiarCampos() {
        rutaPortadaActual = null;
        txtTitulo.clear();
        txtAutor.clear();
        txtEditorial.clear();
        txtAnio.clear();
        txtIsbn.clear();
        txtCopias.clear();
        imgPortadaPreview.setImage(null);
        lblError.setText("");
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        AnimationUtils.aplicarFadeIn(lblError);
        AnimationUtils.aplicarSacudida(lblError);
    }
}