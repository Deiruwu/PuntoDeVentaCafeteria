package com.dei.cafeteria.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
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
 * Diseño contemporáneo inspirado en "Tierra & Humo"
 * @author DeyCafeteria
 */
public class LoginFrame extends JFrame {

    // Colores del tema "Tierra & Humo"
    private static final Color COLOR_FONDO = new Color(140, 94, 88);          // Terracota Suave #8C5E58
    private static final Color COLOR_PANEL = new Color(232, 218, 203);        // Crema Antiguo #E8DACB
    private static final Color COLOR_TEXTO = new Color(44, 59, 71);           // Azul Medianoche #2C3B47
    private static final Color COLOR_BOTON = new Color(212, 146, 93);         // Ámbar Luminoso #D4925D
    private static final Color COLOR_DETALLE = new Color(97, 112, 91);        // Verde Salvia #61705B
    private static final Color COLOR_PLACEHOLDER = new Color(120, 120, 120);  // Gris para placeholder
    private static final Color COLOR_BLANCO = new Color(255, 255, 255);       // Blanco puro

    // Componentes de la interfaz
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JButton btnSalir;
    private JPanel panelFondo;
    private JLabel lblImagenLogo;

    // Servicio de autentificación
    private ServicioDeAutentificacion servicioAutentificacion;

    /**
     * Constructor de la ventana de login
     */
    public LoginFrame() {
        setTitle("Tierra & Humo - Inicio de Sesión");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

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
            // Inicializar las dependencias necesarias
            RolDAO rolDAO = new RolDAO();
            EstadoUsuarioDAO estadoUsuarioDAO = new EstadoUsuarioDAO();
            EmpleadoDAO empleadoDAO = new EmpleadoDAO(rolDAO);

            // Ahora podemos crear UsuarioDAO con sus dependencias
            UsuarioDAO usuarioDAO = new UsuarioDAO(empleadoDAO, estadoUsuarioDAO);

            servicioAutentificacion = new ServicioDeAutentificacion(usuarioDAO);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al inicializar el servicio de autenticación: " + e.getMessage(),
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * Configura el panel de fondo con el color terracota suave
     */
    private void configurarPanelFondo() {
        panelFondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Suavizado para mejor calidad visual
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Color terracota suave de fondo
                g2d.setColor(COLOR_FONDO);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelFondo.setLayout(null);
        setContentPane(panelFondo);
    }

    /**
     * Inicialización de componentes de la interfaz con estilo "Tierra & Humo"
     */
    private void initComponents() {
        // Panel principal con esquinas redondeadas y color crema antiguo
        JPanel panelLogin = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo del panel con bordes redondeados en color crema antiguo
                g2d.setColor(COLOR_PANEL);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Borde sutil en verde salvia
                g2d.setColor(COLOR_DETALLE);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 30, 30);
            }
        };

        panelLogin.setLayout(null);
        panelLogin.setBounds(250, 80, 400, 440);
        panelLogin.setOpaque(false);

        // Panel para la imagen del logo (cuadro blanco simple)
        lblImagenLogo = new JLabel("Insertar Imagen");
        lblImagenLogo.setForeground(COLOR_TEXTO);
        lblImagenLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagenLogo.setVerticalAlignment(SwingConstants.CENTER);
        lblImagenLogo.setBounds(150, 30, 100, 100);
        lblImagenLogo.setBorder(BorderFactory.createLineBorder(COLOR_DETALLE, 1));
        lblImagenLogo.setBackground(COLOR_BLANCO);
        lblImagenLogo.setOpaque(true);

        // Título "Tierra & Humo"
        JLabel lblTitulo = new JLabel("Tierra & Humo");
        lblTitulo.setBounds(0, 150, 400, 40);
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 32));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        // Eslogan "Respira lento, bebe hondo."
        JLabel lblEslogan = new JLabel("Respira lento, bebe hondo.");
        lblEslogan.setBounds(0, 190, 400, 25);
        lblEslogan.setForeground(COLOR_TEXTO);
        lblEslogan.setFont(new Font("Roboto Mono", Font.ITALIC, 16));
        lblEslogan.setHorizontalAlignment(SwingConstants.CENTER);

        // Separador horizontal sutil
        JSeparator separator = new JSeparator();
        separator.setBounds(100, 230, 200, 2);
        separator.setForeground(COLOR_DETALLE);
        separator.setBackground(COLOR_DETALLE);

        // Campos de texto con estilo minimalista y placeholder
        txtUsuario = createPlaceholderTextField("Usuario");
        txtUsuario.setBounds(75, 250, 250, 40);

        txtPassword = createPlaceholderPasswordField("Contraseña");
        txtPassword.setBounds(75, 310, 250, 40);

        // Botones con estilo ámbar luminoso
        btnIngresar = createStyledButton("INGRESAR", COLOR_BOTON);
        btnIngresar.setBounds(75, 370, 160, 45);

        btnSalir = createStyledButton("SALIR", COLOR_BOTON);
        btnSalir.setBounds(245, 370, 80, 45);

        // Agregar componentes al panel
        panelLogin.add(lblImagenLogo);
        panelLogin.add(lblTitulo);
        panelLogin.add(lblEslogan);
        panelLogin.add(separator);
        panelLogin.add(txtUsuario);
        panelLogin.add(txtPassword);
        panelLogin.add(btnIngresar);
        panelLogin.add(btnSalir);

        // Agregar panel al frame
        panelFondo.add(panelLogin);

        // Configurar eventos
        configurarEventos();
    }

    /**
     * Crea un campo de texto con placeholder y estilo contemporáneo
     */
    private JTextField createPlaceholderTextField(final String placeholder) {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Roboto Mono", Font.PLAIN, 14));
        textField.setForeground(COLOR_TEXTO);
        textField.setBackground(new Color(COLOR_PANEL.getRed(), COLOR_PANEL.getGreen(), COLOR_PANEL.getBlue(), 220));

        // Borde inferior en color detalle
        textField.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_DETALLE));

        // Padding interno
        textField.setBorder(BorderFactory.createCompoundBorder(
                textField.getBorder(),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // Implementación del placeholder
        textField.setText(placeholder);
        textField.setForeground(COLOR_PLACEHOLDER);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(COLOR_TEXTO);
                }
                // Cambia el color del borde inferior cuando tiene foco
                textField.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 2, 0, COLOR_BOTON),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(COLOR_PLACEHOLDER);
                }
                // Restaura el color del borde inferior cuando pierde el foco
                textField.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 2, 0, COLOR_DETALLE),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            }
        });

        return textField;
    }

    /**
     * Crea un campo de contraseña con placeholder y estilo contemporáneo
     */
    private JPasswordField createPlaceholderPasswordField(final String placeholder) {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Roboto Mono", Font.PLAIN, 14));
        passwordField.setForeground(COLOR_TEXTO);
        passwordField.setBackground(new Color(COLOR_PANEL.getRed(), COLOR_PANEL.getGreen(), COLOR_PANEL.getBlue(), 220));

        // Borde inferior en color detalle
        passwordField.setBorder(new MatteBorder(0, 0, 2, 0, COLOR_DETALLE));

        // Padding interno
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                passwordField.getBorder(),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // Placeholder para el campo de contraseña
        passwordField.setEchoChar((char) 0); // Desactiva los caracteres ocultos inicialmente
        passwordField.setText(placeholder);
        passwordField.setForeground(COLOR_PLACEHOLDER);

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•'); // Activa los caracteres ocultos
                    passwordField.setForeground(COLOR_TEXTO);
                }
                // Cambia el color del borde inferior cuando tiene foco
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 2, 0, COLOR_BOTON),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setEchoChar((char) 0); // Desactiva los caracteres ocultos
                    passwordField.setText(placeholder);
                    passwordField.setForeground(COLOR_PLACEHOLDER);
                }
                // Restaura el color del borde inferior cuando pierde el foco
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 2, 0, COLOR_DETALLE),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            }
        });

        return passwordField;
    }

    /**
     * Crea un botón con estilo "Tierra & Humo"
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(COLOR_TEXTO);
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(text, x, y);
            }
        };

        button.setFont(new Font("Roboto Mono", Font.BOLD, 14));
        button.setForeground(COLOR_TEXTO);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
     * Valida las credenciales ingresadas
     */
    private void validarCredenciales() {
        String usuario = txtUsuario.getText();
        String password = new String(txtPassword.getPassword());

        // Verificar si los campos tienen los textos de placeholder
        if (usuario.equals("Usuario")) {
            usuario = "";
        }

        if (password.equals("Contraseña")) {
            password = "";
        }

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarMensaje("Por favor complete todos los campos", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Implementación simplificada para autenticación
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
                txtPassword.setText("Contraseña");
                txtPassword.setEchoChar((char) 0);
                txtPassword.setForeground(COLOR_PLACEHOLDER);
                txtPassword.requestFocus();
            }
        } catch (ServicioException e) {
            mostrarMensaje(e.getMessage(), "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("Contraseña");
            txtPassword.setEchoChar((char) 0);
            txtPassword.setForeground(COLOR_PLACEHOLDER);
            txtPassword.requestFocus();
        }
    }

    /**
     * Muestra un mensaje con estilo personalizado "Tierra & Humo"
     */
    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        UIManager.put("OptionPane.background", COLOR_PANEL);
        UIManager.put("Panel.background", COLOR_PANEL);
        UIManager.put("OptionPane.messageForeground", COLOR_TEXTO);
        UIManager.put("Button.background", COLOR_BOTON);
        UIManager.put("Button.foreground", COLOR_TEXTO);
        UIManager.put("Button.font", new Font("Roboto Mono", Font.BOLD, 12));

        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }

    /**
     * Abre la ventana principal según el tipo de usuario
     *
     * @param usuario Usuario autenticado
     */
    private void abrirSistemaPrincipal(Usuario usuario) {
        try {
            // Obtener el rol a través del empleado
            String rolNombre = usuario.getEmpleado().getRol().getNombre().toLowerCase();

            // Implementación simplificada para abrir la vista correspondiente
            mostrarMensaje("Abriendo panel para: " + rolNombre, "Información", JOptionPane.INFORMATION_MESSAGE);

            // TODO: Implementar apertura de vista según el rol
            // La implementación dependerá de la estructura de tu aplicación
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