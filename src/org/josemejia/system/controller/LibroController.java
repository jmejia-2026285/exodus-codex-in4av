/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.josemejia.system.controller;

/**
 *
 * @author informatica
 */
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

public class LibroController {

    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnBuscar, btnVolver;
    @FXML
    private Label lblTotalLibros;
    @FXML
    private ScrollPane scrollCatalogo; //
    @FXML
    private FlowPane panelTarjetas; 
    @FXML
    private TextField txtTitulo, txtAutor, txtEditorial, txtAnio, txtIsbn, txtCopias;
    @FXML
    private ImageView imgPortadaPreview;
    @FXML
    private Button btnSeleccionarPortada, btnGuardar, btnLimpiar, btnEliminar;

    private final LibroService libroService = new LibroService();
    private final ViewFactory viewFactory = new ViewFactory();
    private Libro libroSeleccionado;
    private String rutaPortadaActual;

    @FXML
    private void initialize() {
        AnimationUtils.aplicarFadeIn(panelTarjetas);
        AnimationUtils.aplicarEfectoHover(btnBuscar);
        AnimationUtils.aplicarEfectoHover(btnVolver);
        AnimationUtils.aplicarEfectoHover(btnGuardar);
        AnimationUtils.aplicarEfectoHover(btnLimpiar);
        AnimationUtils.aplicarEfectoHover(btnEliminar);
        AnimationUtils.aplicarFocoAnimado(txtBuscar);
        AnimationUtils.aplicarFocoAnimado(txtTitulo);
        AnimationUtils.aplicarFocoAnimado(txtAutor);
        AnimationUtils.aplicarFocoAnimado(txtEditorial);
        AnimationUtils.aplicarFocoAnimado(txtAnio);
        AnimationUtils.aplicarFocoAnimado(txtIsbn);
        AnimationUtils.aplicarFocoAnimado(txtCopias);
        ImagenUtils.aplicarEsquinasRedondeadas(imgPortadaPreview, 10);
        cargarCatalogo(libroService.listar());
        actualizarModoFormulario();

    }

    @FXML
    private void handleBuscar() {
        try {
            List<Libro> resultado = libroService.buscar(txtBuscar.getText());
            cargarCatalogo(resultado);
            if (resultado.isEmpty()) {
                AlertUtils.mostrarAlertaPersonalizada("Catálogo", "No se encontraron libros con ese criterio.", TipoNotificacion.SIN_RESULTADOS);
            }
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    @FXML
    private void handleVolver() {
        viewFactory.viewDashboard();
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
                mostrarError(e);
            }
        }
    }

            @FXML
    private void handleGuardar() {
        if (libroSeleccionado == null) {
            AlertUtils.mostrarAlertaPersonalizada("Catálogo", "Selecciona un libro para editarlo.", TipoNotificacion.ADVERTENCIA);
            return;
        }
        String error = validarFormulario();
        if (error != null) {
            AlertUtils.mostrarAlertaPersonalizada("Datos incompletos", error, TipoNotificacion.ADVERTENCIA);
            return;
        }
        try {
            Usuario usuario = SesionManager.getInstanciaSessionManager().getUsuarioActual();
            Libro libro = construirLibro(libroSeleccionado);
            libroService.actualizar(libro, usuario);
            AlertUtils.mostrarAlertaPersonalizada("Catálogo", "El libro se actualizó correctamente.", TipoNotificacion.LIBRO_GUARDADO);
            handleLimpiar();
            cargarCatalogo(libroService.listar());
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    @FXML
    private void handleEliminar() {
        if (libroSeleccionado == null) {
            AlertUtils.mostrarAlertaPersonalizada("Catálogo", "Selecciona un libro para eliminar.", TipoNotificacion.ADVERTENCIA);
            return;
        }
        try {
            libroService.eliminar(libroSeleccionado, SesionManager.getInstanciaSessionManager().getUsuarioActual());
            AlertUtils.mostrarAlertaPersonalizada("Catálogo", "El libro se eliminó del acervo.", TipoNotificacion.LIBRO_ELIMINADO);
            handleLimpiar();
            cargarCatalogo(libroService.listar());
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    @FXML
    private void handleLimpiar() {
        libroSeleccionado = null;
        rutaPortadaActual = null;
        txtTitulo.clear();
        txtAutor.clear();
        txtEditorial.clear();
        txtAnio.clear();
        txtIsbn.clear();
        txtCopias.clear();
        imgPortadaPreview.setImage(null);
        actualizarModoFormulario();
    }

    private void cargarCatalogo(List<Libro> libros) {
        panelTarjetas.getChildren().clear();
        libros.forEach(libro -> panelTarjetas.getChildren().add(crearTarjeta(libro)));
        lblTotalLibros.setText(libros.size() == 1 ? "1 título registrado" : libros.size() + " títulos registrados");
    }

    private VBox crearTarjeta(Libro libro) {
        ImageView portada = new ImageView(ImagenUtils.cargarPortadaDesdeRuta(libro.getPortada()));
        portada.setFitWidth(120);
        portada.setFitHeight(150);
        portada.getStyleClass().add("libro-tarjeta-imagen");
        ImagenUtils.aplicarEsquinasRedondeadas(portada, 10);

        Label titulo = new Label(libro.getTitulo());
        titulo.getStyleClass().add("libro-tarjeta-titulo");
        titulo.setWrapText(true);
        titulo.setMaxWidth(150);

        Label autor = new Label(libro.getAutorPrincipal());
        autor.getStyleClass().add("libro-tarjeta-autor");

        Label copias = new Label(libro.getCopiasDisponibles() + " copias");
        copias.getStyleClass().add(libro.getCopiasDisponibles() > 0 ? "chip-exito" : "chip-error");

        Button btnEditar = new Button("Editar");
        btnEditar.getStyleClass().add("boton-tarjeta");
        btnEditar.setOnAction(e -> seleccionarLibro(libro));

        Button btnComprobante = new Button("Imprimir comprobante");
        btnComprobante.getStyleClass().add("boton-tarjeta");
        btnComprobante.setOnAction(e -> abrirComprobante(libro));

        Button btnEliminarTarjeta = new Button("Eliminar");
        btnEliminarTarjeta.getStyleClass().add("boton-tarjeta-peligro");
        btnEliminarTarjeta.setOnAction(e -> {
            seleccionarLibro(libro);
            handleEliminar();
        });

        HBox acciones = new HBox(8, btnEditar, btnComprobante, btnEliminarTarjeta);
        acciones.setAlignment(Pos.CENTER);
        VBox tarjeta = new VBox(8, portada, titulo, autor, copias, acciones);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.setPrefWidth(170);
        tarjeta.getStyleClass().add("libro-tarjeta");
        AnimationUtils.aplicarSlideIn(tarjeta, 24);
        AnimationUtils.aplicarEfectoHoverTarjeta(tarjeta);
        return tarjeta;
    }

    private void abrirComprobante(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/josemejia/system/view/ComprobantePrestamoView.fxml"));
            Parent raizComprobante = loader.load();

            ComprobantePrestamoController controlador = loader.getController();
            controlador.setLibro(libro);

            Stage stageComprobante = new Stage();
            stageComprobante.initOwner(SceneManager.getInstanciaSceneManager().getStagePrincipal());
            stageComprobante.initModality(Modality.WINDOW_MODAL);
            stageComprobante.setTitle("Exodus Codex - Comprobante de préstamo");
            stageComprobante.setResizable(false);
            stageComprobante.setScene(new Scene(raizComprobante));

            controlador.setStage(stageComprobante);

            stageComprobante.showAndWait();
        } catch (IOException e) {
            mostrarError(new RuntimeException("No se pudo abrir el comprobante de préstamo.", e));
        }
    }

    private void seleccionarLibro(Libro libro) {
        libroSeleccionado = libro;
        rutaPortadaActual = libro.getPortada();
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutorPrincipal());
        txtEditorial.setText(libro.getEditorial());
        txtAnio.setText(String.valueOf(libro.getAnioPublicacion()));
        txtIsbn.setText(libro.getIsbn());
        txtCopias.setText(String.valueOf(libro.getCopiasDisponibles()));
        imgPortadaPreview.setImage(ImagenUtils.cargarPortadaDesdeRuta(libro.getPortada()));
        actualizarModoFormulario();
    }

        private void actualizarModoFormulario() {
        boolean editando = libroSeleccionado != null;
        btnGuardar.setText("Actualizar");
        btnGuardar.setDisable(!editando);
        btnEliminar.setDisable(!editando);
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

    private void mostrarError(RuntimeException e) {
    String mensaje = e.getMessage();
    TipoNotificacion tipo = mensaje != null && mensaje.contains("iniciar sesión")
            ? TipoNotificacion.ACCESO_DENEGADO
            : TipoNotificacion.ERROR;
    AlertUtils.mostrarAlertaPersonalizada("Catálogo", mensaje, tipo);
}
}
