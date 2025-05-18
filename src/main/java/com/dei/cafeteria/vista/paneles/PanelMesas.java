package com.dei.cafeteria.vista.paneles;

import javax.swing.*;
import java.awt.*;

import com.dei.cafeteria.vista.componentes.PanelBase;

/**
 * Panel para mostrar y gestionar las mesas
 */
public class PanelMesas extends PanelBase {

    public PanelMesas() {
        super();
    }

    @Override
    protected void inicializarComponentes() {
        setLayout(new BorderLayout());
        JLabel titulo = new JLabel("Gestión de Mesas", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titulo, BorderLayout.NORTH);

        // Aquí iría la implementación específica del panel de mesas
    }

    @Override
    protected void establecerEventos() {
        // Implementación de eventos específicos
    }

    @Override
    public void actualizarContenido() {
        // Actualizar datos del panel
    }

    public void recargarMesas() {
        // Recargar información de mesas desde la base de datos
    }
}