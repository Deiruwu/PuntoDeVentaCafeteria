package com.dei.cafeteria.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.dao.EmpleadoDAO;
import com.dei.cafeteria.dao.RolDAO;
import com.dei.cafeteria.modelo.Empleado;

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
        crearPanelInfoEmpleado();

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

    private void crearPanelInfoEmpleado() {
        panelInfoEmpleado = new JPanel();
        panelInfoEmpleado.setLayout(new BoxLayout(panelInfoEmpleado, BoxLayout.Y_AXIS));
        panelInfoEmpleado.setBackground(COLOR_AZUL);
        panelInfoEmpleado.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelInfoEmpleado.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelInfoEmpleado.setMaximumSize(new Dimension(180, 200));

        // Cargar la foto del empleado
        String rutaImagen = cajeroActual.getImagenUrl();
        ImageIcon imagenEmpleado = null;

        if (rutaImagen != null && !rutaImagen.isEmpty()) {
            // Verificar si la ruta contiene "/ruta/imagenes" y transformarla a ruta absoluta
            if (rutaImagen.contains("/imagenes/")) {
                int indice = rutaImagen.indexOf("/imagenes/");
                rutaImagen = rutaImagen.substring(indice + 1); // +1 para quitar el primer "/"
            }

            // Intentar cargar la imagen
            try {
                File archivo = new File(rutaImagen);
                if (archivo.exists()) {
                    imagenEmpleado = new ImageIcon(rutaImagen);
                } else {
                    // Si no existe, usar una imagen por defecto
                    imagenEmpleado = new ImageIcon("imagenes/empleado_default.png");
                }
            } catch (Exception e) {
                System.err.println("Error al cargar la imagen del empleado: " + e.getMessage());
                imagenEmpleado = new ImageIcon("imagenes/empleado_default.png");
            }
        } else {
            imagenEmpleado = new ImageIcon("imagenes/empleado_default.png");
        }

        // Redimensionar imagen si es necesario
        if (imagenEmpleado != null) {
            Image img = imagenEmpleado.getImage();
            Image imgRedimensionada = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            imagenEmpleado = new ImageIcon(imgRedimensionada);
        }

        // Crear componentes de información del empleado
        lblFotoEmpleado = new JLabel();
        lblFotoEmpleado.setIcon(imagenEmpleado);
        lblFotoEmpleado.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Crear círculo para la foto
        lblFotoEmpleado.setBorder(BorderFactory.createLineBorder(COLOR_CREMA, 2));

        // Obtener nombre del empleado
        String nombreEmpleado = cajeroActual.getNombre();
        if (nombreEmpleado == null || nombreEmpleado.isEmpty()) {
            nombreEmpleado = "Empleado";
        }

        lblNombreEmpleado = new JLabel(nombreEmpleado);
        lblNombreEmpleado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombreEmpleado.setForeground(COLOR_CREMA);
        lblNombreEmpleado.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Obtener ID del empleado
        int idEmpleado = cajeroActual.getId();
        lblIdEmpleado = new JLabel("ID: " + idEmpleado);
        lblIdEmpleado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblIdEmpleado.setForeground(COLOR_CREMA);
        lblIdEmpleado.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Agregar componentes al panel
        panelInfoEmpleado.add(lblFotoEmpleado);
        panelInfoEmpleado.add(Box.createVerticalStrut(10));
        panelInfoEmpleado.add(lblNombreEmpleado);
        panelInfoEmpleado.add(Box.createVerticalStrut(5));
        panelInfoEmpleado.add(lblIdEmpleado);
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
                empleado = empleadoDAO.buscarPorId(1); // Asumiendo que el ID 1 corresponde a un cajero
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }

            VistaCajero vista = new VistaCajero(empleado);
            vista.setVisible(true);
        });
    }
}