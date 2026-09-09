/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.josemejia.system.utils;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author informatica
 */
public class SceneManager {

    private static SceneManager instanciaSceneManager;
    private Stage stageMain;

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
            stageMain.sizeToScene();
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
}
