package com.dei.cafeteria.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.dei.cafeteria.dao.*;
import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.modelo.EstadoUsuario;
import com.dei.cafeteria.modelo.Rol;
import com.dei.cafeteria.modelo.Usuario;
import com.dei.cafeteria.util.HashUtil;
/**
 * Panel para la gestión de usuarios y empleados en el módulo de administrador
 */
public class PanelGestionUsuarios extends JPanel {

    private Empleado administradorActual;
    private EmpleadoDAO empleadoDAO;
    private UsuarioDAO usuarioDAO;
    private RolDAO rolDAO;
    private EstadoUsuarioDAO estadoUsuarioDAO;

    // Colores de la aplicación
    private final Color COLOR_TERRACOTA = new Color(140, 94, 88);
    private final Color COLOR_AZUL = new Color(44, 59, 71);
    private final Color COLOR_CREMA = new Color(232, 218, 203);
    private final Color COLOR_VERDE = new Color(97, 112, 91);
    private final Color COLOR_AMBAR = new Color(212, 146, 93);

    // Paneles principales
    private JPanel panelFiltros;
    private JPanel panelTabla;
    private JPanel panelEdicion;
    private JPanel panelDetalleUsuario;
    private JPanel panelAcciones;

    // Componentes para filtros
    private JTextField txtBuscar;
    private JComboBox<String> cmbFiltroRol;
    private JComboBox<String> cmbFiltroEstado;
    private JButton btnBuscar;
    private JButton btnLimpiarFiltros;

    // Componentes para tabla
    private JTable tablaEmpleados;
    private DefaultTableModel modeloTabla;
    private JScrollPane scrollTabla;

    // Componentes para edición
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JComboBox<Rol> cmbRol;
    private JLabel lblImagenEmpleado;
    private JButton btnSeleccionarImagen;
    private String rutaImagenSeleccionada;

    // Componentes para usuario
    private JTextField txtNombreUsuario;
    private JPasswordField txtContraseña;
    private JPasswordField txtConfirmarContraseña;
    private JComboBox<String> cmbEstado;
    private JButton btnResetContraseña;

    // Componentes para acciones
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnEliminar;
    private JButton btnCancelar;

    // Estado
    private boolean modoEdicion = false;
    private boolean esNuevoEmpleado = false;
    private Empleado empleadoSeleccionado;
    private Usuario usuarioSeleccionado;

    public PanelGestionUsuarios(Empleado administradorActual) {
        this.administradorActual = administradorActual;
        this.empleadoDAO = new EmpleadoDAO(new RolDAO());
        this.estadoUsuarioDAO = new EstadoUsuarioDAO();
        this.usuarioDAO = new UsuarioDAO(empleadoDAO, estadoUsuarioDAO);
        this.rolDAO = new RolDAO();
        configurarPanel();
        inicializarComponentes();
        cargarDatos();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(COLOR_CREMA);
    }

    private void inicializarComponentes() {
        // Panel de filtros
        configurarPanelFiltros();

        // Panel de tabla
        configurarPanelTabla();

        // Panel de edición
        configurarPanelEdicion();

        // Panel de acciones
        configurarPanelAcciones();

        // Agregar paneles al panel principal
        add(panelFiltros, BorderLayout.NORTH);
        add(panelTabla, BorderLayout.CENTER);

        JPanel panelLateral = new JPanel(new BorderLayout());
        panelLateral.setBackground(COLOR_CREMA);
        panelLateral.add(panelEdicion, BorderLayout.CENTER);
        panelLateral.add(panelAcciones, BorderLayout.SOUTH);
        panelLateral.setPreferredSize(new Dimension(380, 0));

        add(panelLateral, BorderLayout.EAST);
    }

    private void configurarPanelFiltros() {
        panelFiltros = new JPanel();
        panelFiltros.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFiltros.setBackground(COLOR_CREMA);
        panelFiltros.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_TERRACOTA),
                "Filtros de búsqueda",
                1,
                0,
                new Font("Segoe UI", Font.BOLD, 12),
                COLOR_TERRACOTA));

        JLabel lblBuscar = new JLabel("Buscar:");
        txtBuscar = new JTextField(15);

        JLabel lblRol = new JLabel("Rol:");
        cmbFiltroRol = new JComboBox<>();
        cmbFiltroRol.addItem("Todos");
        try {
            List<Rol> roles = rolDAO.listarTodos();
            for (Rol rol : roles) {
                cmbFiltroRol.addItem(rol.getNombre());
            }
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar roles: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        JLabel lblEstado = new JLabel("Estado:");
        cmbFiltroEstado = new JComboBox<>();
        cmbFiltroEstado.addItem("Todos");
        cmbFiltroEstado.addItem("Activo");
        cmbFiltroEstado.addItem("Inactivo");
        cmbFiltroEstado.addItem("Bloqueado");

        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(COLOR_VERDE);
        btnBuscar.setForeground(COLOR_CREMA);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.addActionListener(e -> buscarEmpleados());

        btnLimpiarFiltros = new JButton("Limpiar");
        btnLimpiarFiltros.setBackground(COLOR_TERRACOTA);
        btnLimpiarFiltros.setForeground(COLOR_CREMA);
        btnLimpiarFiltros.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiarFiltros.addActionListener(e -> limpiarFiltros());

        panelFiltros.add(lblBuscar);
        panelFiltros.add(txtBuscar);
        panelFiltros.add(lblRol);
        panelFiltros.add(cmbFiltroRol);
        panelFiltros.add(lblEstado);
        panelFiltros.add(cmbFiltroEstado);
        panelFiltros.add(btnBuscar);
        panelFiltros.add(btnLimpiarFiltros);
    }

    private void configurarPanelTabla() {
        panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(COLOR_CREMA);
        panelTabla.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

        // Configurar la tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Apellido");
        modeloTabla.addColumn("Rol");
        modeloTabla.addColumn("Usuario");
        modeloTabla.addColumn("Estado");
        modeloTabla.addColumn("Último Login");

        tablaEmpleados = new JTable(modeloTabla);
        tablaEmpleados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEmpleados.getTableHeader().setReorderingAllowed(false);
        tablaEmpleados.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && tablaEmpleados.getSelectedRow() != -1) {
                    cargarEmpleadoSeleccionado();
                }
            }
        });

        scrollTabla = new JScrollPane(tablaEmpleados);
        panelTabla.add(scrollTabla, BorderLayout.CENTER);
    }

    private void configurarPanelEdicion() {
        panelEdicion = new JPanel();
        panelEdicion.setLayout(new BorderLayout());
        panelEdicion.setBackground(COLOR_CREMA);
        panelEdicion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(COLOR_TERRACOTA),
                        "Datos del Empleado/Usuario",
                        1,
                        0,
                        new Font("Segoe UI", Font.BOLD, 12),
                        COLOR_TERRACOTA),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Panel para datos del empleado
        JPanel panelDatosEmpleado = new JPanel();
        panelDatosEmpleado.setLayout(new GridBagLayout());
        panelDatosEmpleado.setBackground(COLOR_CREMA);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Etiquetas y campos
        JLabel lblId = new JLabel("ID:");
        txtId = new JTextField();
        txtId.setEditable(false);

        JLabel lblNombre = new JLabel("Nombre:");
        txtNombre = new JTextField();

        JLabel lblApellido = new JLabel("Apellido:");
        txtApellido = new JTextField();

        JLabel lblRol = new JLabel("Rol:");
        cmbRol = new JComboBox<>();
        try {
            List<Rol> roles = rolDAO.listarTodos();
            for (Rol rol : roles) {
                cmbRol.addItem(rol);
            }
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar roles: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        JLabel lblImagen = new JLabel("Imagen:");
        lblImagenEmpleado = new JLabel();
        lblImagenEmpleado.setPreferredSize(new Dimension(100, 100));
        lblImagenEmpleado.setBorder(BorderFactory.createLineBorder(COLOR_AZUL));
        lblImagenEmpleado.setHorizontalAlignment(SwingConstants.CENTER);

        btnSeleccionarImagen = new JButton("Seleccionar imagen");
        btnSeleccionarImagen.setBackground(COLOR_AZUL);
        btnSeleccionarImagen.setForeground(COLOR_CREMA);
        btnSeleccionarImagen.addActionListener(e -> seleccionarImagen());

        // Agregar componentes al panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelDatosEmpleado.add(lblId, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        panelDatosEmpleado.add(txtId, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        panelDatosEmpleado.add(lblNombre, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        panelDatosEmpleado.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        panelDatosEmpleado.add(lblApellido, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        panelDatosEmpleado.add(txtApellido, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        panelDatosEmpleado.add(lblRol, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        panelDatosEmpleado.add(cmbRol, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        panelDatosEmpleado.add(lblImagen, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelDatosEmpleado.add(lblImagenEmpleado, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelDatosEmpleado.add(btnSeleccionarImagen, gbc);

        // Panel para datos del usuario
        panelDetalleUsuario = new JPanel();
        panelDetalleUsuario.setLayout(new GridBagLayout());
        panelDetalleUsuario.setBackground(COLOR_CREMA);
        panelDetalleUsuario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_AZUL),
                "Datos de acceso",
                1,
                0,
                new Font("Segoe UI", Font.BOLD, 12),
                COLOR_AZUL));

        JLabel lblNombreUsuario = new JLabel("Nombre de usuario:");
        txtNombreUsuario = new JTextField();

        JLabel lblContraseña = new JLabel("Contraseña:");
        txtContraseña = new JPasswordField();

        JLabel lblConfirmarContraseña = new JLabel("Confirmar contraseña:");
        txtConfirmarContraseña = new JPasswordField();

        JLabel lblEstado = new JLabel("Estado:");
        String[] estados = {"Activo", "Inactivo", "Suspendido"};
        cmbEstado = new JComboBox<>(estados);

        btnResetContraseña = new JButton("Resetear contraseña");
        btnResetContraseña.setBackground(COLOR_AMBAR);
        btnResetContraseña.setForeground(Color.WHITE);
        btnResetContraseña.addActionListener(e -> resetearContraseña());

        // Agregar componentes al panel de usuario
        GridBagConstraints gbcUsuario = new GridBagConstraints();
        gbcUsuario.insets = new Insets(5, 5, 5, 5);
        gbcUsuario.anchor = GridBagConstraints.WEST;
        gbcUsuario.fill = GridBagConstraints.HORIZONTAL;

        gbcUsuario.gridx = 0;
        gbcUsuario.gridy = 0;
        panelDetalleUsuario.add(lblNombreUsuario, gbcUsuario);

        gbcUsuario.gridx = 1;
        gbcUsuario.gridy = 0;
        gbcUsuario.weightx = 1.0;
        panelDetalleUsuario.add(txtNombreUsuario, gbcUsuario);

        gbcUsuario.gridx = 0;
        gbcUsuario.gridy = 1;
        gbcUsuario.weightx = 0.0;
        panelDetalleUsuario.add(lblContraseña, gbcUsuario);

        gbcUsuario.gridx = 1;
        gbcUsuario.gridy = 1;
        gbcUsuario.weightx = 1.0;
        panelDetalleUsuario.add(txtContraseña, gbcUsuario);

        gbcUsuario.gridx = 0;
        gbcUsuario.gridy = 2;
        gbcUsuario.weightx = 0.0;
        panelDetalleUsuario.add(lblConfirmarContraseña, gbcUsuario);

        gbcUsuario.gridx = 1;
        gbcUsuario.gridy = 2;
        gbcUsuario.weightx = 1.0;
        panelDetalleUsuario.add(txtConfirmarContraseña, gbcUsuario);

        gbcUsuario.gridx = 0;
        gbcUsuario.gridy = 3;
        gbcUsuario.weightx = 0.0;
        panelDetalleUsuario.add(lblEstado, gbcUsuario);

        gbcUsuario.gridx = 1;
        gbcUsuario.gridy = 3;
        gbcUsuario.weightx = 1.0;
        panelDetalleUsuario.add(cmbEstado, gbcUsuario);

        gbcUsuario.gridx = 0;
        gbcUsuario.gridy = 4;
        gbcUsuario.gridwidth = 2;
        gbcUsuario.weightx = 1.0;
        panelDetalleUsuario.add(btnResetContraseña, gbcUsuario);

        // Agregar paneles al panel de edición
        panelEdicion.add(panelDatosEmpleado, BorderLayout.NORTH);
        panelEdicion.add(panelDetalleUsuario, BorderLayout.CENTER);

        // Inicialmente deshabilitamos los campos de edición
        setFormEditable(false);
    }

    private void configurarPanelAcciones() {
        panelAcciones = new JPanel();
        panelAcciones.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelAcciones.setBackground(COLOR_CREMA);
        panelAcciones.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        btnNuevo = new JButton("Nuevo");
        btnNuevo.setBackground(COLOR_VERDE);
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevo.addActionListener(e -> nuevoEmpleado());

        btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(COLOR_AZUL);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setEnabled(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardarEmpleado());

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(COLOR_TERRACOTA);
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setEnabled(false);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.addActionListener(e -> eliminarEmpleado());

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(COLOR_AMBAR);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setEnabled(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> cancelarEdicion());

        panelAcciones.add(btnNuevo);
        panelAcciones.add(btnGuardar);
        panelAcciones.add(btnEliminar);
        panelAcciones.add(btnCancelar);
    }

    public void cargarDatos() {
        limpiarTabla();
        try {
            List<Empleado> empleados = empleadoDAO.listarTodos();
            for (Empleado empleado : empleados) {
                Usuario usuario = null;
                try {
                    usuario = usuarioDAO.buscarPorEmpleado(empleado.getId());
                } catch (DAOException e) {
                    // El empleado puede no tener usuario asociado
                }

                Object[] fila = new Object[7];
                fila[0] = empleado.getId();
                fila[1] = empleado.getNombre();
                fila[2] = empleado.getApellido();
                fila[3] = empleado.getRol().getNombre();
                fila[4] = usuario != null ? usuario.getNombreUsuario() : "N/A";
                fila[5] = usuario != null ? usuario.getEstado().toString() : "N/A";
                fila[6] = usuario != null && usuario.getUltimoLogin() != null ?
                        formatearFecha(usuario.getUltimoLogin()) : "Nunca";

                modeloTabla.addRow(fila);
            }
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarTabla() {
        while (modeloTabla.getRowCount() > 0) {
            modeloTabla.removeRow(0);
        }
    }

    private String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(formatter);
    }

    private void cargarEmpleadoSeleccionado() {
        int filaSeleccionada = tablaEmpleados.getSelectedRow();
        if (filaSeleccionada != -1) {
            try {
                int idEmpleado = (int) tablaEmpleados.getValueAt(filaSeleccionada, 0);
                empleadoSeleccionado = empleadoDAO.buscarPorId(idEmpleado);

                try {
                    usuarioSeleccionado = usuarioDAO.buscarPorEmpleado(idEmpleado);
                } catch (DAOException e) {
                    usuarioSeleccionado = null;
                }

                mostrarDatosEmpleado();
                btnEliminar.setEnabled(true);
            } catch (DAOException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar empleado: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarDatosEmpleado() {
        if (empleadoSeleccionado != null) {
            txtId.setText(String.valueOf(empleadoSeleccionado.getId()));
            txtNombre.setText(empleadoSeleccionado.getNombre());
            txtApellido.setText(empleadoSeleccionado.getApellido());

            // Seleccionar el rol en el combo
            for (int i = 0; i < cmbRol.getItemCount(); i++) {
                Rol rol = (Rol) cmbRol.getItemAt(i);
                if (rol.getId().equals(empleadoSeleccionado.getRol().getId())) {
                    cmbRol.setSelectedIndex(i);
                    break;
                }
            }

            // Mostrar imagen
            mostrarImagenEmpleado(empleadoSeleccionado.getImagenUrl());

            // Datos de usuario
            if (usuarioSeleccionado != null) {
                txtNombreUsuario.setText(usuarioSeleccionado.getNombreUsuario());
                txtContraseña.setText("");
                txtConfirmarContraseña.setText("");
                cmbEstado.setSelectedItem(usuarioSeleccionado.getEstado());
                btnResetContraseña.setEnabled(true);
            } else {
                txtNombreUsuario.setText("");
                txtContraseña.setText("");
                txtConfirmarContraseña.setText("");
                cmbEstado.setSelectedIndex(0); // Por defecto ACTIVO
                btnResetContraseña.setEnabled(false);
            }
        }
    }

    private void mostrarImagenEmpleado(String rutaImagen) {
        ImageIcon imagenEmpleado = null;

        if (rutaImagen != null && !rutaImagen.isEmpty()) {
            // Verificar si la ruta contiene "/imagenes/" y transformarla a ruta absoluta
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

        // Redimensionar imagen
        if (imagenEmpleado != null) {
            Image img = imagenEmpleado.getImage();
            Image imgRedimensionada = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imagenEmpleado = new ImageIcon(imgRedimensionada);
        }

        lblImagenEmpleado.setIcon(imagenEmpleado);
    }

    private void nuevoEmpleado() {
        limpiarFormulario();
        setFormEditable(true);
        esNuevoEmpleado = true;
        modoEdicion = true;
        btnGuardar.setEnabled(true);
        btnEliminar.setEnabled(false);
        btnCancelar.setEnabled(true);
        btnNuevo.setEnabled(false);
        btnResetContraseña.setEnabled(false);
        tablaEmpleados.clearSelection();
    }

    private void guardarEmpleado() {
        if (!validarFormulario()) {
            return;
        }

        try {
            // Crear o actualizar el empleado
            Empleado empleado = esNuevoEmpleado ? new Empleado() : empleadoSeleccionado;

            empleado.setNombre(txtNombre.getText().trim());
            empleado.setApellido(txtApellido.getText().trim());
            empleado.setRol((Rol) cmbRol.getSelectedItem());

            // Manejar la imagen
            if (rutaImagenSeleccionada != null && !rutaImagenSeleccionada.isEmpty()) {
                empleado.setImagenUrl(rutaImagenSeleccionada);
            } else if (esNuevoEmpleado) {
                empleado.setImagenUrl("/imagenes/empleados/default.png");
            }

            empleado.setFechaActualizacion(LocalDateTime.now());
            if (esNuevoEmpleado) {
                empleado.setFechaCreacion(LocalDateTime.now());
                empleadoDAO.guardar(empleado);
            } else {
                empleadoDAO.actualizar(empleado);
            }

            // Manejar el usuario
            String nombreUsuario = txtNombreUsuario.getText().trim();
            String contraseña = new String(txtContraseña.getPassword());

            if (!nombreUsuario.isEmpty()) {
                Usuario usuario = usuarioSeleccionado;
                if (usuario == null) {
                    usuario = new Usuario();
                    usuario.setFechaCreacion(LocalDateTime.now());
                    usuario.setEmpleado(empleado);
                }

                usuario.setNombreUsuario(nombreUsuario);
                if (!contraseña.isEmpty()) {
                    usuario.setHashContraseña(HashUtil.hashPassword(contraseña));
                }
                usuario.setEstado(estadoUsuarioDAO.buscarPorNombre((String) cmbEstado.getSelectedItem()));
                usuario.setFechaActualizacion(LocalDateTime.now());

                if (usuarioSeleccionado == null) {
                    usuarioDAO.guardar(usuario);
                } else {
                    usuarioDAO.actualizar(usuario);
                }
            }

            JOptionPane.showMessageDialog(this, "Empleado guardado exitosamente.",
                    "Información", JOptionPane.INFORMATION_MESSAGE);

            cargarDatos();
            limpiarFormulario();
            setFormEditable(false);
            modoEdicion = false;
            esNuevoEmpleado = false;
            actualizarBotones();
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEmpleado() {
        if (empleadoSeleccionado == null) {
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar al empleado " +
                        empleadoSeleccionado.getNombre() + " " +
                        empleadoSeleccionado.getApellido() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                // Primero eliminar usuario asociado si existe
                if (usuarioSeleccionado != null) {
                    usuarioDAO.eliminar(usuarioSeleccionado.getId());
                }

                // Luego eliminar el empleado
                empleadoDAO.eliminar(empleadoSeleccionado.getId());

                JOptionPane.showMessageDialog(this, "Empleado eliminado exitosamente.",
                        "Información", JOptionPane.INFORMATION_MESSAGE);

                cargarDatos();
                limpiarFormulario();
                setFormEditable(false);
                actualizarBotones();
            } catch (DAOException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelarEdicion() {
        if (empleadoSeleccionado != null) {
            mostrarDatosEmpleado();
        } else {
            limpiarFormulario();
        }

        setFormEditable(false);
        modoEdicion = false;
        esNuevoEmpleado = false;
        actualizarBotones();
    }

    private void actualizarBotones() {
        btnNuevo.setEnabled(!modoEdicion);
        btnGuardar.setEnabled(modoEdicion);
        btnEliminar.setEnabled(!modoEdicion && empleadoSeleccionado != null);
        btnCancelar.setEnabled(modoEdicion);
        btnResetContraseña.setEnabled(!modoEdicion && usuarioSeleccionado != null);
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        cmbRol.setSelectedIndex(0);
        txtNombreUsuario.setText("");
        txtContraseña.setText("");
        txtConfirmarContraseña.setText("");
        cmbEstado.setSelectedIndex(0);
        rutaImagenSeleccionada = null;
        mostrarImagenEmpleado(null);

        empleadoSeleccionado = null;
        usuarioSeleccionado = null;
    }

    private void setFormEditable(boolean editable) {
        txtNombre.setEditable(editable);
        txtApellido.setEditable(editable);
        cmbRol.setEnabled(editable);
        btnSeleccionarImagen.setEnabled(editable);
        txtNombreUsuario.setEditable(editable);
        txtContraseña.setEditable(editable);
        txtConfirmarContraseña.setEditable(editable);
        cmbEstado.setEnabled(editable);
    }

    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();

        if (txtNombre.getText().trim().isEmpty()) {
            errores.append("- El nombre es obligatorio.\n");
        }

        if (txtApellido.getText().trim().isEmpty()) {
            errores.append("- El apellido es obligatorio.\n");
        }

        if (txtNombreUsuario.getText().trim().isEmpty()) {
            errores.append("- El nombre de usuario es obligatorio.\n");
        } else {
            // Verificar si el nombre de usuario ya existe
            try {
                Usuario usuarioExistente = usuarioDAO.buscarPorNombreUsuario(txtNombreUsuario.getText().trim());
                if (usuarioExistente != null && (usuarioSeleccionado == null ||
                        !usuarioExistente.getId().equals(usuarioSeleccionado.getId()))) {
                    errores.append("- El nombre de usuario ya existe.\n");
                }
            } catch (DAOException e) {
                // Ignorar este error específico
            }
        }

        // Validar contraseñas solo si es un nuevo usuario o se está cambiando la contraseña
        String contraseña = new String(txtContraseña.getPassword());
        String confirmarContraseña = new String(txtConfirmarContraseña.getPassword());

        if ((usuarioSeleccionado == null || !contraseña.isEmpty()) && contraseña.isEmpty()) {
            errores.append("- La contraseña es obligatoria para nuevos usuarios.\n");
        }

        if (!contraseña.isEmpty() && !contraseña.equals(confirmarContraseña)) {
            errores.append("- Las contraseñas no coinciden.\n");
        }

        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Por favor corrija los siguientes errores:\n" + errores.toString(),
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar imagen");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png", "gif"));

        int seleccion = fileChooser.showOpenDialog(this);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            rutaImagenSeleccionada = "/imagenes/empleados/" + archivo.getName();

            // Mostrar la imagen seleccionada
            try {
                ImageIcon imagen = new ImageIcon(archivo.getAbsolutePath());
                Image img = imagen.getImage();
                Image imgRedimensionada = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                lblImagenEmpleado.setIcon(new ImageIcon(imgRedimensionada));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cargar la imagen: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetearContraseña() {
        if (usuarioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "No hay usuario seleccionado.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea resetear la contraseña del usuario " +
                        usuarioSeleccionado.getNombreUsuario() + "?",
                "Confirmar reseteo", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // Generar una contraseña temporal
            String contraseñaTemporal = generarContraseñaTemporal();

            try {
                // Actualizar la contraseña en la base de datos
                usuarioSeleccionado.setHashContraseña(HashUtil.hashPassword(contraseñaTemporal));
                usuarioSeleccionado.setFechaActualizacion(LocalDateTime.now());
                usuarioDAO.actualizar(usuarioSeleccionado);

                // Mostrar la nueva contraseña
                JOptionPane.showMessageDialog(this,
                        "La contraseña ha sido reseteada. Nueva contraseña temporal:\n" +
                                contraseñaTemporal + "\n\n" +
                                "Por favor, informe al usuario que debe cambiar esta contraseña.",
                        "Contraseña reseteada", JOptionPane.INFORMATION_MESSAGE);
            } catch (DAOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error al resetear la contraseña: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String generarContraseñaTemporal() {
        // Generar una contraseña aleatoria de 8 caracteres
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            int indice = (int) (Math.random() * caracteres.length());
            sb.append(caracteres.charAt(indice));
        }
        return sb.toString();
    }

    private void buscarEmpleados() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        String rolSeleccionado = cmbFiltroRol.getSelectedItem().toString();
        String estadoSeleccionado = cmbFiltroEstado.getSelectedItem().toString();

        limpiarTabla();

        try {
            List<Empleado> empleados = empleadoDAO.listarTodos();
            for (Empleado empleado : empleados) {
                // Filtrar por texto de búsqueda
                boolean coincideTexto = textoBusqueda.isEmpty() ||
                        empleado.getNombre().toLowerCase().contains(textoBusqueda) ||
                        empleado.getApellido().toLowerCase().contains(textoBusqueda);

                // Filtrar por rol
                boolean coincideRol = rolSeleccionado.equals("Todos") ||
                        empleado.getRol().getNombre().equals(rolSeleccionado);

                // Buscar usuario asociado
                Usuario usuario = null;
                try {
                    usuario = usuarioDAO.buscarPorEmpleado(empleado.getId());
                } catch (DAOException e) {
                    // El empleado puede no tener usuario asociado
                }

                // Filtrar por estado
                boolean coincideEstado = estadoSeleccionado.equals("Todos") ||
                        (usuario != null && usuario.getEstado().toString().equals(estadoSeleccionado)) ||
                        (estadoSeleccionado.equals("Inactivo") && usuario == null);

                if (coincideTexto && coincideRol && coincideEstado) {
                    Object[] fila = new Object[7];
                    fila[0] = empleado.getId();
                    fila[1] = empleado.getNombre();
                    fila[2] = empleado.getApellido();
                    fila[3] = empleado.getRol().getNombre();
                    fila[4] = usuario != null ? usuario.getNombreUsuario() : "N/A";
                    fila[5] = usuario != null ? usuario.getEstado().toString() : "N/A";
                    fila[6] = usuario != null && usuario.getUltimoLogin() != null ?
                            formatearFecha(usuario.getUltimoLogin()) : "Nunca";

                    modeloTabla.addRow(fila);
                }
            }
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFiltros() {
        txtBuscar.setText("");
        cmbFiltroRol.setSelectedIndex(0);
        cmbFiltroEstado.setSelectedIndex(0);
        cargarDatos();
    }
}