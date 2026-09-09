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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.josemejia.system.MainClass;

public class ViewFactory {

    private static final String PATH_VIEWS = "/org/josemejia/system/view/";

    // Enum para centralizar la configuración de cada vista (Principio Abierto/Cerrado)
    private enum ViewConfig {
        LOGIN("LoginView.fxml", "Exodus Codex - Iniciar sesión", false, 800, 600),
        REGISTRO("RegistroBibliotecarioView.fxml", "Exodus Codex - Registrar Bibliotecario", false, 800, 620),
        DASHBOARD("DashboardView.fxml", "Exodus Codex - Menú principal", true, 860, 560),
        CATALOGO("LibroView.fxml", "Exodus Codex - Catálogo bibliográfico", true, 960, 620);

        final String fxmlFile;
        final String title;
        final boolean resizable;
        final int width;
        final int height;

        ViewConfig(String fxmlFile, String title, boolean resizable, int width, int height) {
            this.fxmlFile = fxmlFile;
            this.title = title;
            this.resizable = resizable;
            this.width = width;
            this.height = height;
        }

        static ViewConfig fromString(String name) {
            try {
                return ViewConfig.valueOf(name.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Vista no reconocida: " + name);
            }
        }
    }

    public Scene loadFileFXML(String nameFile, int width, int height) {
        String pathOfFile = PATH_VIEWS + nameFile;
        URL urlFile = MainClass.class.getResource(pathOfFile);

        if (urlFile == null) {
            throw new IllegalArgumentException("No se encontró el archivo FXML en la ruta: " + pathOfFile);
        }

        try {
            // FXMLLoader ya usa JavaFXBuilderFactory por defecto, no es necesario configurarlo.
            // Además, se puede pasar la URL directamente al constructor para ahorrar líneas.
            FXMLLoader loader = new FXMLLoader(urlFile);
            return new Scene(loader.load(), width, height);
        } catch (IOException e) {
            throw new UncheckedIOException("Error al cargar el FXML: " + pathOfFile, e);
        }
    }

    public void loadScene(String viewName) {
        try {
            ViewConfig config = ViewConfig.fromString(viewName);
            Stage stage = SceneManager.getInstanciaSceneManager().getStagePrincipal();

            // Configurar el escenario una sola vez
            stage.setTitle(config.title);
            stage.setResizable(config.resizable);

            // Cargar y cambiar la escena
            Scene scene = loadFileFXML(config.fxmlFile, config.width, config.height);
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
}
