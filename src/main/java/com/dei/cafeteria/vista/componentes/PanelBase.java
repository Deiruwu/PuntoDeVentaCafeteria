package com.dei.cafeteria.vista.componentes;

import javax.swing.JPanel;
import com.dei.cafeteria.util.ColorPaleta;

/**
 * Panel base que proporciona comportamiento común para todos los paneles de la aplicación
 */
public abstract class PanelBase extends JPanel {

    public PanelBase() {
        setBackground(ColorPaleta.CREMA.getColor());
        inicializarComponentes();
        establecerEventos();
    }

    /**
     * Metodo que debe ser implementado por las clases hijas para inicializar sus componentes
     */
    protected abstract void inicializarComponentes();

    /**
     * Metodo que debe ser implementado por las clases hijas para establecer los eventos
     */
    protected abstract void establecerEventos();

    /**
     * Metodo para actualizar el contenido del panel
     */
    public abstract void actualizarContenido();
}