/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.josemejia.system.utils;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationUtils {

    // Constructor privado para evitar instanciación
    private AnimationUtils() {
    }

    public static void aplicarFadeIn(Node nodo) {
        nodo.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(400), nodo);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    public static void aplicarEfectoHover(Node nodo) {
        ScaleTransition st = new ScaleTransition(Duration.millis(120), nodo);
        nodo.setOnMouseEntered(e -> {
            st.setToX(1.04);
            st.setToY(1.04);
            st.playFromStart();
        });
        nodo.setOnMouseExited(e -> {
            st.setToX(1.0);
            st.setToY(1.0);
            st.playFromStart();
        });
    }
}
