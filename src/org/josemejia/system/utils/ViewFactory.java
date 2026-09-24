package org.josemejia.system.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Locale;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
    private static final String RUTA_ESTILOS = "/org/josemejia/system/resources/styles/styles.css";
    private static final double RADIO_VENTANA = 18;

    private static final int BORDE_IZQ = 1, BORDE_DER = 2, BORDE_ARR = 4, BORDE_ABA = 8;
    private static final double MARGEN_REDIMENSION = 6;
    private static final double MIN_ANCHO = 640, MIN_ALTO = 420;

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
            FXMLLoader loader = new FXMLLoader(urlFile);
            return crearEscenaConMarco(loader.load());
        } catch (IOException e) {
            throw new UncheckedIOException("Error al cargar el FXML: " + pathOfFile, e);
        }
    }

    private Scene crearEscenaConMarco(Parent contenido) {
        SceneManager sceneManager = SceneManager.getInstanciaSceneManager();
        Stage stage = sceneManager.getStagePrincipal();

        Region borde = new Region();
        borde.getStyleClass().add("ventana-borde");
        borde.setMouseTransparent(true);
        borde.visibleProperty().bind(sceneManager.maximizadaProperty().not());

        Button btnMinimizar = new Button("\u2014");
        btnMinimizar.getStyleClass().add("ventana-control");
        btnMinimizar.setFocusTraversable(false);
        btnMinimizar.setOnAction(e -> stage.setIconified(true));

        Button btnMaximizar = new Button();
        btnMaximizar.getStyleClass().add("ventana-control");
        btnMaximizar.setFocusTraversable(false);
        btnMaximizar.textProperty().bind(
                sceneManager.maximizadaProperty().map(m -> m ? "\u2750" : "\u25A1"));
        btnMaximizar.setOnAction(e -> sceneManager.alternarMaximizado());

        Button btnCerrar = new Button("\u2715");
        btnCerrar.getStyleClass().addAll("ventana-control", "ventana-control-cerrar");
        btnCerrar.setFocusTraversable(false);
        btnCerrar.setOnAction(e -> Platform.exit());

        HBox controles = new HBox(6, btnMinimizar, btnMaximizar, btnCerrar);
        controles.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(controles, Pos.TOP_RIGHT);
        StackPane.setMargin(controles, new Insets(10, 14, 0, 0));

        StackPane marco = new StackPane(contenido, borde, controles);

        Rectangle recorte = new Rectangle();
        recorte.arcWidthProperty().bind(
                sceneManager.maximizadaProperty().map(m -> m ? 0.0 : RADIO_VENTANA * 2));
        recorte.arcHeightProperty().bind(
                sceneManager.maximizadaProperty().map(m -> m ? 0.0 : RADIO_VENTANA * 2));
        recorte.widthProperty().bind(marco.widthProperty());
        recorte.heightProperty().bind(marco.heightProperty());
        marco.setClip(recorte);

        // Arrastre y redimensionado
        final double[] delta = new double[2];
        final boolean[] arrastrando = {false};
        final int[] bordeActivo = {0};
        final double[] inicio = new double[6]; // x, y, ancho, alto, screenX, screenY

        marco.addEventFilter(MouseEvent.MOUSE_MOVED,
                e -> marco.setCursor(cursorParaBorde(detectarBorde(e, marco))));

        marco.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            bordeActivo[0] = e.isPrimaryButtonDown() ? detectarBorde(e, marco) : 0;
            arrastrando[0] = bordeActivo[0] == 0 && e.isPrimaryButtonDown()
                    && !sceneManager.isMaximizada() && esZonaArrastrable(e.getTarget());
            delta[0] = e.getScreenX() - stage.getX();
            delta[1] = e.getScreenY() - stage.getY();
            inicio[0] = stage.getX();
            inicio[1] = stage.getY();
            inicio[2] = stage.getWidth();
            inicio[3] = stage.getHeight();
            inicio[4] = e.getScreenX();
            inicio[5] = e.getScreenY();
            if (bordeActivo[0] != 0) {
                e.consume();
            }
        });
        marco.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            if (bordeActivo[0] != 0) {
                redimensionar(stage, marco, bordeActivo[0], inicio, e);
                e.consume();
            } else if (arrastrando[0]) {
                stage.setX(e.getScreenX() - delta[0]);
                stage.setY(e.getScreenY() - delta[1]);
            }
        });
        marco.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            if (bordeActivo[0] != 0) {
                bordeActivo[0] = 0;
                e.consume();
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

    private int detectarBorde(MouseEvent e, Region marco) {
        if (SceneManager.getInstanciaSceneManager().isMaximizada()) {
            return 0;
        }
        int borde = 0;
        if (e.getSceneX() < MARGEN_REDIMENSION) {
            borde |= BORDE_IZQ;
        } else if (e.getSceneX() > marco.getWidth() - MARGEN_REDIMENSION) {
            borde |= BORDE_DER;
        }
        if (e.getSceneY() < MARGEN_REDIMENSION) {
            borde |= BORDE_ARR;
        } else if (e.getSceneY() > marco.getHeight() - MARGEN_REDIMENSION) {
            borde |= BORDE_ABA;
        }
        return borde;
    }

    private Cursor cursorParaBorde(int borde) {
        switch (borde) {
            case BORDE_IZQ:
            case BORDE_DER:
                return Cursor.H_RESIZE;
            case BORDE_ARR:
            case BORDE_ABA:
                return Cursor.V_RESIZE;
            case BORDE_IZQ | BORDE_ARR:
                return Cursor.NW_RESIZE;
            case BORDE_DER | BORDE_ABA:
                return Cursor.SE_RESIZE;
            case BORDE_DER | BORDE_ARR:
                return Cursor.NE_RESIZE;
            case BORDE_IZQ | BORDE_ABA:
                return Cursor.SW_RESIZE;
            default:
                return null;
        }
    }

    private void redimensionar(Stage stage, Region marco, int borde, double[] ini, MouseEvent e) {
        double dx = e.getScreenX() - ini[4];
        double dy = e.getScreenY() - ini[5];
        double minAncho = Math.max(MIN_ANCHO, marco.minWidth(-1));
        double minAlto = Math.max(MIN_ALTO, marco.minHeight(-1));

        double x = ini[0], y = ini[1], ancho = ini[2], alto = ini[3];

        if ((borde & BORDE_DER) != 0) {
            ancho = Math.max(minAncho, ini[2] + dx);
        }
        if ((borde & BORDE_IZQ) != 0) {
            ancho = Math.max(minAncho, ini[2] - dx);
            x = ini[0] + ini[2] - ancho;
        }
        if ((borde & BORDE_ABA) != 0) {
            alto = Math.max(minAlto, ini[3] + dy);
        }
        if ((borde & BORDE_ARR) != 0) {
            alto = Math.max(minAlto, ini[3] - dy);
            y = ini[1] + ini[3] - alto;
        }
        stage.setX(x);
        stage.setY(y);
        stage.setWidth(ancho);
        stage.setHeight(alto);
    }

    public void loadScene(String viewName) {
        try {
            ViewConfig config = ViewConfig.fromString(viewName);
            Stage stage = SceneManager.getInstanciaSceneManager().getStagePrincipal();

            stage.setTitle(config.title);
            stage.setResizable(false);

            Scene scene = loadFileFXML(config.fxmlFile);
            SceneManager.getInstanciaSceneManager().changeScene(scene);

        } catch (RuntimeException e) {
            System.err.println("Error al cargar la vista '" + viewName + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

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
