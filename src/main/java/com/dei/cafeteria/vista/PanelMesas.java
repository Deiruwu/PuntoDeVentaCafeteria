package com.dei.cafeteria.vista;

import com.dei.cafeteria.controlador.ControladorMesas;
import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.modelo.Mesa;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel para mostrar y gestionar las mesas disponibles/ocupadas
 */
public class PanelMesas extends JPanel {

    private JPanel panelContenidoMesas;
    private JScrollPane scrollPane;
    private JLabel lblTitulo;

    private ControladorMesas controladorMesas;
    private List<Mesa> listaMesas;

    public PanelMesas() {
        this.controladorMesas = new ControladorMesas();
        setLayout(new BorderLayout());
        setBackground(VistaMesero.COLOR_CREMA);

        inicializarComponentes();
        cargarMesasDesdeControlador();
    }

    private void inicializarComponentes() {
        lblTitulo = new JLabel("Mesas Disponibles");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(VistaMesero.COLOR_AZUL);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 0));

        panelContenidoMesas = new JPanel();
        panelContenidoMesas.setLayout(new GridLayout(0, 3, 15, 15));
        panelContenidoMesas.setBackground(VistaMesero.COLOR_CREMA);
        panelContenidoMesas.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        scrollPane = new JScrollPane(panelContenidoMesas);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(lblTitulo, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void cargarMesasDesdeControlador() {
        try {
            listaMesas = controladorMesas.obtenerTodasLasMesas();
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar las mesas: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            listaMesas = new ArrayList<>();
        }

        actualizarVistaMesas();
    }

    private void actualizarVistaMesas() {
        panelContenidoMesas.removeAll();

        for (Mesa mesa : listaMesas) {
            panelContenidoMesas.add(crearPanelMesa(mesa));
        }

        panelContenidoMesas.revalidate();
        panelContenidoMesas.repaint();
    }

    private JPanel crearPanelMesa(Mesa mesa) {
        JPanel panel = new JPanel(new BorderLayout());
        int estadoMesa = mesa.getEstadoMesa();

        Color colorFondo = controladorMesas.getColor(estadoMesa);

        panel.setBackground(colorFondo);
        panel.setPreferredSize(new Dimension(200, 150));
        panel.setBorder(new SoftBevelBorder(BevelBorder.RAISED));

        JLabel lblNumero = new JLabel("Mesa " + mesa.getNumero());
        lblNumero.setHorizontalAlignment(SwingConstants.CENTER);
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNumero.setForeground(Color.WHITE);

        String estadoMesaStr = controladorMesas.getEstadoStr(estadoMesa);

        JLabel lblEstado = new JLabel(estadoMesaStr);
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEstado.setForeground(Color.WHITE);

        panel.add(lblNumero, BorderLayout.CENTER);
        panel.add(lblEstado, BorderLayout.SOUTH);

        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(colorFondo.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(colorFondo);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(panel,
                        "Mesa " + mesa.getNumero() + "\nEstado: " + estadoMesaStr,
                        "Información de Mesa",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return panel;
    }
}
