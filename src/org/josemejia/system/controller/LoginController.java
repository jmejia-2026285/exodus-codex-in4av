package org.josemejia.system.controller;

import org.josemejia.system.utils.SesionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import org.josemejia.system.model.Usuario;
import org.josemejia.system.service.AuthService;
import org.josemejia.system.utils.AlertUtils;
import org.josemejia.system.utils.AnimationUtils;
import org.josemejia.system.utils.ImagenUtils;
import org.josemejia.system.utils.ValidationsUtils;
import org.josemejia.system.utils.ViewFactory;

public class LoginController {

    @FXML
    private VBox raiz;
    @FXML
    private ImageView imgLogo;
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblError;

    @FXML
    private Button btnLogin;

    private final AuthService authService = new AuthService();
    private final ViewFactory viewFactory = new ViewFactory();

    @FXML
    private void initialize() {
        imgLogo.setImage(ImagenUtils.cargarImagenLocal("logo.png"));
        AnimationUtils.aplicarFadeIn(raiz);
        AnimationUtils.aplicarEfectoHover(btnLogin);
        AnimationUtils.aplicarFocoAnimado(txtUsuario);
        AnimationUtils.aplicarFocoAnimado(txtPassword);
        lblError.setText("");
    }

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
        String password = txtPassword.getText() == null ? "" : txtPassword.getText();

        if (ValidationsUtils.esCampoVacio(usuario) || ValidationsUtils.esCampoVacio(password)) {
            mostrarError("Usuario y contraseña son obligatorios.");
            AnimationUtils.aplicarFadeIn(lblError);
            AnimationUtils.aplicarSacudida(lblError);
            AlertUtils.mostrarAlertaPersonalizada(
                    "Datos incompletos",
                    "Usuario y contraseña son obligatorios.",
                    AlertUtils.TipoNotificacion.ADVERTENCIA);
            return;
        }

        lblError.setText("");

        try {
            Usuario encontrado = authService.login(usuario, password);

            if (encontrado == null) {
                mostrarError("Usuario o contraseña incorrectos.");
                AnimationUtils.aplicarFadeIn(lblError);
                AnimationUtils.aplicarSacudida(lblError);
                AlertUtils.mostrarAlertaPersonalizada(
                        "Error de inicio de sesión",
                        "El usuario o la contraseña son incorrectos.",
                        AlertUtils.TipoNotificacion.ERROR);
                return;
            }

            lblError.setText("");
            SesionManager.getInstanciaSessionManager().setUsuarioActual(encontrado);
            viewFactory.viewDashboard();

        } catch (RuntimeException excepcion) {
            mostrarError("No se pudo iniciar sesión. Intenta de nuevo.");
            AlertUtils.mostrarAlertaPersonalizada(
                    "Error de inicio de sesión",
                    "No se pudo iniciar sesión. Intenta de nuevo.",
                    AlertUtils.TipoNotificacion.ERROR);
            excepcion.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }
}
