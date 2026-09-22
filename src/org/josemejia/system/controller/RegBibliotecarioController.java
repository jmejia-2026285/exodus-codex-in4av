package org.josemejia.system.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import org.josemejia.system.model.Usuario;
import org.josemejia.system.service.UsuarioService;
import org.josemejia.system.utils.AlertUtils;
import org.josemejia.system.utils.AnimationUtils;
import org.josemejia.system.utils.SesionManager;
import org.josemejia.system.utils.ValidationsUtils;
import org.josemejia.system.utils.ViewFactory;

public class RegBibliotecarioController {

    @FXML
    private VBox raiz;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtConfirmarPassword;
    @FXML
    private Label lblError;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnCancelar;

    private final UsuarioService usuarioService = new UsuarioService();
    private final ViewFactory viewFactory = new ViewFactory();

    @FXML
    private void initialize() {
        AnimationUtils.aplicarFadeIn(raiz);
        AnimationUtils.aplicarEfectoHover(btnRegistrar);
        lblError.setText("");
    }

    @FXML
    private void handleCancelar() {
        viewFactory.viewLogin();
    }

    @FXML
    private void handleRegistrar() {
        lblError.setText("");

        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        String apellido = txtApellido.getText() == null ? "" : txtApellido.getText().trim();
        String correo = txtCorreo.getText() == null ? "" : txtCorreo.getText().trim();
        String usuario = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
        String password = txtPassword.getText() == null ? "" : txtPassword.getText();
        String confirmarPassword = txtConfirmarPassword.getText() == null ? "" : txtConfirmarPassword.getText();

        // 1. Campos obligatorios
        if (ValidationsUtils.esCampoVacio(nombre)
                || ValidationsUtils.esCampoVacio(apellido)
                || ValidationsUtils.esCampoVacio(correo)
                || ValidationsUtils.esCampoVacio(usuario)
                || ValidationsUtils.esCampoVacio(password)
                || ValidationsUtils.esCampoVacio(confirmarPassword)) {
            mostrarError("Por favor, rellene todos los campos.");
            AlertUtils.mostrarAlertaPersonalizada(
                    "Datos incompletos",
                    "Por favor, rellene todos los campos.",
                    AlertUtils.TipoNotificacion.ADVERTENCIA);
            return;
        }

        // 2. Formato de correo
        String errorCorreo = ValidationsUtils.obtenerErrorCorreo(correo);
        if (errorCorreo != null) {
            mostrarError(errorCorreo);
            AlertUtils.mostrarAlertaPersonalizada(
                    "Correo inválido",
                    errorCorreo,
                    AlertUtils.TipoNotificacion.ADVERTENCIA);
            return;
        }

        // 3. Contraseñas coinciden
        if (!password.equals(confirmarPassword)) {
            mostrarError("Las contraseñas no coinciden.");
            AlertUtils.mostrarAlertaPersonalizada(
                    "Contraseñas distintas",
                    "Las contraseñas no coinciden.",
                    AlertUtils.TipoNotificacion.ADVERTENCIA);
            return;
        }

        // 4. Longitud de campos
        String errorLongitud = validarLongitudes(nombre, apellido, correo, usuario, password);
        if (errorLongitud != null) {
            mostrarError(errorLongitud);
            AlertUtils.mostrarAlertaPersonalizada(
                    "Datos inválidos",
                    errorLongitud,
                    AlertUtils.TipoNotificacion.ADVERTENCIA);
            return;
        }

        // 5. Registrar
        Usuario nuevoBibliotecario = new Usuario();
        nuevoBibliotecario.setNombre(nombre);
        nuevoBibliotecario.setApellido(apellido);
        nuevoBibliotecario.setCorreo(correo);
        nuevoBibliotecario.setUsuario(usuario);
        nuevoBibliotecario.setPassword(password);

        Usuario usuarioActual = SesionManager.getInstanciaSessionManager().getUsuarioActual();

        try {
            usuarioService.registrarBibliotecario(nuevoBibliotecario, usuarioActual);

            AlertUtils.mostrarAlertaPersonalizada(
                    "Registro completado",
                    "El bibliotecario ha sido registrado correctamente.",
                    AlertUtils.TipoNotificacion.EXITO);
            limpiarCampos();

        } catch (IllegalStateException excepcion) {
            mostrarError(excepcion.getMessage());
            AlertUtils.mostrarAlertaPersonalizada(
                    "Error al registrar",
                    excepcion.getMessage(),
                    AlertUtils.TipoNotificacion.ERROR);
        }
    }

    private String validarLongitudes(String nombre, String apellido, String correo, String usuario, String password) {
        if (nombre.length() > 50) {
            return "El campo NOMBRE es mayor a 50 letras.";
        }
        if (apellido.length() > 50) {
            return "El campo APELLIDO es mayor a 50 letras.";
        }
        if (correo.length() > 50) {
            return "El campo CORREO es mayor a 50 letras.";
        }
        if (usuario.length() > 25) {
            return "El campo USUARIO es mayor a 25 letras.";
        }
        if (password.length() > 35) {
            return "El campo CONTRASEÑA es mayor a 35 letras.";
        }
        return null;
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtApellido.clear();
        txtCorreo.clear();
        txtUsuario.clear();
        txtPassword.clear();
        txtConfirmarPassword.clear();
        lblError.setText("");
    }
}