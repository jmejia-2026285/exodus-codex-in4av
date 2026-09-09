package org.josemejia.system.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.josemejia.system.model.Usuario;
import org.josemejia.system.service.AuthService;
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
        lblError.setText("");
    }

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
        String password = txtPassword.getText() == null ? "" : txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarError("Ingresa usuario y contraseña.");
            return;
        }

        try {
            Usuario usuarioAutenticado = authService.login(usuario, password);

            if (usuarioAutenticado == null) {
                mostrarError("Usuario o contraseña incorrectos.");
                return;
            }

            lblError.setText("");
            viewFactory.viewDashboard();

        } catch (RuntimeException excepcion) {
            mostrarError("No se pudo iniciar sesión. Intenta de nuevo.");
            excepcion.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }
}