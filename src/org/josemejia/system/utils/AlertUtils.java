/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.josemejia.system.utils;

/**
 *
 * @author informatica
 */
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class AlertUtils {

    public enum TipoNotificacion {
        INFO(Alert.AlertType.INFORMATION, "rem_normal.png", "alerta-info"),
        EXITO(Alert.AlertType.INFORMATION, "rem_feliz.png", "alerta-exito"),
        ERROR(Alert.AlertType.ERROR, "error-alert.png", "alerta-error"),
        ADVERTENCIA(Alert.AlertType.WARNING, "Rem_dudosa_cute.png", "alerta-advertencia");

        private final Alert.AlertType tipo;
        private final String imagen;
        private final String claseEstilo;

        TipoNotificacion(Alert.AlertType tipo, String imagen, String claseEstilo) {
            this.tipo = tipo;
            this.imagen = imagen;
            this.claseEstilo = claseEstilo;
        }

        public Alert.AlertType getTipoAlerta() {
            return tipo;
        }

        public String getImagen() {
            return imagen;
        }

        public String getClaseEstilo() {
            return claseEstilo;
        }
    }

    private static final String RUTA_CSS = "/org/josemejia/system/resources/styles/AlertStyles.css";
    private static final double TAMANIO_ICONO = 48;

    // Constructor privado para evitar instanciación
    private AlertUtils() {
    }

    public static void mostrarAlertaPersonalizada(String titulo, String mensaje) {
        mostrarAlertaPersonalizada(titulo, mensaje, TipoNotificacion.INFO);
    }

    public static void mostrarAlertaPersonalizada(String titulo, String mensaje, TipoNotificacion tipo) {
        Alert alerta = new Alert(tipo.getTipoAlerta());
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);

        Label lbl = new Label(mensaje);
        lbl.setWrapText(true);
        lbl.setMaxWidth(280);

        // Burbuja de mensaje: el icono de Rem correspondiente al tipo, junto al texto
        HBox contenedor = new HBox(14);
        contenedor.setAlignment(Pos.CENTER_LEFT);
        contenedor.setPadding(new Insets(4, 8, 4, 8));

        Image imagenRem = ImagenUtils.cargarImagenLocal(tipo.getImagen());
        if (imagenRem != null) {
            ImageView icono = new ImageView(imagenRem);
            icono.setFitWidth(TAMANIO_ICONO);
            icono.setFitHeight(TAMANIO_ICONO);
            icono.setPreserveRatio(true);
            contenedor.getChildren().add(icono);
            // También se usa como graphic para que el header-panel coloreado se muestre
            ImageView iconoHeader = new ImageView(imagenRem);
            iconoHeader.setFitWidth(32);
            iconoHeader.setFitHeight(32);
            iconoHeader.setPreserveRatio(true);
            alerta.setGraphic(iconoHeader);
        }

        contenedor.getChildren().add(lbl);
        alerta.getDialogPane().setContent(contenedor);

        // Carga segura del CSS
        var urlCss = AlertUtils.class.getResource(RUTA_CSS);
        if (urlCss != null) {
            alerta.getDialogPane().getStylesheets().add(urlCss.toExternalForm());
            alerta.getDialogPane().getStyleClass().add(tipo.getClaseEstilo());
        }

        alerta.showAndWait();
    }
}
