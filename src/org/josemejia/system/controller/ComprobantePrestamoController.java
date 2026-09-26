package org.josemejia.system.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.josemejia.system.model.Libro;
import org.josemejia.system.utils.AlertUtils;
import org.josemejia.system.utils.AlertUtils.TipoNotificacion;
import org.josemejia.system.utils.AnimationUtils;
import org.josemejia.system.utils.ValidationsUtils;

public class ComprobantePrestamoController {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final double ANCHO_MEDIA_CARTA_IN = 5.5;
    private static final double ALTO_MEDIA_CARTA_IN = 8.5;

    @FXML
    private VBox panelFormulario;
    @FXML
    private VBox panelComprobante;

    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblAutor;
    @FXML
    private Label lblIsbn;

    @FXML
    private TextField txtNombreEstudiante;
    @FXML
    private TextField txtMatricula;
    @FXML
    private DatePicker dpFechaLimite;
    @FXML
    private Label lblMensajeError;

    @FXML
    private Label lblTituloImpreso;
    @FXML
    private Label lblAutorImpreso;
    @FXML
    private Label lblIsbnImpreso;
    @FXML
    private Label lblEstudianteImpreso;
    @FXML
    private Label lblMatriculaImpreso;
    @FXML
    private Label lblFechaPrestamoImpreso;
    @FXML
    private Label lblFechaLimiteImpreso;

    @FXML
    private Button btnGenerar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnImprimir;
    @FXML
    private Button btnVolverEditar;

    private Libro libro;
    private Stage stage;

    @FXML
    private void initialize() {
        AnimationUtils.aplicarEfectoHover(btnGenerar);
        AnimationUtils.aplicarEfectoHover(btnCancelar);
        AnimationUtils.aplicarEfectoHover(btnImprimir);
        AnimationUtils.aplicarEfectoHover(btnVolverEditar);
        AnimationUtils.aplicarFocoAnimado(txtNombreEstudiante);
        AnimationUtils.aplicarFocoAnimado(txtMatricula);

        dpFechaLimite.setValue(LocalDate.now().plusDays(7));
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
        lblTitulo.setText(libro.getTitulo());
        lblAutor.setText(libro.getAutorPrincipal());
        lblIsbn.setText("ISBN: " + libro.getIsbn());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleGenerarComprobante() {
        String error = validarFormulario();
        if (error != null) {
            lblMensajeError.setText(error);
            lblMensajeError.setVisible(true);
            lblMensajeError.setManaged(true);
            return;
        }
        lblMensajeError.setVisible(false);
        lblMensajeError.setManaged(false);

        lblTituloImpreso.setText(libro.getTitulo());
        lblAutorImpreso.setText("Autor: " + libro.getAutorPrincipal());
        lblIsbnImpreso.setText("ISBN: " + libro.getIsbn());
        lblEstudianteImpreso.setText("Estudiante: " + txtNombreEstudiante.getText().trim());
        lblMatriculaImpreso.setText("Matrícula: " + txtMatricula.getText().trim());
        lblFechaPrestamoImpreso.setText("Fecha de préstamo: " + LocalDate.now().format(FORMATO_FECHA));
        lblFechaLimiteImpreso.setText(dpFechaLimite.getValue().format(FORMATO_FECHA));

        panelFormulario.setVisible(false);
        panelFormulario.setManaged(false);
        panelComprobante.setVisible(true);
        panelComprobante.setManaged(true);
    }

    @FXML
    private void handleVolverEditar() {
        panelComprobante.setVisible(false);
        panelComprobante.setManaged(false);
        panelFormulario.setVisible(true);
        panelFormulario.setManaged(true);
    }

    @FXML
    private void handleImprimir() {
        Printer impresora = Printer.getDefaultPrinter();
        if (impresora == null) {
            AlertUtils.mostrarAlertaPersonalizada("Impresión", "No se detectó ninguna impresora disponible.", TipoNotificacion.IMPRESION_ERROR);
            return;
        }

        Paper mediaCarta = obtenerPapelMediaCarta(impresora);
        PageLayout layout = impresora.createPageLayout(mediaCarta, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);

        PrinterJob job = PrinterJob.createPrinterJob(impresora);
        if (job == null) {
            AlertUtils.mostrarAlertaPersonalizada("Impresión", "No se pudo iniciar el trabajo de impresión.", TipoNotificacion.IMPRESION_ERROR);
            return;
        }
        job.getJobSettings().setPageLayout(layout);

        boolean confirmado = job.showPrintDialog(stage);
        if (!confirmado) {
            return;
        }

        ImageView hojaParaImprimir = generarImagenComprobante(layout);

        boolean exito = job.printPage(layout, hojaParaImprimir);
        if (exito) {
            job.endJob();
            AlertUtils.mostrarAlertaPersonalizada("Impresión", "El comprobante se envió a la impresora.", TipoNotificacion.IMPRESION_EXITOSA);
            stage.close();
        } else {
            AlertUtils.mostrarAlertaPersonalizada("Impresión", "No se pudo imprimir el comprobante.", TipoNotificacion.IMPRESION_ERROR);
        }
    }

    private Paper obtenerPapelMediaCarta(Printer impresora) {
        double anchoObjetivo = ANCHO_MEDIA_CARTA_IN * 72.0;
        double altoObjetivo = ALTO_MEDIA_CARTA_IN * 72.0;

        Paper mejor = impresora.getDefaultPageLayout().getPaper();
        double mejorDiferencia = Double.MAX_VALUE;

        for (Paper candidato : impresora.getPrinterAttributes().getSupportedPapers()) {
            double diferencia = Math.abs(candidato.getWidth() - anchoObjetivo)
                    + Math.abs(candidato.getHeight() - altoObjetivo);
            if (diferencia < mejorDiferencia) {
                mejorDiferencia = diferencia;
                mejor = candidato;
            }
        }
        return mejor;
    }

    /**
     * Genera una imagen (snapshot) del comprobante y la envuelve en un ImageView
     * ya ajustado al área imprimible de la página.
     */
    private ImageView generarImagenComprobante(PageLayout layout) {
        HBox contenedorBotones = (HBox) btnImprimir.getParent();
        boolean botonesVisiblesAntes = contenedorBotones.isVisible();
        contenedorBotones.setVisible(false);
        contenedorBotones.setManaged(false);

        panelComprobante.getTransforms().clear();
        panelComprobante.applyCss();
        panelComprobante.layout();

        SnapshotParameters parametrosSnapshot = new SnapshotParameters();
        parametrosSnapshot.setFill(Color.WHITE);
        WritableImage snapshot = panelComprobante.snapshot(parametrosSnapshot, null);

        contenedorBotones.setVisible(botonesVisiblesAntes);
        contenedorBotones.setManaged(botonesVisiblesAntes);

        ImageView vistaComprobante = new ImageView(snapshot);
        vistaComprobante.setPreserveRatio(true);

        double anchoImprimible = layout.getPrintableWidth();
        double altoImprimible = layout.getPrintableHeight();
        double escala = Math.min(anchoImprimible / snapshot.getWidth(), altoImprimible / snapshot.getHeight());
        if (escala < 1.0) {
            vistaComprobante.setFitWidth(snapshot.getWidth() * escala);
            vistaComprobante.setFitHeight(snapshot.getHeight() * escala);
        }
        return vistaComprobante;
    }

    @FXML
    private void handleCancelar() {
        stage.close();
    }

    private String validarFormulario() {
        if (ValidationsUtils.esCampoVacio(txtNombreEstudiante.getText())) {
            return "El nombre del estudiante es obligatorio.";
        }
        if (ValidationsUtils.esCampoVacio(txtMatricula.getText())) {
            return "La matrícula es obligatoria.";
        }
        if (!ValidationsUtils.esFechaLimiteValida(dpFechaLimite.getValue())) {
            return "La fecha límite debe ser posterior a hoy.";
        }
        return null;
    }
}
