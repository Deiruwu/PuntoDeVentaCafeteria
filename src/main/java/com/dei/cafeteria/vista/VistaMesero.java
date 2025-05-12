package com.dei.cafeteria.vista;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.dao.EmpleadoDAO;
import com.dei.cafeteria.dao.RolDAO;
import com.dei.cafeteria.modelo.Empleado;
import lombok.Getter;

/**
 * Clase principal que integra todos los componentes del módulo Mesero
 */
public class VistaMesero extends JFrame {

    private Empleado meseroActual;

    // Constantes para colores según la paleta especificada
    public static final Color COLOR_TERRACOTA = new Color(140, 94, 88);  // #8C5E58
    public static final Color COLOR_AZUL = new Color(44, 59, 71);       // #2C3B47
    public static final Color COLOR_CREMA = new Color(232, 218, 203);   // #E8DACB
    public static final Color COLOR_VERDE = new Color(97, 112, 91);     // #61705B
    public static final Color COLOR_AMBAR = new Color(212, 146, 93);    // #D4925D

    // Paneles principales
    private JPanel panelPrincipal;
    private JPanel panelContenido;
    private JPanel panelMenu;

    // Componentes del menú
    private JButton btnMesas;
    private JButton btnProductos;
    private JButton btnTomarOrden;
    private JButton btnEnviarPedido;

    // Paneles de contenido
    private PanelMesas panelMesas;
    private PanelProductos panelProductos;
    private PanelTomarOrden panelTomarOrden;

    // Panel actual mostrado
    private JPanel panelActual;

    public VistaMesero(Empleado meseroActual) {
        this.meseroActual = meseroActual;
        configurarVentana();
        inicializarComponentes();
        establecerEventos();
        mostrarPanel("mesas"); // Iniciar con panel de mesas
    }

    private void configurarVentana() {
        setTitle("Cafetería - Módulo Mesero");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configurar el panel principal con BorderLayout
        panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(COLOR_CREMA);
        setContentPane(panelPrincipal);
    }

    private void inicializarComponentes() {
        // Crear panel de menú lateral
        panelMenu = new JPanel();
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setBackground(COLOR_AZUL);
        panelMenu.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panelMenu.setPreferredSize(new Dimension(200, getHeight()));

        // Crear botones del menú con estilo personalizado
        btnMesas = crearBotonMenu("Mesas");
        btnProductos = crearBotonMenu("Productos");
        btnTomarOrden = crearBotonMenu("Tomar Orden");
        btnEnviarPedido = crearBotonMenu("Enviar Pedido");

        // Añadir botones al menú
        panelMenu.add(Box.createVerticalStrut(20));
        panelMenu.add(crearLabelMenu("MESERO"));
        panelMenu.add(Box.createVerticalStrut(40));
        panelMenu.add(btnMesas);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(btnProductos);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(btnTomarOrden);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(btnEnviarPedido);
        panelMenu.add(Box.createVerticalGlue());

        // Crear panel de contenido principal (donde se cargarán las diferentes vistas)
        panelContenido = new JPanel(new CardLayout());
        panelContenido.setBackground(COLOR_CREMA);

        // Inicializar los paneles específicos
        panelMesas = new PanelMesas();
        panelProductos = new PanelProductos();
        panelTomarOrden = new PanelTomarOrden(meseroActual);

        // Añadir paneles al contenedor principal
        panelContenido.add(panelMesas, "mesas");
        panelContenido.add(panelProductos, "productos");
        panelContenido.add(panelTomarOrden, "tomarOrden");

        // Añadir componentes al panel principal
        panelPrincipal.add(panelMenu, BorderLayout.WEST);
        panelPrincipal.add(panelContenido, BorderLayout.CENTER);
    }

    private JButton crearBotonMenu(String texto) {
        JButton boton = new JButton(texto);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(180, 40));
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        boton.setForeground(Color.WHITE);
        boton.setBackground(COLOR_AZUL);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(COLOR_TERRACOTA);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(COLOR_AZUL);
            }
        });

        return boton;
    }

    private JLabel crearLabelMenu(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(COLOR_CREMA);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void establecerEventos() {
        btnMesas.addActionListener(e -> mostrarPanel("mesas"));
        btnProductos.addActionListener(e -> mostrarPanel("productos"));
        btnTomarOrden.addActionListener(e -> mostrarPanel("tomarOrden"));
        btnEnviarPedido.addActionListener(e -> enviarPedido());
    }

    private void mostrarPanel(String nombrePanel) {
        CardLayout cl = (CardLayout) panelContenido.getLayout();
        cl.show(panelContenido, nombrePanel);
    }

    private void enviarPedido() {
        if (panelTomarOrden.validarPedido()) {
            JOptionPane.showMessageDialog(this,
                    "Pedido enviado correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            panelTomarOrden.limpiarPedido();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No hay un pedido válido para enviar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // Metodo principal para probar la interfaz
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Aplicar Look and Feel similar a Material Design
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Personalizar componentes globalmente
                UIManager.put("Button.background", COLOR_CREMA);
                UIManager.put("Button.foreground", COLOR_AZUL);
                UIManager.put("Panel.background", COLOR_CREMA);
                UIManager.put("Label.foreground", COLOR_AZUL);
                UIManager.put("TextField.caretForeground", COLOR_AZUL);

            } catch (Exception e) {
                e.printStackTrace();
            }
            RolDAO rolDAO = new RolDAO();
            EmpleadoDAO empleadoDAO = new EmpleadoDAO(rolDAO);
            Empleado empleado = null;
            try {
                empleado = empleadoDAO.buscarPorId(2);
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }

            VistaMesero vista = new VistaMesero(empleado);
            vista.setVisible(true);
        });
    }

}


