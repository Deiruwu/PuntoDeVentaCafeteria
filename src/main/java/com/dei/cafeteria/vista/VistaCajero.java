package com.dei.cafeteria.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.dao.EmpleadoDAO;
import com.dei.cafeteria.dao.RolDAO;
import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.vista.componentes.PanelInfoEmpleado;

/**
 * Clase principal que integra todos los componentes del módulo Cajero
 */
public class VistaCajero extends JFrame {

    private Empleado cajeroActual;

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


    // Panel para la información del empleado
    private JPanel panelInfoEmpleado;
    private JLabel lblFotoEmpleado;
    private JLabel lblNombreEmpleado;
    private JLabel lblIdEmpleado;

    // Componentes del menú
    private JButton btnOrdenes;
    private JButton btnPagos;
    private JButton btnHistorial;
    private JButton btnReportes;

    // Paneles de contenido
    private PanelOrdenesPendientes panelOrdenesPendientes;
    private PanelProcesarPago panelProcesarPago;
    private PanelHistorialPagos panelHistorialPagos;
    //private PanelReportes panelReportes;

    // Panel actual mostrado
    private JPanel panelActual;

    public VistaCajero(Empleado cajeroActual) {
        this.cajeroActual = cajeroActual;
        configurarVentana();
        inicializarComponentes();
        establecerEventos();
        mostrarPanel("ordenes"); // Iniciar con panel de órdenes pendientes
    }

    private void configurarVentana() {
        setTitle("Cafetería - Módulo Cajero");
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

        // Agregar panel para la información del empleado
        panelInfoEmpleado = new PanelInfoEmpleado(cajeroActual);

        // Crear botones del menú con estilo personalizado
        btnOrdenes = crearBotonMenu("Órdenes Pendientes");
        btnPagos = crearBotonMenu("Procesar Pago");
        btnHistorial = crearBotonMenu("Historial de Pagos");
        btnReportes = crearBotonMenu("Reportes");

        // Añadir botones al menú
        panelMenu.add(Box.createVerticalStrut(20));
        panelMenu.add(crearLabelMenu("CAJERO"));
        panelMenu.add(Box.createVerticalStrut(20));
        panelMenu.add(panelInfoEmpleado);
        panelMenu.add(Box.createVerticalStrut(40));
        panelMenu.add(btnOrdenes);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(btnPagos);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(btnHistorial);
        //panelMenu.add(Box.createVerticalStrut(15));
        //panelMenu.add(btnReportes);
        panelMenu.add(Box.createVerticalGlue());

        // Crear panel de contenido principal (donde se cargarán las diferentes vistas)
        panelContenido = new JPanel(new CardLayout());
        panelContenido.setBackground(COLOR_CREMA);

        // Inicializar los paneles específicos
        panelOrdenesPendientes = new PanelOrdenesPendientes();
        panelProcesarPago = new PanelProcesarPago(cajeroActual);
        panelHistorialPagos = new PanelHistorialPagos(cajeroActual);
        //panelReportes = new PanelReportes();

        // Añadir paneles al contenedor principal
        panelContenido.add(panelOrdenesPendientes, "ordenes");
        panelContenido.add(panelProcesarPago, "pagos");
        panelContenido.add(panelHistorialPagos, "historial");
        //panelContenido.add(panelReportes, "reportes");

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
        btnOrdenes.addActionListener(e -> mostrarPanel("ordenes"));
        btnPagos.addActionListener(e -> mostrarPanel("pagos"));
        btnHistorial.addActionListener(e -> mostrarPanel("historial"));
        btnReportes.addActionListener(e -> mostrarPanel("reportes"));
    }

    private void mostrarPanel(String nombrePanel) {
        CardLayout cl = (CardLayout) panelContenido.getLayout();
        cl.show(panelContenido, nombrePanel);

        // Recargar datos según el panel mostrado
        if (nombrePanel.equals("ordenes")) {
            panelOrdenesPendientes.cargarOrdenesPendientes();
        } else if (nombrePanel.equals("pagos")) {
            panelProcesarPago.actualizarOrdenSeleccionada();
        } else if (nombrePanel.equals("historial")) {
            //panelHistorialPagos.cargarHistorialPagos();
        }
    }
}