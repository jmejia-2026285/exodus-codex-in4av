/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.josemejia.system.utils;

/**
 *
 * @author mejia
 */


import java.util.regex.Pattern;

public class ValidationsUtils {

    // Patrones precompilados para mejor rendimiento
    private static final Pattern PATRON_CORREO = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9._%+-]*@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PATRON_ENTERO = Pattern.compile("^-?\\d+$"); // Acepta números positivos y negativos

    private ValidationsUtils() {

    }

    public static boolean esCampoVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    public static boolean esCorreoValido(String correo) {
        return obtenerErrorCorreo(correo) == null;
    }

    public static String obtenerErrorCorreo(String correo) {
        if (esCampoVacio(correo)) {
            return "El correo es obligatorio.";
        }

        String valor = correo.trim();

        // El Regex ya valida espacios, cantidad de '@' y estructura del dominio.
        // Delegamos toda la validación de formato a la expresión regular para evitar código redundante.
        if (!PATRON_CORREO.matcher(valor).matches()) {
            return "El formato del correo no es válido (ej: usuario@dominio.com).";
        }

        return null; // Es válido
    }

    public static boolean esAnioValido(int anio) {
        // Puedes ajustar 2100 según las reglas de tu negocio
        return anio > 0 && anio <= 2100;
    }

    public static boolean esEnteroValido(String texto) {
        if (esCampoVacio(texto)) {
            return false;
        }

        return PATRON_ENTERO.matcher(texto.trim()).matches();
    }

    public static boolean esEnteroPositivoValido(String texto) {
        if (esCampoVacio(texto)) {
            return false;
        }
        return Pattern.compile("^\\d+$").matcher(texto.trim()).matches();
    }
}
