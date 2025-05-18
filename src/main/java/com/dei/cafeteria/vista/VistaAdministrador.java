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
 * Clase principal que integra todos los componentes del módulo Administrador
 */
public class VistaAdministrador extends JFrame {

    private Empleado administradorActual;

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
    private JButton btnUsuarios;
    private JButton btnProductos;
    private JButton btnOrdenes;
    private JButton btnConfiguracion;

    // Paneles de contenido
    private PanelGestionUsuarios panelGestionUsuarios;
    private PanelGestionProductos panelGestionProductos;
    private PanelGestionOrdenes panelGestionOrdenes;
    //private PanelConfiguracion panelConfiguracion;

    // Panel actual mostrado
    private JPanel panelActual;

    public VistaAdministrador(Empleado administradorActual) {
        this.administradorActual = administradorActual;
        configurarVentana();
        inicializarComponentes();
        establecerEventos();
        mostrarPanel("usuarios"); // Iniciar con panel de gestión de usuarios
    }

    private void configurarVentana() {
        setTitle("Cafetería - Módulo Administrador");
        setSize(1200, 800);
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
        panelMenu.setPreferredSize(new Dimension(220, getHeight()));

        // Agregar panel para la información del empleado
        crearPanelInfoEmpleado();

        // Crear botones del menú con estilo personalizado
        btnUsuarios = crearBotonMenu("Gestión de Usuarios");
        btnProductos = crearBotonMenu("Gestión de Productos");
        btnOrdenes = crearBotonMenu("Gestión de Órdenes");
        btnConfiguracion = crearBotonMenu("Configuración");

        // Añadir botones al menú
        panelMenu.add(Box.createVerticalStrut(20));
        panelMenu.add(crearLabelMenu("ADMINISTRADOR"));
        panelMenu.add(Box.createVerticalStrut(20));
        panelMenu.add(panelInfoEmpleado);
        panelMenu.add(Box.createVerticalStrut(40));
        panelMenu.add(btnUsuarios);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(btnProductos);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(btnOrdenes);
        panelMenu.add(Box.createVerticalGlue());

        // Crear panel de contenido principal (donde se cargarán las diferentes vistas)
        panelContenido = new JPanel(new CardLayout());
        panelContenido.setBackground(COLOR_CREMA);

        // Inicializar los paneles específicos
        panelGestionUsuarios = new PanelGestionUsuarios(administradorActual);
        panelGestionProductos = new PanelGestionProductos();
        panelGestionOrdenes = new PanelGestionOrdenes(administradorActual);
        //panelConfiguracion = new PanelConfiguracion(administradorActual);

        // Añadir paneles al contenedor principal
        panelContenido.add(panelGestionUsuarios, "usuarios");
        panelContenido.add(panelGestionProductos, "productos");
        panelContenido.add(panelGestionOrdenes, "ordenes");
        //panelContenido.add(panelConfiguracion, "configuracion");

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
        panelInfoEmpleado.setMaximumSize(new Dimension(200, 220));

        // Cargar la foto del empleado
        String rutaImagen = administradorActual.getImagenUrl();
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
            Image imgRedimensionada = img.getScaledInstance(130, 130, Image.SCALE_SMOOTH);
            imagenEmpleado = new ImageIcon(imgRedimensionada);
        }

        // Crear componentes de información del empleado
        lblFotoEmpleado = new JLabel();
        lblFotoEmpleado.setIcon(imagenEmpleado);
        lblFotoEmpleado.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Crear círculo para la foto
        lblFotoEmpleado.setBorder(BorderFactory.createLineBorder(COLOR_CREMA, 2));

        // Obtener nombre completo del empleado
        String nombreEmpleado = administradorActual.getNombre() + " " + administradorActual.getApellido();
        if (nombreEmpleado == null || nombreEmpleado.trim().isEmpty()) {
            nombreEmpleado = "Administrador";
        }

        lblNombreEmpleado = new JLabel(nombreEmpleado);
        lblNombreEmpleado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombreEmpleado.setForeground(COLOR_CREMA);
        lblNombreEmpleado.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Obtener ID del empleado
        int idEmpleado = administradorActual.getId();
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
        boton.setMaximumSize(new Dimension(200, 40));
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
                boton.setBackground(COLOR_VERDE);
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
        btnUsuarios.addActionListener(e -> mostrarPanel("usuarios"));
        btnProductos.addActionListener(e -> mostrarPanel("productos"));
        btnOrdenes.addActionListener(e -> mostrarPanel("ordenes"));
        btnConfiguracion.addActionListener(e -> mostrarPanel("configuracion"));
    }

    private void mostrarPanel(String nombrePanel) {
        CardLayout cl = (CardLayout) panelContenido.getLayout();
        cl.show(panelContenido, nombrePanel);

        // Recargar datos según el panel mostrado
        if (nombrePanel.equals("usuarios")) {
            panelGestionUsuarios.cargarDatos();
        } else if (nombrePanel.equals("productos")) {
            panelGestionProductos.cargarProductos();
        } else if (nombrePanel.equals("ordenes")) {
            //panelGestionOrdenes.cargarDatos();
        } else if (nombrePanel.equals("configuracion")) {
            //panelConfiguracion.cargarDatos();
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
                empleado = empleadoDAO.buscarPorId(1);
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }

            VistaAdministrador vista = new VistaAdministrador(empleado);
            vista.setVisible(true);
        });
    }
}