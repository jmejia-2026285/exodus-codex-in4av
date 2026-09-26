package org.josemejia.system.controller;

import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

import java.io.File;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.josemejia.system.utils.ImagenUtils;
import org.josemejia.system.utils.SceneManager;

public class PersonalController {

    @FXML
    private TextField txtBuscarId;
    @FXML
    private Button btnBuscar, btnVerTodos, btnVolver;
    @FXML
    private Label lblTotalPersonal;
    @FXML
    private FlowPane panelTarjetas;
    @FXML
    private TextField txtNombre, txtApellido, txtCorreo, txtUsuario;
    @FXML
    private ImageView imgFotoPreview;
    @FXML
    private Button btnSeleccionarFoto, btnGuardar, btnLimpiar;

    private final UsuarioService usuarioService = new UsuarioService();
    private final ViewFactory viewFactory = new ViewFactory();
    private Usuario usuarioSeleccionado;
    private String rutaFotoActual;

    @FXML
    private void initialize() {
        AnimationUtils.aplicarFadeIn(panelTarjetas);
        AnimationUtils.aplicarEfectoHover(btnBuscar);
        AnimationUtils.aplicarEfectoHover(btnVerTodos);
        AnimationUtils.aplicarEfectoHover(btnVolver);
        AnimationUtils.aplicarFocoAnimado(txtBuscarId);
        AnimationUtils.aplicarEfectoHover(btnSeleccionarFoto);
        AnimationUtils.aplicarEfectoHover(btnGuardar);
        AnimationUtils.aplicarEfectoHover(btnLimpiar);
        AnimationUtils.aplicarFocoAnimado(txtNombre);
        AnimationUtils.aplicarFocoAnimado(txtApellido);
        AnimationUtils.aplicarFocoAnimado(txtCorreo);
        AnimationUtils.aplicarFocoAnimado(txtUsuario);
        ImagenUtils.aplicarEsquinasRedondeadas(imgFotoPreview, 10);

        cargarTodos();
        actualizarModoFormulario();
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
                AlertUtils.mostrarAlertaPersonalizada("Personal", "No existe ninguna cuenta con el ID \"" + id + "\".", TipoNotificacion.SIN_RESULTADOS);
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
    
        @FXML
    private void handleSeleccionarFoto() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar fotografía");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File archivo = selector.showOpenDialog(SceneManager.getInstanciaSceneManager().getStagePrincipal());
        if (archivo != null) {
            try {
                rutaFotoActual = ImagenUtils.copiarFotoPersonalAAppData(archivo);
                imgFotoPreview.setImage(ImagenUtils.cargarPortadaDesdeRuta(rutaFotoActual));
            } catch (RuntimeException e) {
                mostrarError(e);
            }
        }
    }

    @FXML
    private void handleGuardar() {
        if (usuarioSeleccionado == null) {
            AlertUtils.mostrarAlertaPersonalizada("Personal", "Selecciona una cuenta para editar.", TipoNotificacion.ADVERTENCIA);
            return;
        }
        String error = validarFormulario();
        if (error != null) {
            AlertUtils.mostrarAlertaPersonalizada("Datos incompletos", error, TipoNotificacion.ADVERTENCIA);
            return;
        }
        try {
            Usuario usuarioActual = SesionManager.getInstanciaSessionManager().getUsuarioActual();
            usuarioSeleccionado.setNombre(txtNombre.getText().trim());
            usuarioSeleccionado.setApellido(txtApellido.getText().trim());
            usuarioSeleccionado.setCorreo(txtCorreo.getText().trim());
            usuarioSeleccionado.setUsuario(txtUsuario.getText().trim());
            usuarioSeleccionado.setFoto(rutaFotoActual);

            usuarioService.actualizarBibliotecario(usuarioSeleccionado, usuarioActual);
            AlertUtils.mostrarAlertaPersonalizada("Personal", "La cuenta se actualizó correctamente.", TipoNotificacion.USUARIO_ACTUALIZADO);
            handleLimpiar();
            cargarTodos();
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    @FXML
    private void handleLimpiar() {
        usuarioSeleccionado = null;
        rutaFotoActual = null;
        txtNombre.clear();
        txtApellido.clear();
        txtCorreo.clear();
        txtUsuario.clear();
        imgFotoPreview.setImage(null);
        actualizarModoFormulario();
    }

    private void actualizarModoFormulario() {
        btnGuardar.setDisable(usuarioSeleccionado == null);
    }

    private String validarFormulario() {
        if (ValidationsUtils.esCampoVacio(txtNombre.getText())) {
            return "El nombre es obligatorio.";
        }
        if (ValidationsUtils.esCampoVacio(txtApellido.getText())) {
            return "El apellido es obligatorio.";
        }
        String errorCorreo = ValidationsUtils.obtenerErrorCorreo(txtCorreo.getText().trim());
        if (errorCorreo != null) {
            return errorCorreo;
        }
        if (ValidationsUtils.esCampoVacio(txtUsuario.getText())) {
            return "El nombre de usuario es obligatorio.";
        }
        return null;
    }

    private void seleccionarUsuario(Usuario usuario) {
        usuarioSeleccionado = usuario;
        rutaFotoActual = usuario.getFoto();
        txtNombre.setText(usuario.getNombre());
        txtApellido.setText(usuario.getApellido());
        txtCorreo.setText(usuario.getCorreo());
        txtUsuario.setText(usuario.getUsuario());
        imgFotoPreview.setImage(ImagenUtils.cargarPortadaDesdeRuta(usuario.getFoto()));
        actualizarModoFormulario();
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
        ImageView foto = new ImageView(ImagenUtils.cargarPortadaDesdeRuta(usuario.getFoto()));
        foto.setFitWidth(100);
        foto.setFitHeight(100);
        foto.getStyleClass().add("libro-tarjeta-imagen");
        ImagenUtils.aplicarEsquinasRedondeadas(foto, 50);

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

        Button btnEditarTarjeta = new Button("Editar");
        btnEditarTarjeta.getStyleClass().add("boton-tarjeta");
        btnEditarTarjeta.setOnAction(e -> seleccionarUsuario(usuario));

        HBox acciones = new HBox(8, btnEditarTarjeta);
        acciones.setAlignment(Pos.CENTER);

        if (!usuario.esBibliotecarioJefe()) {
            Button btnEliminarTarjeta = new Button("Eliminar");
            btnEliminarTarjeta.getStyleClass().add("boton-tarjeta-peligro");
            btnEliminarTarjeta.setOnAction(e -> eliminarBibliotecario(usuario));
            acciones.getChildren().add(btnEliminarTarjeta);
        }

        VBox tarjeta = new VBox(6, foto, nombre, correo, idLabel, info, acciones);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.setPrefWidth(190);
        tarjeta.getStyleClass().add("libro-tarjeta");

        AnimationUtils.aplicarSlideIn(tarjeta, 24);
        AnimationUtils.aplicarEfectoHoverTarjeta(tarjeta);
        return tarjeta;
    }

        private void eliminarBibliotecario(Usuario usuario) {
        boolean confirmado = AlertUtils.mostrarConfirmacion(
                "Eliminar bibliotecario",
                "¿Seguro que quieres eliminar a " + usuario.getNombre() + " " + usuario.getApellido() + " del sistema? Esta acción no se puede deshacer.",
                "Eliminar");
        if (!confirmado) {
            return;
        }
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
        String mensaje = e.getMessage();
        TipoNotificacion tipo = mensaje != null && mensaje.contains("Bibliotecario Jefe puede")
                ? TipoNotificacion.ACCESO_DENEGADO
                : TipoNotificacion.ERROR;
        AlertUtils.mostrarAlertaPersonalizada("Personal", mensaje, tipo);
    }
}
