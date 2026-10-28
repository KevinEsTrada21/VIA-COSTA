package com.viacosta.util;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.function.Predicate;

/**
 * Validador y formateador en tiempo real para campos de entrada Java Swing (JTextField / JPasswordField).
 * Bloquea de forma inmediata que el usuario ingrese más caracteres de los permitidos
 * o caracteres no válidos (por ejemplo letras en el DNI o números en los nombres).
 */
public class FieldValidator {

    /**
     * Restringe a solo dígitos numéricos y un máximo exacto de caracteres.
     * Ejemplo: DNI (8 dígitos), Teléfono (9 dígitos).
     */
    public static void soloNumeros(JTextField field, int maxLongitud) {
        aplicarFiltro(field, text -> text.length() <= maxLongitud && text.matches("\\d*"));
    }

    /**
     * Restringe a solo letras (incluye tildes, ñ, ü y espacios) y longitud máxima.
     * Bloquea números y símbolos.
     * Ejemplo: Nombres y Apellidos.
     */
    public static void soloLetras(JTextField field, int maxLongitud) {
        aplicarFiltro(field, text -> text.length() <= maxLongitud && text.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]*"));
    }

    /**
     * Limita longitud máxima y no admite espacios en blanco.
     * Ejemplo: Correo electrónico, Usuario.
     */
    public static void sinEspacios(JTextField field, int maxLongitud) {
        aplicarFiltro(field, text -> text.length() <= maxLongitud && !text.contains(" "));
    }

    /**
     * Limita la longitud máxima de caracteres permitidos.
     * Ejemplo: Dirección, Búsqueda libre, Contraseña.
     */
    public static void longitudMaxima(JTextField field, int maxLongitud) {
        aplicarFiltro(field, text -> text.length() <= maxLongitud);
    }

    private static void aplicarFiltro(JTextField field, Predicate<String> validador) {
        if (field == null || !(field.getDocument() instanceof AbstractDocument)) {
            return;
        }
        AbstractDocument doc = (AbstractDocument) field.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null) return;
                String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String nuevo = actual.substring(0, offset) + string + actual.substring(offset);
                if (validador.test(nuevo)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String textoReemplazo = text != null ? text : "";
                String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String nuevo = actual.substring(0, offset) + textoReemplazo + actual.substring(offset + length);
                if (validador.test(nuevo)) {
                    super.replace(fb, offset, length, textoReemplazo, attrs);
                }
            }
        });
    }
}
