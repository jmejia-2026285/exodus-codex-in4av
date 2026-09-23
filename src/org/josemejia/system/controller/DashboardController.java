package org.josemejia.system.controller;

/**
 *
 * @author informatica
 */
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.josemejia.system.model.Usuario;
import org.josemejia.system.utils.AnimationUtils;
import org.josemejia.system.utils.SesionManager;
import javafx.scene.layout.VBox;
import org.josemejia.system.utils.ViewFactory;

public class DashboardController {
// añadir campos @FXML

    @FXML
    private VBox tarjetaAgregarTitulo;
    @FXML
    private VBox tarjetaNuevoBibliotecario;
    @FXML
    private BorderPane raiz;

    @FXML
    private Label lblBienvenida;

    @FXML
    private Label lblRol;

    @FXML
    private Button btnCatalogo;

    @FXML
    private Button btnRegistrarBibliotecario;
    
    @FXML
    private Button btnGestionPersonal;

    @FXML
    private Button btnCerrarSesion;

    private final ViewFactory viewFactory = new ViewFactory();

    @FXML
    private void initialize() {
        Usuario usuarioActual = SesionManager.getInstanciaSessionManager().getUsuarioActual();

        if (usuarioActual != null) {
            lblBienvenida.setText("Bienvenido(a), " + usuarioActual.getNombre());
            lblRol.setText(usuarioActual.getRol());
        }

        boolean esJefe = usuarioActual != null && usuarioActual.esBibliotecarioJefe();
        btnRegistrarBibliotecario.setVisible(esJefe);
        btnRegistrarBibliotecario.setManaged(esJefe);
        
        btnGestionPersonal.setVisible(esJefe);
        btnGestionPersonal.setManaged(esJefe);
        

        AnimationUtils.aplicarFadeIn(raiz);
        AnimationUtils.aplicarEfectoHover(btnCatalogo);
        AnimationUtils.aplicarEfectoHover(btnRegistrarBibliotecario);
        AnimationUtils.aplicarEfectoHover(btnGestionPersonal);
        AnimationUtils.aplicarEfectoHover(btnCerrarSesion);
        AnimationUtils.aplicarEfectoHoverTarjeta(tarjetaAgregarTitulo);
        AnimationUtils.aplicarEfectoHoverTarjeta(tarjetaNuevoBibliotecario);
    }

    @FXML
    private void handleCatalogo() {
        viewFactory.viewCatalogo();
    }

    @FXML
    private void handleRegistrarBibliotecario() {
        viewFactory.viewRegistro();
    }
    @FXML
    private void handleGestionPersonal() {
        viewFactory.viewPersonal();
    }

    @FXML
    private void handleCerrarSesion() {
        SesionManager.getInstanciaSessionManager().cerrarSesion();
        viewFactory.viewLogin();
    }
}
