package com.dei.cafeteria.vista.componentes;

import javax.swing.*;
import java.awt.*;
import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.util.ColorPaleta;
import com.dei.cafeteria.vista.util.ComponentFactory;

/// Panel que muestra la información del empleado
public class PanelInfoEmpleado extends JPanel {

    private JLabel lblFotoEmpleado;
    private JLabel lblNombreEmpleado;
    private JLabel lblIdEmpleado;
    private Empleado empleado;

    public PanelInfoEmpleado(Empleado empleado) {
        this.empleado = empleado;
        configurarPanel();
        inicializarComponentes();
    }

    private void configurarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorPaleta.AZUL.getColor());
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setMaximumSize(new Dimension(180, 200));
    }

    private void inicializarComponentes() {
        // Cargar la foto del empleado
        ImageIcon imagenEmpleado = ComponentFactory.cargarImagen(empleado.getImagenUrl(), 120, 120);

        // Crear componentes de información del empleado
        lblFotoEmpleado = new JLabel(imagenEmpleado);
        lblFotoEmpleado.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFotoEmpleado.setBorder(BorderFactory.createLineBorder(ColorPaleta.CREMA.getColor(), 2));

        // Obtener nombre del empleado
        String nombreEmpleado = empleado.getNombre();
        if (nombreEmpleado == null || nombreEmpleado.isEmpty()) {
            nombreEmpleado = "Empleado (" + empleado.getId() + ")";
        }

        lblNombreEmpleado = new JLabel(nombreEmpleado);
        lblNombreEmpleado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombreEmpleado.setForeground(ColorPaleta.CREMA.getColor());
        lblNombreEmpleado.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Obtener ID del empleado
        int idEmpleado = empleado.getId();
        lblIdEmpleado = new JLabel("\uF111   Connected");
        lblIdEmpleado.setFont(new Font("fuenteNerd", 0, 12));
        lblIdEmpleado.setForeground(new Color(72, 160, 92, 255));
        lblIdEmpleado.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Agregar componentes al panel
        add(lblFotoEmpleado);
        add(Box.createVerticalStrut(10));
        add(lblNombreEmpleado);
        add(Box.createVerticalStrut(5));
        add(lblIdEmpleado);
    }

    public void actualizarEmpleado(Empleado empleado) {
        this.empleado = empleado;
        removeAll();
        inicializarComponentes();
        revalidate();
        repaint();
    }
}