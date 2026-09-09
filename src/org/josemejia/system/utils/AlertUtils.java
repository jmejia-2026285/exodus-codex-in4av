/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.josemejia.system.utils;

/**
 *
 * @author informatica
 */
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AlertUtils {

    public enum TipoNotificacion {
        INFO(Alert.AlertType.INFORMATION, "alerta-info"),
        EXITO(Alert.AlertType.INFORMATION, "alerta-exito"),
        ERROR(Alert.AlertType.ERROR, "alerta-error"),
        ADVERTENCIA(Alert.AlertType.WARNING, "alerta-advertencia");

        private final Alert.AlertType tipo;
        private final String estilo;

        TipoNotificacion(Alert.AlertType tipo, String estilo) {
            this.tipo = tipo;
            this.estilo = estilo;
        }

        public Alert.AlertType getTipoAlerta() {
            return tipo;
        }

        public String getClaseEstilo() {
            return estilo;
        }
    }

    private static final String RUTA_CSS = "/org/josemejia/system/resources/styles/AlertStyles.css";

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
        lbl.setMaxWidth(320);

        VBox contenedor = new VBox(lbl);
        contenedor.setAlignment(Pos.CENTER);
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
