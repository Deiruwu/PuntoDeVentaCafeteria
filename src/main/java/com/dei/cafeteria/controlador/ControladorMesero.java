package com.dei.cafeteria.controlador;

import javax.swing.JOptionPane;
import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.vista.PanelTomarOrden;
import com.dei.cafeteria.vista.VistaMesero;
import com.dei.cafeteria.vista.paneles.*;
import lombok.Getter;

/**
 * Controlador para la vista del mesero
 */
public class ControladorMesero {

    private VistaMesero vista;
    @Getter
    private Empleado meseroActual;

    public ControladorMesero(Empleado mesero) {
        this.meseroActual = mesero;
        this.vista = new VistaMesero(this, mesero);
    }

    public void mostrarVista() {
        vista.setVisible(true);
    }

    public void cambiarPanel(String nombrePanel) {
        vista.mostrarPanel(nombrePanel);
    }

    public boolean enviarPedido() {
        PanelTomarOrden panelTomarOrden = vista.getPanelTomarOrden();
        if (panelTomarOrden.validarPedido()) {
            // Aquí iría la lógica para enviar el pedido al sistema
            JOptionPane.showMessageDialog(vista,
                    "Pedido enviado correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            panelTomarOrden.limpiarPedido();
            vista.getPanelMesas().recargarMesas();
            vista.getPanelGestionOrdenes().actualizarOrdenes();
            return true;
        } else {
            JOptionPane.showMessageDialog(vista,
                    "No hay un pedido válido para enviar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }
}