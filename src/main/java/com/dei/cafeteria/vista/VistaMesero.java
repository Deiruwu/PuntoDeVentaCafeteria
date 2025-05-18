package com.dei.cafeteria.vista;

import javax.swing.*;
import java.awt.*;

import com.dei.cafeteria.controlador.ControladorMesero;
import com.dei.cafeteria.dao.*;
import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.util.ColorPaleta;
import com.dei.cafeteria.vista.componentes.PanelInfoEmpleado;
import com.dei.cafeteria.vista.util.ComponentFactory;
import lombok.Getter;

/**
 * Clase principal que integra todos los componentes del módulo Mesero
 */
public class VistaMesero extends JFrame {

    private Empleado meseroActual;
    private ControladorMesero controlador;

    // Paneles principales
    private JPanel panelPrincipal;
    private JPanel panelContenido;
    private JPanel panelMenu;

    // Panel para la información del empleado
    private PanelInfoEmpleado panelInfoEmpleado;

    // Componentes del menú
    private JButton btnMesas;
    private JButton btnProductos;
    private JButton btnTomarOrden;
    private JButton btnEnviarPedido;
    private JButton btnGestionOrdenes;

    // Getters para los paneles (para el controlador)
    // Paneles de contenido
    @Getter
    private PanelMesas panelMesas;
    @Getter
    private PanelProductos panelProductos;
    @Getter
    private PanelTomarOrden panelTomarOrden;
    @Getter
    private PanelGestionOrdenes panelGestionOrdenes;

    public VistaMesero(ControladorMesero controlador, Empleado meseroActual) {
        this.controlador = controlador;
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
        panelPrincipal.setBackground(ColorPaleta.CREMA.getColor());
        setContentPane(panelPrincipal);
    }

    private void inicializarComponentes() {
        // Crear panel de menú lateral
        inicializarPanelMenu();

        // Crear panel de contenido principal
        inicializarPanelContenido();

        // Añadir componentes al panel principal
        panelPrincipal.add(panelMenu, BorderLayout.WEST);
        panelPrincipal.add(panelContenido, BorderLayout.CENTER);
    }

    private void inicializarPanelMenu() {
        panelMenu = new JPanel();
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setBackground(ColorPaleta.AZUL.getColor());
        panelMenu.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panelMenu.setPreferredSize(new Dimension(200, getHeight()));

        // Crear panel info empleado
        panelInfoEmpleado = new PanelInfoEmpleado(meseroActual);

        // Crear botones del menú
        btnMesas = ComponentFactory.crearBotonMenu("Mesas");
        btnProductos = ComponentFactory.crearBotonMenu("Productos");
        btnGestionOrdenes = ComponentFactory.crearBotonMenu("Ordenes");
        btnTomarOrden = ComponentFactory.crearBotonMenu("Tomar Orden");
        btnEnviarPedido = ComponentFactory.crearBotonMenu("Enviar Pedido");

        // Añadir componentes al menú
        panelMenu.add(Box.createVerticalStrut(20));
        panelMenu.add(ComponentFactory.crearLabelMenu("MESERO"));
        panelMenu.add(Box.createVerticalStrut(20));
        panelMenu.add(panelInfoEmpleado);
        panelMenu.add(Box.createVerticalStrut(40));
        panelMenu.add(btnMesas);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(btnProductos);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(btnGestionOrdenes);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(btnTomarOrden);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(btnEnviarPedido);
        panelMenu.add(Box.createVerticalGlue());
    }

    private void inicializarPanelContenido() {
        panelContenido = new JPanel(new CardLayout());
        panelContenido.setBackground(ColorPaleta.CREMA.getColor());

        // Inicializar los paneles específicos
        panelMesas = new PanelMesas();
        panelProductos = new PanelProductos();
        panelTomarOrden = new PanelTomarOrden(meseroActual);
        panelGestionOrdenes = new PanelGestionOrdenes(meseroActual);

        // Añadir paneles al contenedor principal
        panelContenido.add(panelMesas, "mesas");
        panelContenido.add(panelProductos, "productos");
        panelContenido.add(panelGestionOrdenes, "gestionOrdenes");
        panelContenido.add(panelTomarOrden, "tomarOrden");
    }

    private void establecerEventos() {
        btnMesas.addActionListener(e -> mostrarPanel("mesas"));
        btnProductos.addActionListener(e -> mostrarPanel("productos"));
        btnTomarOrden.addActionListener(e -> mostrarPanel("tomarOrden"));
        btnGestionOrdenes.addActionListener(e -> mostrarPanel("gestionOrdenes"));
        btnEnviarPedido.addActionListener(e -> controlador.enviarPedido());
    }

    public void mostrarPanel(String nombrePanel) {
        CardLayout cl = (CardLayout) panelContenido.getLayout();
        cl.show(panelContenido, nombrePanel);
    }

    // Punto de entrada principal modificado para usar el controlador
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Aplicar Look and Feel similar a Material Design
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Personalizar componentes globalmente
                UIManager.put("Button.background", ColorPaleta.CREMA.getColor());
                UIManager.put("Button.foreground", ColorPaleta.AZUL.getColor());
                UIManager.put("Panel.background", ColorPaleta.CREMA.getColor());
                UIManager.put("Label.foreground", ColorPaleta.AZUL.getColor());
                UIManager.put("TextField.caretForeground", ColorPaleta.AZUL.getColor());

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                RolDAO rolDAO = new RolDAO();
                EmpleadoDAO empleadoDAO = new EmpleadoDAO(rolDAO);
                Empleado empleado = empleadoDAO.buscarPorId(2);

                ControladorMesero controlador = new ControladorMesero(empleado);
                controlador.mostrarVista();
            } catch (DAOException e) {
                JOptionPane.showMessageDialog(null,
                        "Error al cargar datos del empleado: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}