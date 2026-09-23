/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.josemejia.system.utils;

/**
 *
 * @author informatica
 */
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Locale;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.josemejia.system.MainClass;

public class ViewFactory {

    private static final String PATH_VIEWS = "/org/josemejia/system/view/";

    // Enum para centralizar la configuración de cada vista (Principio Abierto/Cerrado)
    private enum ViewConfig {
        LOGIN("LoginView.fxml", "Exodus Codex - Iniciar sesión", true),
        REGISTRO("RegBibliotecarioView.fxml", "Exodus Codex - Registrar Bibliotecario", true),
        DASHBOARD("DashboardView.fxml", "Exodus Codex - Menú principal", true),
        CATALOGO("LibroView.fxml", "Exodus Codex - Catálogo bibliográfico", true),
PERSONAL("PersonalView.fxml", "Exodus Codex - Gestión de personal", true);
        final String fxmlFile;
        final String title;
        final boolean resizable;

        ViewConfig(String fxmlFile, String title, boolean resizable) {
            this.fxmlFile = fxmlFile;
            this.title = title;
            this.resizable = resizable;
        }

        static ViewConfig fromString(String name) {
            try {
                return ViewConfig.valueOf(name.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Vista no reconocida: " + name);
            }
        }
    }

    public Scene loadFileFXML(String nameFile) {
        String pathOfFile = PATH_VIEWS + nameFile;
        URL urlFile = MainClass.class.getResource(pathOfFile);

        if (urlFile == null) {
            throw new IllegalArgumentException("No se encontró el archivo FXML en la ruta: " + pathOfFile);
        }

        try {
            // FXMLLoader ya usa JavaFXBuilderFactory por defecto, no es necesario configurarlo.
            // Además, se puede pasar la URL directamente al constructor para ahorrar líneas.
            FXMLLoader loader = new FXMLLoader(urlFile);
            return crearEscenaConMarco(loader.load());
        } catch (IOException e) {
            throw new UncheckedIOException("Error al cargar el FXML: " + pathOfFile, e);
        }
    }
        private static final String RUTA_ESTILOS = "/org/josemejia/system/resources/styles/styles.css";
    private static final double RADIO_VENTANA = 18;

    private Scene crearEscenaConMarco(Parent contenido) {
        Stage stage = SceneManager.getInstanciaSceneManager().getStagePrincipal();

        Region borde = new Region();
        borde.getStyleClass().add("ventana-borde");
        borde.setMouseTransparent(true);

        Button btnMinimizar = new Button("\u2014");
        btnMinimizar.getStyleClass().add("ventana-control");
        btnMinimizar.setFocusTraversable(false);
        btnMinimizar.setOnAction(e -> stage.setIconified(true));

        Button btnCerrar = new Button("\u2715");
        btnCerrar.getStyleClass().addAll("ventana-control", "ventana-control-cerrar");
        btnCerrar.setFocusTraversable(false);
        btnCerrar.setOnAction(e -> Platform.exit());

        HBox controles = new HBox(6, btnMinimizar, btnCerrar);
        controles.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(controles, Pos.TOP_RIGHT);
        StackPane.setMargin(controles, new Insets(10, 14, 0, 0));

        StackPane marco = new StackPane(contenido, borde, controles);

        Rectangle recorte = new Rectangle();
        recorte.setArcWidth(RADIO_VENTANA * 2);
        recorte.setArcHeight(RADIO_VENTANA * 2);
        recorte.widthProperty().bind(marco.widthProperty());
        recorte.heightProperty().bind(marco.heightProperty());
        marco.setClip(recorte);

        // Arrastre: solo si se presiona fuera de controles interactivos
        final double[] delta = new double[2];
        final boolean[] arrastrando = {false};
        marco.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            arrastrando[0] = e.isPrimaryButtonDown() && esZonaArrastrable(e.getTarget());
            delta[0] = e.getScreenX() - stage.getX();
            delta[1] = e.getScreenY() - stage.getY();
        });
        marco.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            if (arrastrando[0]) {
                stage.setX(e.getScreenX() - delta[0]);
                stage.setY(e.getScreenY() - delta[1]);
            }
        });

        // Obligatorio: las variables de color (-borde, -dorado-codex...) viven en .root
        URL css = ViewFactory.class.getResource(RUTA_ESTILOS);
        if (css != null) {
            marco.getStylesheets().add(css.toExternalForm());
        }

        Scene escena = new Scene(marco);
        escena.setFill(Color.TRANSPARENT);
        return escena;
    }

    private boolean esZonaArrastrable(Object destino) {
        Node nodo = destino instanceof Node ? (Node) destino : null;
        while (nodo != null) {
            if (nodo instanceof ButtonBase || nodo instanceof TextInputControl
                    || nodo instanceof ScrollBar || nodo instanceof ScrollPane) {
                return false;
            }
            nodo = nodo.getParent();
        }
        return true;
    }

    public void loadScene(String viewName) {
        try {
            ViewConfig config = ViewConfig.fromString(viewName);
            Stage stage = SceneManager.getInstanciaSceneManager().getStagePrincipal();

            // Configurar el escenario una sola vez
            stage.setTitle(config.title);
            stage.setResizable(false);

            // Cargar y cambiar la escena
            // loadScene: ahora
            Scene scene = loadFileFXML(config.fxmlFile);
            SceneManager.getInstanciaSceneManager().changeScene(scene);

        } catch (RuntimeException e) {
            System.err.println("Error al cargar la vista '" + viewName + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Se mantienen estos métodos para garantizar compatibilidad y NO modificar otras clases
    public void viewLogin() {
        loadScene("login");
    }

    public void viewRegistro() {
        loadScene("registro");
    }

    public void viewDashboard() {
        loadScene("dashboard");
    }

    public void viewCatalogo() {
        loadScene("catalogo");
    }
    
        public void viewPersonal() {
        loadScene("personal");
    }
}
