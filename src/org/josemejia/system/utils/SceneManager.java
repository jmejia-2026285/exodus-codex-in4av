package org.josemejia.system.utils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author informatica
 */
public class SceneManager {

    private static SceneManager instanciaSceneManager;
    private Stage stageMain;

    private final BooleanProperty maximizada = new SimpleBooleanProperty(false);
    private double[] boundsRestaurar;

    private SceneManager() {

    }

    public static SceneManager getInstanciaSceneManager() {
        if (instanciaSceneManager == null) {
            instanciaSceneManager = new SceneManager();
        }
        return instanciaSceneManager;
    }

    public void changeScene(Scene scene) {
        try {
            stageMain.setScene(scene);
            if (!maximizada.get()) {
                stageMain.sizeToScene();
                stageMain.centerOnScreen();
            }
            stageMain.show();
        } catch (NullPointerException objetoNulo) {
            //alert
        }
    }

    public Stage getStagePrincipal() {
        return stageMain;
    }

    public void setStagePrincipal(Stage stagePrincipal) {
        this.stageMain = stagePrincipal;
    }

    public BooleanProperty maximizadaProperty() {
        return maximizada;
    }

    public boolean isMaximizada() {
        return maximizada.get();
    }

    public void alternarMaximizado() {
        if (stageMain == null) {
            return;
        }
        if (maximizada.get()) {
            if (boundsRestaurar != null) {
                stageMain.setX(boundsRestaurar[0]);
                stageMain.setY(boundsRestaurar[1]);
                stageMain.setWidth(boundsRestaurar[2]);
                stageMain.setHeight(boundsRestaurar[3]);
            }
            maximizada.set(false);
        } else {
            boundsRestaurar = new double[]{stageMain.getX(), stageMain.getY(),
                stageMain.getWidth(), stageMain.getHeight()};
            Screen pantalla = Screen.getScreensForRectangle(stageMain.getX(), stageMain.getY(),
                    stageMain.getWidth(), stageMain.getHeight())
                    .stream().findFirst().orElse(Screen.getPrimary());
            Rectangle2D area = pantalla.getVisualBounds();
            stageMain.setX(area.getMinX());
            stageMain.setY(area.getMinY());
            stageMain.setWidth(area.getWidth());
            stageMain.setHeight(area.getHeight());
            maximizada.set(true);
        }
    }
}