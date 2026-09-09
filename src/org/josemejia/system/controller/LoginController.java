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
        lblError.setText("");
    }

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        // 1. Validación de campos vacíos usando ValidationsUtils
        if (ValidationsUtils.esCampoVacio(usuario) || ValidationsUtils.esCampoVacio(password)) {
            lblError.setText("Usuario y contraseña son obligatorios.");
            AnimationUtils.aplicarFadeIn(lblError); // Detalle pro: anima la aparición del error
            return;
        }

        // Limpiamos el label de error antes de hacer la petición al servicio
        lblError.setText("");

        // 2. Autenticación
        Usuario encontrado = authService.login(usuario.trim(), password);

        // 3. Manejo de credenciales incorrectas usando AlertUtils
        if (encontrado == null) {
            lblError.setText("Usuario o contraseña incorrectos.");
            AnimationUtils.aplicarFadeIn(lblError);

            // 👇 AHORA SÍ USAMOS ALERTUTILS CORRECTAMENTE 👇
            AlertUtils.mostrarAlertaPersonalizada(
                    "Error de inicio de sesión",
                    "El usuario o la contraseña son incorrectos.",
                    AlertUtils.TipoNotificacion.ERROR);
            return;
        }

        // 4. Login exitoso
        SesionManager.getInstanciaSessionManager().setUsuarioActual(encontrado);
        viewFactory.viewDashboard();
    }
}
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
