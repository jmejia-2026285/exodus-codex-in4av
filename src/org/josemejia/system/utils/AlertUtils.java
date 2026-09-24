package org.josemejia.system.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AlertUtils {

    public enum TipoNotificacion {
        INFO("INFORMACIÓN", "rem_normal.png", "alerta-info"),
        EXITO("ÉXITO", "rem_feliz.png", "alerta-exito"),
        ERROR("ERROR", "error-alert.png", "alerta-error"),
        ADVERTENCIA("ADVERTENCIA", "Rem_dudosa_cute.png", "alerta-advertencia"),
        USUARIO_CREADO("ÉXITO", "gif/userdone.gif", "alerta-exito"),
        LIBRO_GUARDADO("ÉXITO", "gif/save.gif", "alerta-exito"),
        LIBRO_ELIMINADO("ÉXITO", "rem_asustada.png", "alerta-exito"),
        USUARIO_ACTUALIZADO("ÉXITO", "gif/Users.gif", "alerta-exito"),
        SIN_RESULTADOS("SIN RESULTADOS", "rem_duda.png", "alerta-info"),
        ACCESO_DENEGADO("ACCESO DENEGADO", "gif/acceso-denegado.gif", "alerta-error"),
        BIENVENIDA("BIENVENIDO", "gif/bibliotecario.gif", "alerta-exito");

        private final String etiqueta;
        private final String imagen;
        private final String claseEstilo;

        TipoNotificacion(String etiqueta, String imagen, String claseEstilo) {
            this.etiqueta = etiqueta;
            this.imagen = imagen;
            this.claseEstilo = claseEstilo;
        }

        public String getEtiqueta() {
            return etiqueta;
        }

        public String getImagen() {
            return imagen;
        }

        public String getClaseEstilo() {
            return claseEstilo;
        }
    }

    private static final String RUTA_CSS = "/org/josemejia/system/resources/styles/AlertStyles.css";
    private static final double TAMANIO_ICONO = 72;
    private static final double ANCHO_ALERTA = 470;
    private static final double RADIO_MARCO = 18;

    private AlertUtils() {
    }

    public static void mostrarAlertaPersonalizada(String titulo, String mensaje) {
        mostrarAlertaPersonalizada(titulo, mensaje, TipoNotificacion.INFO);
    }

    public static void mostrarAlertaPersonalizada(String titulo, String mensaje, TipoNotificacion tipo) {
        String textoTitulo = titulo == null ? "" : titulo;
        String textoMensaje = mensaje == null ? "" : mensaje;

        Stage escenario = new Stage(StageStyle.TRANSPARENT);
        escenario.initModality(Modality.APPLICATION_MODAL);
        escenario.setTitle(textoTitulo);
        escenario.setResizable(false);

        Stage duenio = SceneManager.getInstanciaSceneManager().getStagePrincipal();
        boolean centrarEnDuenio = duenio != null && duenio.isShowing();
        if (centrarEnDuenio) {
            escenario.initOwner(duenio);
        }

        // ---- Contenido de la tarjeta ----
        Label lblEtiqueta = new Label(tipo.getEtiqueta());
        lblEtiqueta.getStyleClass().add("alerta-etiqueta");

        Label lblTitulo = new Label(textoTitulo);
        lblTitulo.getStyleClass().add("alerta-titulo");
        lblTitulo.setWrapText(true);

        Region regla = new Region();
        regla.getStyleClass().add("alerta-regla");

        Label lblMensaje = new Label(textoMensaje);
        lblMensaje.getStyleClass().add("alerta-mensaje");
        lblMensaje.setWrapText(true);
        lblMensaje.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblMensaje, Priority.ALWAYS);

        HBox cuerpo = new HBox(16);
        cuerpo.setAlignment(Pos.CENTER_LEFT);

        Image imagenRem = ImagenUtils.cargarImagenLocal(tipo.getImagen());
        if (imagenRem != null) {
            ImageView icono = new ImageView(imagenRem);
            icono.setFitWidth(TAMANIO_ICONO);
            icono.setFitHeight(TAMANIO_ICONO);
            icono.setPreserveRatio(true);
            cuerpo.getChildren().add(icono);
        }
        cuerpo.getChildren().add(lblMensaje);

        Button btnAceptar = new Button("Aceptar");
        btnAceptar.getStyleClass().add("alerta-boton");
        btnAceptar.setDefaultButton(true);   // Enter
        btnAceptar.setCancelButton(true);    // Esc
        btnAceptar.setOnAction(e -> escenario.close());

        HBox pie = new HBox(btnAceptar);
        pie.setAlignment(Pos.CENTER_RIGHT);

        VBox tarjeta = new VBox(10, lblEtiqueta, lblTitulo, regla, cuerpo, pie);
        tarjeta.getStyleClass().add("alerta-tarjeta");
        VBox.setMargin(cuerpo, new Insets(8, 0, 6, 0));

        // ---- Marco con fondo.jpg (esquinas redondeadas por clip) ----
        StackPane marco = new StackPane(tarjeta);
        marco.getStyleClass().add("alerta-marco");
        Rectangle recorte = new Rectangle();
        recorte.setArcWidth(RADIO_MARCO * 2);
        recorte.setArcHeight(RADIO_MARCO * 2);
        recorte.widthProperty().bind(marco.widthProperty());
        recorte.heightProperty().bind(marco.heightProperty());
        marco.setClip(recorte);

        // La sombra va en un nodo aparte porque el clip la recortaría.
        Region sombra = new Region();
        sombra.getStyleClass().add("alerta-sombra");

        StackPane raiz = new StackPane(sombra, marco);
        raiz.getStyleClass().addAll("alerta-raiz", tipo.getClaseEstilo());
        raiz.setPrefWidth(ANCHO_ALERTA);

        // Ventana sin decoración: se arrastra desde cualquier punto.
        final double[] desplazamiento = new double[2];
        raiz.setOnMousePressed(e -> {
            desplazamiento[0] = e.getSceneX();
            desplazamiento[1] = e.getSceneY();
        });
        raiz.setOnMouseDragged(e -> {
            escenario.setX(e.getScreenX() - desplazamiento[0]);
            escenario.setY(e.getScreenY() - desplazamiento[1]);
        });

        Scene escena = new Scene(raiz);
        escena.setFill(Color.TRANSPARENT);

        var urlCss = AlertUtils.class.getResource(RUTA_CSS);
        if (urlCss != null) {
            escena.getStylesheets().add(urlCss.toExternalForm());
        }

        escenario.setScene(escena);

        if (centrarEnDuenio) {
            escenario.setOnShown(e -> {
                escenario.setX(duenio.getX() + (duenio.getWidth() - escenario.getWidth()) / 2);
                escenario.setY(duenio.getY() + (duenio.getHeight() - escenario.getHeight()) / 2);
            });
        }

        AnimationUtils.aplicarFadeIn(raiz);
        escenario.showAndWait();
    }
}
