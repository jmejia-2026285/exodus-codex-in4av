package org.josemejia.system;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.josemejia.system.utils.SceneManager;
import org.josemejia.system.utils.ViewFactory;

public class MainClass extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stageRoot) {
        SceneManager.getInstanciaSceneManager().setStagePrincipal(stageRoot);
        ViewFactory viewFactory = new ViewFactory();
        viewFactory.viewLogin();

        // Si en algún momento necesitas cargar la vista directamente por FXML
        // (comportamiento de la versión anterior de MainClass), puedes usar:
        // cargarVistaPorFXML(stageRoot);
    }

    /**
     * Lógica original de la segunda versión de MainClass, conservada como
     * método auxiliar para cargar una vista directamente desde un FXML,
     * sin depender de SceneManager/ViewFactory.
     */
    private void cargarVistaPorFXML(Stage stage) {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource(
                    "/org/josemejia/system/view/RegistroBibliotecarioView_Redisenо.fxml"
                )
            );

            Scene scene = new Scene(root);

            stage.setTitle("Sistema Bibliotecario");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}