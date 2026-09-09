package org.josemejia.sgb;

import javafx.application.Application;
import javafx.stage.Stage;
import org.josemejia.sgb.utils.SceneManager;
import org.josemejia.sgb.utils.ViewFactory;

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
