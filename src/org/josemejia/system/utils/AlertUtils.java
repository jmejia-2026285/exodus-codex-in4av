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
        ADVERTENCIA(Alert.AlertType.WARNING, "Rem_dudosa_cute.png", "alerta-advertencia"),
        USUARIO_CREADO(Alert.AlertType.INFORMATION, "userdone.gif", "alerta-exito");

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
    private static final double TAMANIO_ICONO = 70;

    private AlertUtils() {
    }

    public static void mostrarAlertaPersonalizada(String titulo, String mensaje) {
        mostrarAlertaPersonalizada(titulo, mensaje, TipoNotificacion.INFO);
    }

    public static void mostrarAlertaPersonalizada(String titulo, String mensaje, TipoNotificacion tipo) {
        Alert alerta = new Alert(tipo.getTipoAlerta());
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.getDialogPane().setGraphic(null);

        Label lbl = new Label(mensaje);
        lbl.setWrapText(true);
        lbl.setMaxWidth(280);

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
        }

        contenedor.getChildren().add(lbl);
        alerta.getDialogPane().setContent(contenedor);

        var urlCss = AlertUtils.class.getResource(RUTA_CSS);
        if (urlCss != null) {
            alerta.getDialogPane().getStylesheets().add(urlCss.toExternalForm());
            alerta.getDialogPane().getStyleClass().add(tipo.getClaseEstilo());
        }

        alerta.showAndWait();
    }
}