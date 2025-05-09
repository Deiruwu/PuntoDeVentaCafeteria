package com.dei.cafeteria.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

import com.dei.cafeteria.dao.UsuarioDAO;
import com.dei.cafeteria.dao.RolDAO;
import com.dei.cafeteria.dao.EmpleadoDAO;
import com.dei.cafeteria.dao.EstadoUsuarioDAO;
import com.dei.cafeteria.modelo.Usuario;
import com.dei.cafeteria.servicios.ServicioDeAutentificacion;
import com.dei.cafeteria.servicios.ServicioException;

/**
 * Frame para el inicio de sesión del sistema de Punto de Venta para Cafetería
 * Diseño minimalista y moderno
 * @author DeyCafeteria
 */
public class LoginFrame extends JFrame {

    // Colores del tema
    private static final Color COLOR_FONDO = new Color(230, 236, 250);        // Lavanda pastel claro
    private static final Color COLOR_PANEL = new Color(255, 255, 255, 240);   // Blanco semi-transparente
    private static final Color COLOR_TEXTO = new Color(50, 50, 70);           // Gris oscuro azulado
    private static final Color COLOR_BOTON_LOGIN = new Color(130, 158, 246);  // Azul pastel para botón login
    private static final Color COLOR_BOTON_SALIR = new Color(226, 138, 138);  // Rojo pastel para botón salir
    private static final Color COLOR_BORDE = new Color(200, 210, 230);        // Gris azulado claro para bordes

    // Componentes de la interfaz
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JButton btnSalir;
    private JPanel panelFondo;

    // Servicio de autentificación
    private ServicioDeAutentificacion servicioAutentificacion;

    /**
     * Constructor de la ventana de login
     */
    public LoginFrame() {
        setTitle("Cafetería - Inicio de Sesión");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Configurar icono de la aplicación
        // TODO: Agregar icono real de la cafetería
        // setIconImage(new ImageIcon(getClass().getResource("/recursos/iconos/coffee-icon.png")).getImage());

        // Inicializar el servicio de autentificación
        inicializarServicioAutentificacion();

        // Configurar el fondo
        configurarPanelFondo();

        // Inicializar componentes de la interfaz
        initComponents();

        setVisible(true);
    }

    /**
     * Inicializa el servicio de autentificación con conexión a la base de datos
     */
    private void inicializarServicioAutentificacion() {
        try {
            // Crear los DAOs necesarios
            RolDAO rolDAO = new RolDAO();
            EmpleadoDAO empleadoDAO = new EmpleadoDAO(rolDAO);
            EstadoUsuarioDAO estadoUsuarioDAO = new EstadoUsuarioDAO();

            // Crear el DAO de usuario con sus dependencias
            UsuarioDAO usuarioDAO = new UsuarioDAO(empleadoDAO, estadoUsuarioDAO);

            // Crear el servicio de autentificación
            servicioAutentificacion = new ServicioDeAutentificacion(usuarioDAO);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al conectar con la base de datos: " + e.getMessage(),
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * Configura el panel de fondo con un color suave degradado
     */
    private void configurarPanelFondo() {
        panelFondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Suaviza los bordes
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Crea un degradado suave de arriba a abajo
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(220, 226, 250),  // Color superior (lavanda más claro)
                        0, getHeight(), COLOR_FONDO      // Color inferior (lavanda base)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Agrega un patrón sutil de círculos semitransparentes
                g2d.setColor(new Color(255, 255, 255, 30)); // Casi transparente
                for (int i = 0; i < 8; i++) {
                    int size = (int)(Math.random() * 100) + 50;
                    int x = (int)(Math.random() * getWidth());
                    int y = (int)(Math.random() * getHeight());
                    g2d.fillOval(x, y, size, size);
                }
            }
        };
        panelFondo.setLayout(null);
        setContentPane(panelFondo);
    }

    /**
     * Inicialización de componentes de la interfaz con estilo minimalista
     */
    private void initComponents() {
        // Panel principal semi-transparente
        JPanel panelLogin = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo del panel con bordes redondeados
                g2d.setColor(COLOR_PANEL);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Borde sutil
                g2d.setColor(COLOR_BORDE);
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };

        panelLogin.setLayout(null);
        panelLogin.setBounds(250, 80, 300, 340);
        panelLogin.setOpaque(false);

        // Título Bienvenido
        JLabel lblBienvenido = new JLabel("Bienvenido");
        lblBienvenido.setBounds(0, 30, 300, 40);
        lblBienvenido.setForeground(COLOR_TEXTO);
        lblBienvenido.setFont(new Font("Montserrat", Font.BOLD, 28));
        lblBienvenido.setHorizontalAlignment(SwingConstants.CENTER);

        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Sistema de Cafetería");
        lblSubtitulo.setBounds(0, 70, 300, 25);
        lblSubtitulo.setForeground(new Color(120, 120, 140));
        lblSubtitulo.setFont(new Font("Montserrat", Font.PLAIN, 16));
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);

        // Etiquetas y campos de texto
        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setBounds(40, 120, 220, 20);
        lblUsuario.setForeground(COLOR_TEXTO);
        lblUsuario.setFont(new Font("Montserrat", Font.BOLD, 14));

        txtUsuario = createStyledTextField();
        txtUsuario.setBounds(40, 145, 220, 35);

        JLabel lblPassword = new JLabel("Contraseña");
        lblPassword.setBounds(40, 195, 220, 20);
        lblPassword.setForeground(COLOR_TEXTO);
        lblPassword.setFont(new Font("Montserrat", Font.BOLD, 14));

        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        txtPassword.setBounds(40, 220, 220, 35);

        // Botones
        btnIngresar = createStyledButton("Ingresar", COLOR_BOTON_LOGIN);
        btnIngresar.setBounds(40, 280, 140, 40);

        btnSalir = createStyledButton("Salir", COLOR_BOTON_SALIR);
        btnSalir.setBounds(190, 280, 70, 40);

        // Agregar componentes al panel
        panelLogin.add(lblBienvenido);
        panelLogin.add(lblSubtitulo);
        panelLogin.add(lblUsuario);
        panelLogin.add(txtUsuario);
        panelLogin.add(lblPassword);
        panelLogin.add(txtPassword);
        panelLogin.add(btnIngresar);
        panelLogin.add(btnSalir);

        // Agregar panel al frame
        panelFondo.add(panelLogin);

        // Configurar eventos
        configurarEventos();
    }

    /**
     * Crea un campo de texto con estilo moderno
     */
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        styleTextField(textField);
        return textField;
    }

    /**
     * Aplica estilo minimalista a un campo de texto
     */
    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Montserrat", Font.PLAIN, 14));
        textField.setForeground(COLOR_TEXTO);
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        // Efecto de hover y focus
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_BOTON_LOGIN, 1, true),
                        new EmptyBorder(5, 10, 5, 10)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
                        new EmptyBorder(5, 10, 5, 10)
                ));
            }
        });
    }

    /**
     * Crea un botón con estilo moderno
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Montserrat", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 1, true));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efectos de hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Configura los eventos para los componentes
     */
    private void configurarEventos() {
        // Configurar acción para el botón Ingresar
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validarCredenciales();
            }
        });

        // Configurar acción para el botón Salir
        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Para permitir login con Enter
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    validarCredenciales();
                }
            }
        });
    }

    /**
     * Valida las credenciales ingresadas usando el ServicioDeAutentificacion
     */
    private void validarCredenciales() {
        String usuario = txtUsuario.getText();
        String password = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarMensaje("Por favor complete todos los campos", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Autenticar usuario usando el servicio
            Usuario usuarioAutenticado = servicioAutentificacion.autenticar(usuario, password);

            if (usuarioAutenticado != null) {
                mostrarMensaje("¡Bienvenido, " + usuarioAutenticado.getEmpleado().getNombre() + "!",
                        "Acceso Correcto", JOptionPane.INFORMATION_MESSAGE);

                // Abrir la ventana principal según el tipo de usuario
                abrirSistemaPrincipal(usuarioAutenticado);

                // Cerrar ventana de login
                this.dispose();
            } else {
                mostrarMensaje("Usuario o contraseña incorrectos", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtPassword.requestFocus();
            }
        } catch (ServicioException e) {
            mostrarMensaje(e.getMessage(), "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    /**
     * Muestra un mensaje con estilo personalizado
     */
    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        UIManager.put("OptionPane.background", COLOR_PANEL);
        UIManager.put("Panel.background", COLOR_PANEL);
        UIManager.put("OptionPane.messageForeground", COLOR_TEXTO);
        UIManager.put("Button.background", COLOR_BOTON_LOGIN);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Montserrat", Font.BOLD, 12));

        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }

    /**
     * Abre la ventana principal según el tipo de usuario
     *
     * @param usuario Usuario autenticado
     */
    private void abrirSistemaPrincipal(Usuario usuario) {
        // Verificar el rol del usuario y abrir la ventana correspondiente
        try {
            // Obtener el rol a través del empleado
            String rolNombre = usuario.getEmpleado().getRol().getNombre().toLowerCase();

            switch (rolNombre) {
                case "administrador":
                    mostrarMensaje("Abriendo panel de administrador", "Información", JOptionPane.INFORMATION_MESSAGE);
                    // TODO: Crear e inicializar la vista de administrador
                    // new AdminView(usuario);
                    break;
                case "cajero":
                    mostrarMensaje("Abriendo panel de cajero", "Información", JOptionPane.INFORMATION_MESSAGE);
                    // TODO: Crear e inicializar la vista de cajero
                    // new CajeroView(usuario);
                    break;
                case "gerente":
                    mostrarMensaje("Abriendo panel de gerente", "Información", JOptionPane.INFORMATION_MESSAGE);
                    // TODO: Crear e inicializar la vista de gerente
                    // new GerenteView(usuario);
                    break;
                default:
                    mostrarMensaje("Abriendo panel estándar", "Información", JOptionPane.INFORMATION_MESSAGE);
                    // TODO: Crear e inicializar la vista estándar
                    // new EmpleadoView(usuario);
                    break;
            }
        } catch (Exception e) {
            mostrarMensaje("Error al abrir el sistema principal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método principal para pruebas
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        try {
            // Establecer Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Mejoras adicionales al UI general
            UIManager.put("TextField.caretForeground", COLOR_TEXTO);
            UIManager.put("PasswordField.caretForeground", COLOR_TEXTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame();
            }
        });
    }
}