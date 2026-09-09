/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.josemejia.system.utils;

/**
 *
 * @author mejia
 */
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import javafx.scene.image.Image;

/**
 * Maneja imágenes como archivo local: las empaquetadas con el proyecto (logo,
 * placeholder) y las que el usuario elige desde su equipo (portadas de libro),
 * que se copian a una carpeta persistente en vez de guardarse como bytes en la
 * base de datos. Mismo patrón que ImagenUtils/copiarImagenAAppData de Cartyx.
 */
public class ImagenUtils {

    private static final String RUTA_BASE_RECURSOS = "/org/josemejia/sgb/resources/image/";

    private static final String CARPETA_PORTADAS
            = System.getProperty("user.home") + File.separator + ".exoduscodex" + File.separator + "portadas";

    private ImagenUtils() {
    }

    /**
     * Carga una imagen empaquetada dentro del proyecto (ej. el logo).
     */
    public static Image cargarImagenLocal(String nombreArchivo) {
        InputStream flujo = ImagenUtils.class.getResourceAsStream(RUTA_BASE_RECURSOS + nombreArchivo);
        if (flujo == null) {
            return null;
        }
        return new Image(flujo);
    }

    /**
     * Copia la imagen elegida por el usuario (FileChooser) a una carpeta local
     * persistente y devuelve la ruta absoluta que se debe guardar en la BD.
     */
    public static String copiarPortadaAAppData(File archivoOriginal) {
        try {
            Path carpetaDestino = Path.of(CARPETA_PORTADAS);
            Files.createDirectories(carpetaDestino);

            String extension = obtenerExtension(archivoOriginal.getName());
            String nombreUnico = UUID.randomUUID() + extension;
            Path destino = carpetaDestino.resolve(nombreUnico);

            Files.copy(archivoOriginal.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

            return destino.toAbsolutePath().toString();

        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar la portada seleccionada.", e);
        }
    }

    /**
     * Carga una portada guardada previamente a partir de su ruta absoluta en
     * disco.
     */
    public static Image cargarPortadaDesdeRuta(String rutaAbsoluta) {
        if (rutaAbsoluta == null || rutaAbsoluta.isBlank()) {
            return null;
        }

        File archivo = new File(rutaAbsoluta);
        if (!archivo.exists()) {
            return null;
        }

        return new Image(archivo.toURI().toString());
    }

    private static String obtenerExtension(String nombreArchivo) {
        int punto = nombreArchivo.lastIndexOf('.');
        return punto >= 0 ? nombreArchivo.substring(punto) : ".png";
    }
}
