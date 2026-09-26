/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.josemejia.system.controller;

/**
 *
 * @author informatica
 */
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.Priority;
import javafx.scene.control.Tooltip;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.josemejia.system.model.Libro;
import org.josemejia.system.service.LibroService;
import org.josemejia.system.utils.AlertUtils;
import org.josemejia.system.utils.AlertUtils.TipoNotificacion;
import org.josemejia.system.utils.AnimationUtils;
import org.josemejia.system.utils.ImagenUtils;
import org.josemejia.system.utils.SceneManager;
import org.josemejia.system.utils.SesionManager;
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

    private final LibroService libroService = new LibroService();
    private final ViewFactory viewFactory = new ViewFactory();

    @FXML
    private void initialize() {
        AnimationUtils.aplicarFadeIn(panelTarjetas);
        AnimationUtils.aplicarEfectoHover(btnBuscar);
        AnimationUtils.aplicarEfectoHover(btnVolver);
        AnimationUtils.aplicarFocoAnimado(txtBuscar);
        cargarCatalogo(libroService.listar());
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

    private void cargarCatalogo(List<Libro> libros) {
        panelTarjetas.getChildren().clear();
        libros.forEach(libro -> panelTarjetas.getChildren().add(crearTarjeta(libro)));
        lblTotalLibros.setText(libros.size() == 1 ? "1 título registrado" : libros.size() + " títulos registrados");
    }

    private VBox crearTarjeta(Libro libro) {
        ImageView portada = new ImageView(ImagenUtils.cargarPortadaDesdeRuta(libro.getPortada()));
        portada.setFitWidth(150);
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
        btnEditar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnEditar, Priority.ALWAYS);
        btnEditar.setOnAction(e -> editarLibro(libro));

        Button btnComprobante = new Button("Imprimir");
        btnComprobante.getStyleClass().add("boton-tarjeta");
        btnComprobante.setMaxWidth(Double.MAX_VALUE);
        btnComprobante.setTooltip(new Tooltip("Imprimir comprobante de préstamo"));
        btnComprobante.setOnAction(e -> abrirComprobante(libro));

        Button btnEliminarTarjeta = new Button("Eliminar");
        btnEliminarTarjeta.getStyleClass().add("boton-tarjeta-peligro");
        btnEliminarTarjeta.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnEliminarTarjeta, Priority.ALWAYS);
        btnEliminarTarjeta.setOnAction(e -> eliminarLibro(libro));

        HBox filaSecundaria = new HBox(6, btnEditar, btnEliminarTarjeta);
        filaSecundaria.setAlignment(Pos.CENTER);
        filaSecundaria.setMaxWidth(Double.MAX_VALUE);

        VBox acciones = new VBox(6, btnComprobante, filaSecundaria);
        acciones.setAlignment(Pos.CENTER);

        VBox tarjeta = new VBox(8, portada, titulo, autor, copias, acciones);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.setPrefWidth(190);
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

            Stage stageComprobante = new Stage(StageStyle.TRANSPARENT);
            stageComprobante.initOwner(SceneManager.getInstanciaSceneManager().getStagePrincipal());
            stageComprobante.initModality(Modality.WINDOW_MODAL);
            stageComprobante.setTitle("Exodus Codex - Comprobante de préstamo");
            stageComprobante.setResizable(false);
            stageComprobante.setScene(viewFactory.crearEscenaModal(raizComprobante, stageComprobante));

            controlador.setStage(stageComprobante);

            stageComprobante.showAndWait();
        } catch (IOException e) {
            mostrarError(new RuntimeException("No se pudo abrir el comprobante de préstamo.", e));
        }
    }

    private void editarLibro(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/josemejia/system/view/EditarLibroView.fxml"));
            Parent raizEditor = loader.load();

            EditarLibroController controlador = loader.getController();
            controlador.setLibro(libro);
            controlador.setAlGuardarOEliminar(() -> cargarCatalogo(libroService.listar()));

            Stage stageEditor = new Stage(StageStyle.TRANSPARENT);
            stageEditor.initOwner(SceneManager.getInstanciaSceneManager().getStagePrincipal());
            stageEditor.initModality(Modality.WINDOW_MODAL);
            stageEditor.setTitle("Exodus Codex - Editar libro");
            stageEditor.setResizable(false);
            stageEditor.setScene(viewFactory.crearEscenaModal(raizEditor, stageEditor));

            controlador.setStage(stageEditor);

            stageEditor.showAndWait();
        } catch (IOException e) {
            mostrarError(new RuntimeException("No se pudo abrir el editor de libro.", e));
        }
    }

            private void eliminarLibro(Libro libro) {
        boolean confirmado = AlertUtils.mostrarConfirmacion(
                "Eliminar libro",
                "¿Seguro que quieres eliminar \"" + libro.getTitulo() + "\" del acervo? Esta acción no se puede deshacer.",
                "Eliminar");
        if (!confirmado) {
            return;
        }
        try {
            libroService.eliminar(libro, SesionManager.getInstanciaSessionManager().getUsuarioActual());
            AlertUtils.mostrarAlertaPersonalizada("Catálogo", "El libro se eliminó del acervo.", TipoNotificacion.LIBRO_ELIMINADO);
            cargarCatalogo(libroService.listar());
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void mostrarError(RuntimeException e) {
        String mensaje = e.getMessage();
        TipoNotificacion tipo = mensaje != null && mensaje.contains("iniciar sesión")
                ? TipoNotificacion.ACCESO_DENEGADO
                : TipoNotificacion.ERROR;
        AlertUtils.mostrarAlertaPersonalizada("Catálogo", mensaje, tipo);
    }
}
