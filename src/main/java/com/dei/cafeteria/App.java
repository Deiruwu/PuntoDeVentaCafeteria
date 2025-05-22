package com.dei.cafeteria;

import com.dei.cafeteria.controlador.ControladorMesero;
import com.dei.cafeteria.dao.EmpleadoDAO;
import com.dei.cafeteria.dao.RolDAO;
import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.util.ColorPaleta;
import com.dei.cafeteria.vista.*;

import javax.swing.*;
import java.awt.*;

public class App {
    private static final Color COLOR_TEXTO = new Color(44, 59, 71);           // Azul Medianoche #2C3B47
    private static final Color COLOR_BOTON = new Color(212, 146, 93);
    /**
     * Metodo principal para pruebas
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        try {
            // Establecer Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Mejoras adicionales al UI general
            UIManager.put("TextField.caretForeground", COLOR_TEXTO);
            UIManager.put("PasswordField.caretForeground", COLOR_TEXTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame();
            }
        });
    }
}
