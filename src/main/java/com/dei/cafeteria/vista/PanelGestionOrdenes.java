package com.dei.cafeteria.vista;

import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.dao.EstadoOrdenDAO;
import com.dei.cafeteria.dao.ItemOrdenDAO;
import com.dei.cafeteria.dao.OrdenDAO;
import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.modelo.EstadoOrden;
import com.dei.cafeteria.modelo.ItemOrden;
import com.dei.cafeteria.modelo.Orden;
import com.dei.cafeteria.util.ColorPaleta;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel para gestionar órdenes existentes, ver su estado y actualizarlo
 */
public class PanelGestionOrdenes extends JPanel {

    private Empleado meseroActual;
    private ColorPaleta ColorPaleta; // Referencia a la vista mesero para recargar mesas

    // Componentes de la interfaz
    private JComboBox<String> cbFiltroEstado;
    private JTextField txtBuscar;
    private JPanel panelOrdenes;
    private JScrollPane scrollOrdenes;

    // Datos
    private OrdenDAO ordenDAO;
    private ItemOrdenDAO itemOrdenDAO;
    private EstadoOrdenDAO estadoOrdenDAO;
    private List<Orden> ordenesActuales;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Colores para estados
    private final Color COLOR_PENDIENTE = new Color(255, 193, 7);
    private final Color COLOR_EN_PREPARACION = new Color(0, 123, 255);
    private final Color COLOR_LISTA = new Color(40, 167, 69);
    private final Color COLOR_ENTREGADA = new Color(23, 162, 184);
    private final Color COLOR_PAGADA = new Color(108, 117, 125);
    private final Color COLOR_CANCELADA = new Color(220, 53, 69);

    // Clase para implementar patrón Observer en búsqueda
    private class BusquedaListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            filtrarOrdenes();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            filtrarOrdenes();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            filtrarOrdenes();
        }
    }

    public PanelGestionOrdenes(Empleado meseroActual) {
        this.meseroActual = meseroActual;
        setLayout(new BorderLayout());
        setBackground(ColorPaleta.CREMA.getColor());

        inicializarDAO();
        inicializarComponentes();
        cargarOrdenes();
    }

    private void inicializarDAO() {
        ordenDAO = new OrdenDAO();
        itemOrdenDAO = new ItemOrdenDAO();
        estadoOrdenDAO = new EstadoOrdenDAO();
        ordenesActuales = new ArrayList<>();
    }

    private void inicializarComponentes() {
        // Panel superior con título y filtros
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBackground(ColorPaleta.CREMA.getColor());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Gestión de Órdenes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(ColorPaleta.AZUL.getColor());
        panelSuperior.add(lblTitulo, gbc);

        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelFiltros.setBackground(ColorPaleta.CREMA.getColor());

        JLabel lblFiltroEstado = new JLabel("Estado:");
        lblFiltroEstado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFiltroEstado.setForeground(ColorPaleta.AZUL.getColor());

        cbFiltroEstado = new JComboBox<>(new String[]{
                "Todos", "PENDIENTE", "EN PREPARACIÓN", "LISTA", "ENTREGADA", "PAGADA", "CANCELADA"
        });
        cbFiltroEstado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbFiltroEstado.setPreferredSize(new Dimension(150, 30));

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBuscar.setForeground(ColorPaleta.AZUL.getColor());

        txtBuscar = new JTextField(15);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPaleta.AZUL.getColor()),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Implementar patrón Observer en la búsqueda
        txtBuscar.getDocument().addDocumentListener(new BusquedaListener());

        panelFiltros.add(lblFiltroEstado);
        panelFiltros.add(cbFiltroEstado);
        panelFiltros.add(Box.createHorizontalStrut(15));
        panelFiltros.add(lblBuscar);
        panelFiltros.add(txtBuscar);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        panelSuperior.add(panelFiltros, gbc);

        // Evento para el filtro de estado
        cbFiltroEstado.addActionListener(e -> cargarOrdenes());

        // Panel central para las órdenes
        panelOrdenes = new JPanel();
        panelOrdenes.setLayout(new BoxLayout(panelOrdenes, BoxLayout.Y_AXIS));
        panelOrdenes.setBackground(ColorPaleta.CREMA.getColor());

        scrollOrdenes = new JScrollPane(panelOrdenes);
        scrollOrdenes.setBorder(null);
        scrollOrdenes.getVerticalScrollBar().setUnitIncrement(16);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollOrdenes, BorderLayout.CENTER);
    }

    /**
     * Metodo público para actualizar las órdenes en tiempo real
     * Este metodo será llamado desde fuera cuando se necesite actualizar
     */
    public void actualizarOrdenes() {
        cargarOrdenes();
    }

    private void cargarOrdenes() {
        // Limpiar panel de órdenes
        panelOrdenes.removeAll();
        ordenesActuales.clear();

        try {
            String filtroEstado = (String) cbFiltroEstado.getSelectedItem();
            List<Orden> ordenes;

            // Aplicar filtro si no es "Todos"
            if (!"Todos".equals(filtroEstado)) {
                // Convertir nombre del estado a ID
                int estadoId = obtenerIdEstado(filtroEstado);
                ordenes = ordenDAO.buscarPorEstado(estadoId);
            } else {
                ordenes = ordenDAO.listarTodos();
            }

            ordenesActuales.addAll(ordenes);

            // Aplicar filtro de búsqueda si hay texto
            if (!txtBuscar.getText().isEmpty()) {
                filtrarOrdenes();
                return;
            }

            // Mostrar órdenes en el panel
            mostrarOrdenes(ordenesActuales);

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar las órdenes: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarOrdenes(List<Orden> ordenes) {
        panelOrdenes.removeAll();

        if (ordenes.isEmpty()) {
            JLabel lblNoOrdenes = new JLabel("No hay órdenes para mostrar");
            lblNoOrdenes.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            lblNoOrdenes.setForeground(ColorPaleta.AZUL.getColor());
            lblNoOrdenes.setAlignmentX(Component.CENTER_ALIGNMENT);

            panelOrdenes.add(Box.createVerticalGlue());
            panelOrdenes.add(lblNoOrdenes);
            panelOrdenes.add(Box.createVerticalGlue());
        } else {
            for (Orden orden : ordenes) {
                JPanel panelOrden = crearPanelOrden(orden);
                panelOrdenes.add(panelOrden);
                panelOrdenes.add(Box.createVerticalStrut(10));
            }
        }

        panelOrdenes.revalidate();
        panelOrdenes.repaint();
    }

    private JPanel crearPanelOrden(Orden orden) {
        JPanel panelOrden = new JPanel();
        panelOrden.setLayout(new BorderLayout());
        panelOrden.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getColorEstado(orden.getEstadoId()), 3),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panelOrden.setBackground(Color.WHITE);
        panelOrden.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        // Panel izquierdo - Info básica
        JPanel panelInfo = new JPanel(new GridLayout(4, 1, 5, 5));
        panelInfo.setBackground(Color.WHITE);

        JLabel lblOrdenId = new JLabel("Orden #" + orden.getId());
        lblOrdenId.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblOrdenId.setForeground(ColorPaleta.AZUL.getColor());

        JLabel lblMesa = new JLabel("Mesa: " + orden.getMesa().getId());
        lblMesa.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel lblTotal = new JLabel(String.format("Total: $%.2f", orden.getTotal()));
        lblTotal.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel lblFecha = new JLabel("Creada: " + orden.getFechaCreacion().format(formatter));
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panelInfo.add(lblOrdenId);
        panelInfo.add(lblMesa);
        panelInfo.add(lblTotal);
        panelInfo.add(lblFecha);

        // Panel central - Estado
        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelEstado.setBackground(Color.WHITE);

        JLabel lblEstado = new JLabel(obtenerNombreEstado(orden.getEstadoId()));
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblEstado.setForeground(getColorEstado(orden.getEstadoId()));

        panelEstado.add(lblEstado);

        // Panel derecho - Acciones
        JPanel panelAcciones = new JPanel(new GridLayout(2, 1, 5, 10));
        panelAcciones.setBackground(Color.WHITE);

        JButton btnVerDetalles = new JButton("Ver Detalles");
        btnVerDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnVerDetalles.setBackground(ColorPaleta.AMBAR.getColor());
        btnVerDetalles.setForeground(Color.WHITE);
        btnVerDetalles.setBorderPainted(false);
        btnVerDetalles.setFocusPainted(false);

        JPanel panelBotonesEstado = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelBotonesEstado.setBackground(Color.WHITE);

        // Botones según el estado actual
        if (orden.getEstadoId() == 1) { // PENDIENTE
            JButton btnCancelar = new JButton("Cancelar");
            btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btnCancelar.setBackground(ColorPaleta.TERRACOTA.getColor());
            btnCancelar.setForeground(Color.WHITE);
            btnCancelar.setBorderPainted(false);
            btnCancelar.setFocusPainted(false);

            JButton btnEntregar = new JButton("Entregar");
            btnEntregar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btnEntregar.setBackground(ColorPaleta.VERDE.getColor());
            btnEntregar.setForeground(Color.WHITE);
            btnEntregar.setBorderPainted(false);
            btnEntregar.setFocusPainted(false);

            btnCancelar.addActionListener(e -> cambiarEstadoOrden(orden, 6)); // 6 = CANCELADA
            btnEntregar.addActionListener(e -> {
                // Primero cambiar a EN PREPARACIÓN
                cambiarEstadoOrdenSinConfirmacion(orden, 2);
                // Luego cambiar a LISTA
                cambiarEstadoOrdenSinConfirmacion(orden, 3);
                // Finalmente cambiar a ENTREGADA
                cambiarEstadoOrden(orden, 4);
            });

            panelBotonesEstado.add(btnEntregar);
            panelBotonesEstado.add(btnCancelar);
        } else if (orden.getEstadoId() == 2) { // EN PREPARACIÓN
            JButton btnCancelar = new JButton("Cancelar");
            btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btnCancelar.setBackground(ColorPaleta.TERRACOTA.getColor());
            btnCancelar.setForeground(Color.WHITE);
            btnCancelar.setBorderPainted(false);
            btnCancelar.setFocusPainted(false);

            btnCancelar.addActionListener(e -> cambiarEstadoOrden(orden, 6)); // 6 = CANCELADA

            panelBotonesEstado.add(btnCancelar);
        } else if (orden.getEstadoId() == 3) { // LISTA
            JButton btnEntregar = new JButton("Entregar");
            btnEntregar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btnEntregar.setBackground(ColorPaleta.VERDE.getColor());
            btnEntregar.setForeground(Color.WHITE);
            btnEntregar.setBorderPainted(false);
            btnEntregar.setFocusPainted(false);

            btnEntregar.addActionListener(e -> cambiarEstadoOrden(orden, 4)); // 4 = ENTREGADA

            panelBotonesEstado.add(btnEntregar);
        } else if (orden.getEstadoId() == 4) { // ENTREGADA
            JButton btnMarcarPagada = new JButton("Marcar Pagada");
            btnMarcarPagada.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btnMarcarPagada.setBackground(ColorPaleta.AZUL.getColor());
            btnMarcarPagada.setForeground(Color.WHITE);
            btnMarcarPagada.setBorderPainted(false);
            btnMarcarPagada.setFocusPainted(false);

            btnMarcarPagada.addActionListener(e -> cambiarEstadoOrden(orden, 5)); // 5 = PAGADA

            panelBotonesEstado.add(btnMarcarPagada);
        }

        btnVerDetalles.addActionListener(e -> mostrarDetalleOrden(orden));

        panelAcciones.add(btnVerDetalles);
        panelAcciones.add(panelBotonesEstado);

        // Añadir todos los paneles al panel principal
        panelOrden.add(panelInfo, BorderLayout.WEST);
        panelOrden.add(panelEstado, BorderLayout.CENTER);
        panelOrden.add(panelAcciones, BorderLayout.EAST);

        // Efecto hover
        panelOrden.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panelOrden.setBackground(new Color(245, 245, 245));
                panelInfo.setBackground(new Color(245, 245, 245));
                panelEstado.setBackground(new Color(245, 245, 245));
                panelAcciones.setBackground(new Color(245, 245, 245));
                panelBotonesEstado.setBackground(new Color(245, 245, 245));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panelOrden.setBackground(Color.WHITE);
                panelInfo.setBackground(Color.WHITE);
                panelEstado.setBackground(Color.WHITE);
                panelAcciones.setBackground(Color.WHITE);
                panelBotonesEstado.setBackground(Color.WHITE);
            }
        });

        return panelOrden;
    }

    private void filtrarOrdenes() {
        String textoBusqueda = txtBuscar.getText().toLowerCase();
        if (textoBusqueda.isEmpty()) {
            mostrarOrdenes(ordenesActuales);
            return;
        }

        List<Orden> ordenesFiltradas = new ArrayList<>();

        for (Orden orden : ordenesActuales) {
            // Filtrar por número de orden o número de mesa
            if (String.valueOf(orden.getId()).contains(textoBusqueda) ||
                    String.valueOf(orden.getMesa().getNumero()).contains(textoBusqueda)) {
                ordenesFiltradas.add(orden);
            }
        }

        mostrarOrdenes(ordenesFiltradas);
    }

    /**
     * Cambia el estado de una orden sin mostrar diálogo de confirmación
     * Usado para transiciones rápidas entre estados
     */
    private void cambiarEstadoOrdenSinConfirmacion(Orden orden, int nuevoEstadoId) {
        try {
            // Obtener el objeto estado
            EstadoOrden nuevoEstado = estadoOrdenDAO.buscarPorId(nuevoEstadoId);

            // Actualizar el estado en la base de datos
            orden.setEstado(nuevoEstado);
            ordenDAO.actualizar(orden);

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cambiar el estado de la orden: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstadoOrden(Orden orden, int nuevoEstadoId) {
        try {
            // Obtener el objeto estado
            EstadoOrden nuevoEstado = estadoOrdenDAO.buscarPorId(nuevoEstadoId);

            // Confirmar la acción según el tipo de cambio
            String mensaje = "¿Está seguro de cambiar el estado de la orden a " +
                    obtenerNombreEstado(nuevoEstadoId) + "?";

            int confirmacion = JOptionPane.showConfirmDialog(
                    this, mensaje, "Confirmar cambio", JOptionPane.YES_NO_OPTION);

            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }

            // Actualizar el estado en la base de datos
            orden.setEstado(nuevoEstado);
            ordenDAO.actualizar(orden);

            // Recargar las mesas si la orden se marcó como pagada o cancelada
            if (nuevoEstadoId == 5 || nuevoEstadoId == 6) {
                PanelMesas panelMesas = new PanelMesas();
                panelMesas.recargarMesas();
            }

            // Recargar órdenes
            cargarOrdenes();

            JOptionPane.showMessageDialog(this,
                    "Estado de la orden actualizado correctamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cambiar el estado de la orden: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDetalleOrden(Orden orden) {
        try {
            // Obtener items de la orden
            List<ItemOrden> items = itemOrdenDAO.buscarPorOrdenID(orden.getId());

            // Crear panel para mostrar detalles
            JPanel panelDetalle = new JPanel();
            panelDetalle.setLayout(new BoxLayout(panelDetalle, BoxLayout.Y_AXIS));
            panelDetalle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Información de la orden
            JPanel panelInfoOrden = new JPanel(new GridLayout(4, 2, 10, 5));
            panelInfoOrden.setBorder(BorderFactory.createTitledBorder("Información de la Orden"));

            panelInfoOrden.add(new JLabel("Orden #:"));
            panelInfoOrden.add(new JLabel(String.valueOf(orden.getId())));

            panelInfoOrden.add(new JLabel("Mesa:"));
            panelInfoOrden.add(new JLabel(String.valueOf(orden.getMesa().getNumero())));

            panelInfoOrden.add(new JLabel("Mesero:"));
            panelInfoOrden.add(new JLabel(orden.getMesero().getNombre()));

            panelInfoOrden.add(new JLabel("Fecha:"));
            panelInfoOrden.add(new JLabel(orden.getFechaCreacion().format(formatter)));

            panelDetalle.add(panelInfoOrden);
            panelDetalle.add(Box.createVerticalStrut(15));

            // Lista de productos
            JPanel panelProductos = new JPanel();
            panelProductos.setLayout(new BoxLayout(panelProductos, BoxLayout.Y_AXIS));
            panelProductos.setBorder(BorderFactory.createTitledBorder("Productos"));

            // Cabecera
            JPanel panelCabecera = new JPanel(new GridLayout(1, 4));
            panelCabecera.add(new JLabel("Producto", JLabel.CENTER));
            panelCabecera.add(new JLabel("Tamaño", JLabel.CENTER));
            panelCabecera.add(new JLabel("Cantidad", JLabel.CENTER));
            panelCabecera.add(new JLabel("Subtotal", JLabel.CENTER));
            panelProductos.add(panelCabecera);
            panelProductos.add(new JSeparator());

            // Items
            for (ItemOrden item : items) {
                JPanel panelItem = new JPanel(new GridLayout(1, 4));
                panelItem.add(new JLabel(item.getProducto().getNombre(), JLabel.CENTER));
                panelItem.add(new JLabel(item.getTamaño().getNombre(), JLabel.CENTER));
                panelItem.add(new JLabel(String.valueOf(item.getCantidad()), JLabel.CENTER));

                double subtotal = item.getPrecioUnitario() * item.getCantidad();
                panelItem.add(new JLabel(String.format("$%.2f", subtotal), JLabel.CENTER));

                panelProductos.add(panelItem);

                // Notas del producto (si existen)
                if (item.getNotas() != null && !item.getNotas().isEmpty()) {
                    JPanel panelNotas = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    JLabel lblNotas = new JLabel("Notas: " + item.getNotas());
                    lblNotas.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                    panelNotas.add(lblNotas);
                    panelProductos.add(panelNotas);
                }

                panelProductos.add(new JSeparator());
            }

            // Panel para el total
            JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JLabel lblTotal = new JLabel("TOTAL: " + String.format("$%.2f", orden.getTotal()));
            lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
            panelTotal.add(lblTotal);

            panelDetalle.add(panelProductos);
            panelDetalle.add(Box.createVerticalStrut(10));
            panelDetalle.add(panelTotal);

            // Mostrar diálogo
            JScrollPane scrollDetalle = new JScrollPane(panelDetalle);
            scrollDetalle.setPreferredSize(new Dimension(500, 400));

            JOptionPane.showMessageDialog(this, scrollDetalle,
                    "Detalle de Orden #" + orden.getId(),
                    JOptionPane.PLAIN_MESSAGE);

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener detalles de la orden: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int obtenerIdEstado(String nombreEstado) {
        switch (nombreEstado) {
            case "PENDIENTE": return 1;
            case "EN PREPARACIÓN": return 2;
            case "LISTA": return 3;
            case "ENTREGADA": return 4;
            case "PAGADA": return 5;
            case "CANCELADA": return 6;
            default: return 0;
        }
    }

    private String obtenerNombreEstado(int estadoId) {
        switch (estadoId) {
            case 1: return "PENDIENTE";
            case 2: return "EN PREPARACIÓN";
            case 3: return "LISTA";
            case 4: return "ENTREGADA";
            case 5: return "PAGADA";
            case 6: return "CANCELADA";
            default: return "DESCONOCIDO";
        }
    }

    private Color getColorEstado(int estadoId) {
        switch (estadoId) {
            case 1: return COLOR_PENDIENTE;         // PENDIENTE
            case 2: return COLOR_EN_PREPARACION;    // EN PREPARACIÓN
            case 3: return COLOR_LISTA;             // LISTA
            case 4: return COLOR_ENTREGADA;         // ENTREGADA
            case 5: return COLOR_PAGADA;            // PAGADA
            case 6: return COLOR_CANCELADA;         // CANCELADA
            default: return Color.GRAY;
        }
    }
}