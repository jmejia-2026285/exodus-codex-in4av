package org.josemejia.system;

/**
 *
 * @author informatica
 */ 
import javafx.application.Application;
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
    }
}
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClass extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(
            getClass().getResource(
                "/org/josemejia/system/view/RegistroBibliotecarioView_Redisenо.fxml"
            )
        );

        Scene scene = new Scene(root);

        stage.setTitle("Sistema Bibliotecario");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
