package org.josemejia.system;

import javafx.application.Application; // Importaciones para hacer funcionar java fx
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.josemejia.system.utils.SceneManager;
import org.josemejia.system.utils.ViewFactory;

public class MainClass extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stageRoot) {

        stageRoot.initStyle(StageStyle.TRANSPARENT); // Aqui se elimina el marco de programa normal de windows //

        SceneManager.getInstanciaSceneManager().setStagePrincipal(stageRoot);  // este bloque de codigo se ecarga de invocar al viewFactory
        ViewFactory viewFactory = new ViewFactory();                                            // y especificamente el apartado para invogar al login
        viewFactory.viewLogin();
    }

}
