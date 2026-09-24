package org.josemejia.system.controller;

import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.josemejia.system.model.Usuario;
import org.josemejia.system.service.UsuarioService;
import org.josemejia.system.utils.AlertUtils;
import org.josemejia.system.utils.AlertUtils.TipoNotificacion;
import org.josemejia.system.utils.AnimationUtils;
import org.josemejia.system.utils.SesionManager;
import org.josemejia.system.utils.ValidationsUtils;
import org.josemejia.system.utils.ViewFactory;

public class PersonalController {

    @FXML
    private TextField txtBuscarId;
    @FXML
    private Button btnBuscar, btnVerTodos, btnVolver;
    @FXML
    private Label lblTotalPersonal;
    @FXML
    private FlowPane panelTarjetas;

    private final UsuarioService usuarioService = new UsuarioService();
    private final ViewFactory viewFactory = new ViewFactory();

    @FXML
    private void initialize() {
        AnimationUtils.aplicarFadeIn(panelTarjetas);
        AnimationUtils.aplicarEfectoHover(btnBuscar);
        AnimationUtils.aplicarEfectoHover(btnVerTodos);
        AnimationUtils.aplicarEfectoHover(btnVolver);
        AnimationUtils.aplicarFocoAnimado(txtBuscarId);
        cargarTodos();
    }

    @FXML
    private void handleBuscar() {
        String id = txtBuscarId.getText() == null ? "" : txtBuscarId.getText().trim();
        if (ValidationsUtils.esCampoVacio(id)) {
            cargarTodos();
            return;
        }
        try {
            Usuario usuarioActual = SesionManager.getInstanciaSessionManager().getUsuarioActual();
            Usuario encontrado = usuarioService.buscarPorId(id, usuarioActual);
            panelTarjetas.getChildren().clear();
            if (encontrado == null) {
                lblTotalPersonal.setText("Sin resultados para el ID \"" + id + "\"");
                return;
            }
            panelTarjetas.getChildren().add(crearTarjeta(encontrado));
            lblTotalPersonal.setText("1 resultado para el ID \"" + id + "\"");
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    @FXML
    private void handleVerTodos() {
        txtBuscarId.clear();
        cargarTodos();
    }

    @FXML
    private void handleVolver() {
        viewFactory.viewDashboard();
    }

    private void cargarTodos() {
        try {
            Usuario usuarioActual = SesionManager.getInstanciaSessionManager().getUsuarioActual();
            List<Usuario> personal = usuarioService.listarBibliotecarios(usuarioActual);
            panelTarjetas.getChildren().clear();
            personal.forEach(usuario -> panelTarjetas.getChildren().add(crearTarjeta(usuario)));
            lblTotalPersonal.setText(personal.size() == 1
                    ? "1 cuenta registrada"
                    : personal.size() + " cuentas registradas");
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private VBox crearTarjeta(Usuario usuario) {
        Label nombre = new Label(usuario.getNombre() + " " + usuario.getApellido());
        nombre.getStyleClass().add("libro-tarjeta-titulo");
        nombre.setWrapText(true);
        nombre.setMaxWidth(150);

        Label correo = new Label(usuario.getCorreo());
        correo.getStyleClass().add("libro-tarjeta-autor");
        correo.setWrapText(true);
        correo.setMaxWidth(150);

        Label idLabel = new Label("ID: " + usuario.getIdUsuario() + " · @" + usuario.getUsuario());
        idLabel.getStyleClass().add("libro-tarjeta-autor");

        Label rol = new Label(usuario.getRol());
        rol.getStyleClass().add(usuario.esBibliotecarioJefe() ? "chip-exito" : "chip-error");

        HBox info = new HBox(8, rol);
        info.setAlignment(Pos.CENTER);

        VBox tarjeta = new VBox(6, nombre, correo, idLabel, info);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.setPrefWidth(190);
        tarjeta.getStyleClass().add("libro-tarjeta");

        if (!usuario.esBibliotecarioJefe()) {
            Button btnEliminarTarjeta = new Button("Eliminar");
            btnEliminarTarjeta.getStyleClass().add("boton-tarjeta-peligro");
            btnEliminarTarjeta.setOnAction(e -> eliminarBibliotecario(usuario));
            tarjeta.getChildren().add(btnEliminarTarjeta);
        }

        AnimationUtils.aplicarSlideIn(tarjeta, 24);
        AnimationUtils.aplicarEfectoHoverTarjeta(tarjeta);
        return tarjeta;
    }

    private void eliminarBibliotecario(Usuario usuario) {
        try {
            Usuario usuarioActual = SesionManager.getInstanciaSessionManager().getUsuarioActual();
            usuarioService.eliminarBibliotecario(usuario.getIdUsuario(), usuarioActual);

          
            AlertUtils.mostrarAlertaPersonalizada("Personal Eliminado",
                    "El bibliotecario " + usuario.getNombre() + " " + usuario.getApellido() + " fue eliminado del sistema.",
                    TipoNotificacion.USUARIO_ELIMINADO);

            cargarTodos();
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void mostrarError(RuntimeException e) {
        AlertUtils.mostrarAlertaPersonalizada("Personal", e.getMessage(), TipoNotificacion.ERROR);
    }
}
