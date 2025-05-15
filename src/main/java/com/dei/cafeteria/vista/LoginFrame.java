package com.dei.cafeteria.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

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

    // Ruta absoluta para el logo
    private static final String RUTA_LOGO = "imagenes/logo.png";

    // Íconos Nerd Font para campos de usuario y contraseña
    private static final String ICONO_USUARIO = "\uE61B"; // Ícono de café/usuario
    private static final String ICONO_PASSWORD = "\uDB80\uDF3E"; // Ícono de candado

    // Componentes de la interfaz
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JButton btnSalir;
    private JPanel panelFondo;
    private JLabel lblImagenLogo;
    private JLabel lblIconoUsuario;
    private JLabel lblIconoPassword;

    // Servicio de autentificación
    private ServicioDeAutentificacion servicioAutentificacion;

    /**
     * Constructor de la ventana de login
     */
    public LoginFrame() {
        setTitle("Inicio de Sesión");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Inicializar el servicio de autentificación
        inicializarServicioAutentificacion();

        // Cargar fuente Nerd Font
        cargarFuenteNerdFont();

        // Configurar el fondo
        configurarPanelFondo();

        // Inicializar componentes de la interfaz
        initComponents();

        setVisible(true);
    }

    /**
     * Carga la fuente JetBrainsMono Nerd Font
     */
    private Font fuenteNerd;

    private void cargarFuenteNerdFont() {
        try {
            // Intenta cargar la fuente JetBrainsMono Nerd Font Mono
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontNames = ge.getAvailableFontFamilyNames();

            String fontName = null;
            // Buscar posibles nombres de la fuente JetBrainsMono Nerd Font
            for (String name : fontNames) {
                if (name.contains("JetBrainsMono") && (name.contains("Nerd") || name.contains("NFM"))) {
                    fontName = name;
                    break;
                }
            }

            // Si se encontró la fuente, usarla
            if (fontName != null) {
                fuenteNerd = new Font(fontName, Font.PLAIN, 18);
            } else {
                // Si no se encuentra, usar una fuente monoespaciada por defecto
                fuenteNerd = new Font(Font.MONOSPACED, Font.PLAIN, 18);
                System.out.println("Advertencia: No se encontró JetBrainsMono Nerd Font. Usando fuente alternativa.");
            }
        } catch (Exception e) {
            fuenteNerd = new Font(Font.MONOSPACED, Font.PLAIN, 18);
            System.out.println("Error al cargar la fuente: " + e.getMessage());
        }
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

        // Cargar y mostrar el logo desde la ruta absoluta
        try {
            BufferedImage imgLogo = ImageIO.read(new File(RUTA_LOGO));
            Image scaledImage = imgLogo.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            lblImagenLogo = new JLabel(new ImageIcon(scaledImage));
        } catch (Exception e) {
            // Si no se puede cargar la imagen, mostrar un marcador de posición
            lblImagenLogo = new JLabel("Logo");
            lblImagenLogo.setForeground(COLOR_TEXTO);
            lblImagenLogo.setHorizontalAlignment(SwingConstants.CENTER);
            lblImagenLogo.setVerticalAlignment(SwingConstants.CENTER);
            lblImagenLogo.setBorder(BorderFactory.createLineBorder(COLOR_DETALLE, 1));
            lblImagenLogo.setBackground(COLOR_BLANCO);
            lblImagenLogo.setOpaque(true);
            lblImagenLogo.setSize(250, 250);
            System.out.println("Error al cargar el logo: " + e.getMessage());
        }

        lblImagenLogo.setBounds(100, 0, 200, 200);

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

        // Panel para el campo de usuario con icono
        JPanel panelUsuario = new JPanel(new BorderLayout());
        panelUsuario.setBounds(75, 250, 250, 40);
        panelUsuario.setOpaque(false);

        // Icono de usuario con Nerd Font
        lblIconoUsuario = new JLabel(ICONO_USUARIO);
        lblIconoUsuario.setFont(new Font("fuenteNerd", 0, 20));
        lblIconoUsuario.setForeground(COLOR_DETALLE);
        lblIconoUsuario.setHorizontalAlignment(SwingConstants.CENTER);
        lblIconoUsuario.setPreferredSize(new Dimension(40, 40));

        // Campo de texto de usuario
        txtUsuario = createPlaceholderTextField("Usuario");

        // Agregar componentes al panel de usuario
        panelUsuario.add(lblIconoUsuario, BorderLayout.WEST);
        panelUsuario.add(txtUsuario, BorderLayout.CENTER);

        // Panel para el campo de contraseña con icono
        JPanel panelPassword = new JPanel(new BorderLayout());
        panelPassword.setBounds(75, 310, 250, 40);
        panelPassword.setOpaque(false);

        // Icono de candado con Nerd Font
        lblIconoPassword = new JLabel(ICONO_PASSWORD);
        lblIconoPassword.setFont(new Font("fuenteNerd", 0, 20));
        lblIconoPassword.setForeground(COLOR_DETALLE);
        lblIconoPassword.setHorizontalAlignment(SwingConstants.CENTER);
        lblIconoPassword.setPreferredSize(new Dimension(40, 40));

        // Campo de texto de contraseña
        txtPassword = createPlaceholderPasswordField("Contraseña");

        // Agregar componentes al panel de contraseña
        panelPassword.add(lblIconoPassword, BorderLayout.WEST);
        panelPassword.add(txtPassword, BorderLayout.CENTER);

        // Botones con estilo ámbar luminoso
        btnIngresar = createStyledButton("INGRESAR", COLOR_BOTON);
        btnIngresar.setBounds(75, 370, 160, 45);

        btnSalir = createStyledButton("SALIR", COLOR_BOTON);
        btnSalir.setBounds(245, 370, 80, 45);

        // Agregar componentes al panel
        panelLogin.add(lblImagenLogo);
        panelLogin.add(lblEslogan);
        panelLogin.add(separator);
        panelLogin.add(panelUsuario);
        panelLogin.add(panelPassword);
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

                // Cambia el color del icono cuando el campo tiene foco
                if (textField == txtUsuario) {
                    lblIconoUsuario.setForeground(COLOR_BOTON);
                }
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

                // Restaura el color del icono cuando el campo pierde el foco
                if (textField == txtUsuario) {
                    lblIconoUsuario.setForeground(COLOR_DETALLE);
                }
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

                // Cambia el color del icono cuando el campo tiene foco
                lblIconoPassword.setForeground(COLOR_BOTON);
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

                // Restaura el color del icono cuando el campo pierde el foco
                lblIconoPassword.setForeground(COLOR_DETALLE);
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

        // También permitir login con Enter desde el campo de usuario
        txtUsuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocus();
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
                mostrarMensaje("¡Hola, " + usuarioAutenticado.getEmpleado().getNombre() + "!",
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