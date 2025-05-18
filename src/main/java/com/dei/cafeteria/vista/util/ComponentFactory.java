package com.dei.cafeteria.vista.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.dei.cafeteria.util.ColorPaleta;

/**
 * Clase utilitaria para crear componentes de interfaz gráfica
 */
public class ComponentFactory {

    private ComponentFactory() {
        // Constructor privado para evitar instanciación
    }

    /**
     * Crea un botón de menú con estilo personalizado
     * @param texto Texto del botón
     * @return Botón configurado
     */
    public static JButton crearBotonMenu(String texto) {
        JButton boton = new JButton(texto);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(180, 40));
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        boton.setForeground(Color.WHITE);
        boton.setBackground(ColorPaleta.AZUL.getColor());
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(ColorPaleta.TERRACOTA.getColor());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(ColorPaleta.AZUL.getColor());
            }
        });

        return boton;
    }

    /**
     * Crea una etiqueta de menú con estilo personalizado
     * @param texto Texto de la etiqueta
     * @return Etiqueta configurada
     */
    public static JLabel crearLabelMenu(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(ColorPaleta.CREMA.getColor());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Crea una etiqueta normal con estilo personalizado
     * @param texto Texto de la etiqueta
     * @return Etiqueta configurada
     */
    public static JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(ColorPaleta.AZUL.getColor());
        return label;
    }

    /**
     * Carga una imagen y la redimensiona
     * @param rutaImagen Ruta de la imagen
     * @param ancho Ancho deseado
     * @param alto Alto deseado
     * @return ImageIcon redimensionado
     */
    public static ImageIcon cargarImagen(String rutaImagen, int ancho, int alto) {
        if (rutaImagen == null || rutaImagen.isEmpty()) {
            rutaImagen = "imagenes/empleado_default.png";
        } else if (rutaImagen.contains("/imagenes/")) {
            int indice = rutaImagen.indexOf("/imagenes/");
            rutaImagen = rutaImagen.substring(indice + 1);
        }

        ImageIcon imagen = null;
        try {
            if (new java.io.File(rutaImagen).exists()) {
                imagen = new ImageIcon(rutaImagen);
            } else {
                imagen = new ImageIcon("imagenes/empleado_default.png");
            }

            // Redimensionar
            Image img = imagen.getImage();
            Image imgRedimensionada = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            return new ImageIcon(imgRedimensionada);
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + e.getMessage());
            return new ImageIcon("imagenes/empleado_default.png");
        }
    }
}
