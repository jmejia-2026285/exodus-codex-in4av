package org.josemejia.system.controller;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.josemejia.system.model.Usuario;
import org.josemejia.system.service.UsuarioService;
import org.josemejia.system.utils.AlertUtils;
import org.josemejia.system.utils.AlertUtils.TipoNotificacion;
import org.josemejia.system.utils.AnimationUtils;
import org.josemejia.system.utils.ImagenUtils;
import org.josemejia.system.utils.SesionManager;
import org.josemejia.system.utils.ValidationsUtils;

/**
 * Controlador del pop-up "Editar cuenta". Mismo patrón que
 * EditarLibroController: se abre como una ventana aparte (misma cara que el
 * resto de la aplicación) cuando se presiona "Editar" en una tarjeta de
 * Gestión de personal.
 */
public class EditarBibliotecarioController {

    @FXML
    private TextField txtNombre, txtApellido, txtCorreo, txtUsuario;
    @FXML
    private ImageView imgFotoPreview;
    @FXML
    private HBox filaEliminar;
    @FXML
    private Button btnSeleccionarFoto, btnGuardar, btnEliminar, btnCancelar;

    private final UsuarioService usuarioService = new UsuarioService();
    private Usuario usuario;
    private Stage stage;
    private String rutaFotoActual;
    private Runnable alGuardarOEliminar;

    @FXML
    private void initialize() {
        AnimationUtils.aplicarEfectoHover(btnGuardar);
        AnimationUtils.aplicarEfectoHover(btnEliminar);
        AnimationUtils.aplicarEfectoHover(btnCancelar);
        AnimationUtils.aplicarFocoAnimado(txtNombre);
        AnimationUtils.aplicarFocoAnimado(txtApellido);
        AnimationUtils.aplicarFocoAnimado(txtCorreo);
        AnimationUtils.aplicarFocoAnimado(txtUsuario);
        ImagenUtils.aplicarEsquinasRedondeadas(imgFotoPreview, 10);
    }

    /**
     * Carga los datos del bibliotecario seleccionado en el listado dentro del
     * formulario. Debe llamarse antes de mostrar la ventana.
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        this.rutaFotoActual = usuario.getFoto();
        txtNombre.setText(usuario.getNombre());
        txtApellido.setText(usuario.getApellido());
        txtCorreo.setText(usuario.getCorreo());
        txtUsuario.setText(usuario.getUsuario());
        imgFotoPreview.setImage(ImagenUtils.cargarPortadaDesdeRuta(usuario.getFoto()));
        filaEliminar.setVisible(!usuario.esBibliotecarioJefe());
        filaEliminar.setManaged(!usuario.esBibliotecarioJefe());
        if (usuario.esBibliotecarioJefe()) {
            btnCancelar.setMaxWidth(Double.MAX_VALUE);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Callback que Gestión de personal usa para refrescar la grilla de
     * tarjetas después de guardar cambios o eliminar la cuenta.
     */
    public void setAlGuardarOEliminar(Runnable alGuardarOEliminar) {
        this.alGuardarOEliminar = alGuardarOEliminar;
    }

    @FXML
    private void handleSeleccionarFoto() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar fotografía");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File archivo = selector.showOpenDialog(stage);
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
        String error = validarFormulario();
        if (error != null) {
            AlertUtils.mostrarAlertaPersonalizada("Datos incompletos", error, TipoNotificacion.ADVERTENCIA);
            return;
        }
        try {
            Usuario usuarioActual = SesionManager.getInstanciaSessionManager().getUsuarioActual();
            usuario.setNombre(txtNombre.getText().trim());
            usuario.setApellido(txtApellido.getText().trim());
            usuario.setCorreo(txtCorreo.getText().trim());
            usuario.setUsuario(txtUsuario.getText().trim());
            usuario.setFoto(rutaFotoActual);

            usuarioService.actualizarBibliotecario(usuario, usuarioActual);
            AlertUtils.mostrarAlertaPersonalizada("Personal", "La cuenta se actualizó correctamente.", TipoNotificacion.USUARIO_ACTUALIZADO);
            notificarCambioYCerrar();
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    @FXML
    private void handleEliminar() {
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
            notificarCambioYCerrar();
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    @FXML
    private void handleCancelar() {
        stage.close();
    }

    private void notificarCambioYCerrar() {
        if (alGuardarOEliminar != null) {
            alGuardarOEliminar.run();
        }
        stage.close();
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

    private void mostrarError(RuntimeException e) {
        String mensaje = e.getMessage();
        TipoNotificacion tipo = mensaje != null && mensaje.contains("Bibliotecario Jefe puede")
                ? TipoNotificacion.ACCESO_DENEGADO
                : TipoNotificacion.ERROR;
        AlertUtils.mostrarAlertaPersonalizada("Personal", mensaje, tipo);
    }
}
