package com.dei.cafeteria.vista;

import com.dei.cafeteria.controlador.ControladorMesas;
import com.dei.cafeteria.controlador.ControladorProductos;
import com.dei.cafeteria.dao.*;
import com.dei.cafeteria.modelo.*;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel para tomar órdenes/pedidos
 */
class PanelTomarOrden extends JPanel {

    private Empleado meseroActual;

    // Componentes de la interfaz
    private JComboBox<String> cmbMesas;
    private JTextField txtBuscarProducto;
    private JList<String> listaProductosDisponibles;
    private DefaultListModel<String> modeloProductosDisponibles;
    private JSpinner spnCantidad;
    private JTable tablaProductosOrden;
    private DefaultTableModel modeloTablaProductos;
    private JLabel lblSubtotal;

    // Datos simulados
    private List<Mesa> listaMesas;
    private List<Producto> listaProductos;
    private List<ItemOrden> elementosPedido;
    private int mesaSeleccionada;

    public PanelTomarOrden(Empleado meseroActual) {
        this.meseroActual = meseroActual;
        setLayout(new BorderLayout());
        setBackground(VistaMesero.COLOR_CREMA);
        elementosPedido = new ArrayList<>();

        inicializarComponentes();
        cargarDatosPrueba();
    }

    private void inicializarComponentes() {
        // Panel superior con título y selección de mesa
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(VistaMesero.COLOR_CREMA);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("Tomar Orden");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(VistaMesero.COLOR_AZUL);

        JPanel panelMesa = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelMesa.setBackground(VistaMesero.COLOR_CREMA);

        JLabel lblSeleccionMesa = new JLabel("Mesa:");
        lblSeleccionMesa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSeleccionMesa.setForeground(VistaMesero.COLOR_AZUL);

        cmbMesas = new JComboBox<>();
        cmbMesas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbMesas.setPreferredSize(new Dimension(150, 30));
        cmbMesas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, VistaMesero.COLOR_AZUL),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        panelMesa.add(lblSeleccionMesa);
        panelMesa.add(cmbMesas);
        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        panelSuperior.add(panelMesa, BorderLayout.EAST);

        // Panel principal con división
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de productos
        JPanel panelProductos = new JPanel(new BorderLayout());
        panelProductos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelBusqueda = new JPanel(new BorderLayout());
        panelBusqueda.setBackground(VistaMesero.COLOR_CREMA);

        txtBuscarProducto = new JTextField();
        txtBuscarProducto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, VistaMesero.COLOR_AZUL),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        txtBuscarProducto.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBuscar.setBackground(VistaMesero.COLOR_AMBAR);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setBorderPainted(false);
        btnBuscar.setFocusPainted(false);

        panelBusqueda.add(txtBuscarProducto, BorderLayout.CENTER);
        panelBusqueda.add(btnBuscar, BorderLayout.EAST);

        modeloProductosDisponibles = new DefaultListModel<>();
        listaProductosDisponibles = new JList<>(modeloProductosDisponibles);
        listaProductosDisponibles.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listaProductosDisponibles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollProductos = new JScrollPane(listaProductosDisponibles);
        scrollProductos.setBorder(null);

        panelProductos.add(panelBusqueda, BorderLayout.NORTH);
        panelProductos.add(scrollProductos, BorderLayout.CENTER);

        // Panel de cantidad y agregar
        JPanel panelAgregar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelAgregar.setBackground(VistaMesero.COLOR_CREMA);

        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCantidad.setForeground(VistaMesero.COLOR_AZUL);

        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        spnCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spnCantidad.setPreferredSize(new Dimension(60, 30));

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnAgregar.setBackground(VistaMesero.COLOR_VERDE);
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setBorderPainted(false);
        btnAgregar.setFocusPainted(false);

        panelAgregar.add(lblCantidad);
        panelAgregar.add(spnCantidad);
        panelAgregar.add(btnAgregar);

        panelProductos.add(panelAgregar, BorderLayout.SOUTH);

        // Panel de pedido
        JPanel panelPedido = new JPanel(new BorderLayout());
        panelPedido.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTablaProductos = new DefaultTableModel(
                new Object[]{"Producto", "Precio", "Cantidad", "Subtotal", ""}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        tablaProductosOrden = new JTable(modeloTablaProductos);
        tablaProductosOrden.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaProductosOrden.getTableHeader().setBackground(VistaMesero.COLOR_CREMA);
        tablaProductosOrden.setRowHeight(30);
        tablaProductosOrden.setShowGrid(false);

        // Configurar botón eliminar
        tablaProductosOrden.getColumnModel().getColumn(4).setCellRenderer(new BotonEliminarRenderer());
        tablaProductosOrden.getColumnModel().getColumn(4).setCellEditor(new BotonEliminarEditor());

        JScrollPane scrollPedido = new JScrollPane(tablaProductosOrden);
        scrollPedido.setBorder(null);

        // Panel subtotal
        JPanel panelSubtotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSubtotal.setBackground(VistaMesero.COLOR_CREMA);

        lblSubtotal = new JLabel("Subtotal: $0.00");
        lblSubtotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSubtotal.setForeground(VistaMesero.COLOR_TERRACOTA);

        panelSubtotal.add(lblSubtotal);
        panelPedido.add(scrollPedido, BorderLayout.CENTER);
        panelPedido.add(panelSubtotal, BorderLayout.SOUTH);

        splitPane.setLeftComponent(panelProductos);
        splitPane.setRightComponent(panelPedido);

        add(panelSuperior, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // Eventos
        btnBuscar.addActionListener(e -> buscarProductos());
        btnAgregar.addActionListener(e -> agregarProducto());
        cmbMesas.addActionListener(e -> actualizarMesaSeleccionada());
    }

    private void cargarDatosPrueba() {
        ControladorMesas controladorMesas = new ControladorMesas();
        ControladorProductos controladorProductos = new ControladorProductos();
        try {
            listaMesas = controladorMesas.obtenerTodasLasMesas();
            listaProductos = controladorProductos.obtenerTodosLosProductos();
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }


        actualizarComboMesas();
        actualizarListaProductos();
    }

    private void actualizarComboMesas() {
        cmbMesas.removeAllItems();
        listaMesas.stream()
                .filter(m -> m.getEstadoMesa() == 1)
                .forEach(m -> cmbMesas.addItem("Mesa " + m.getNumero()));
    }

    private void actualizarListaProductos() {
        modeloProductosDisponibles.clear();
        listaProductos.forEach(p ->
                modeloProductosDisponibles.addElement(p.getNombre() + " - $" + p.getPrecioBase()));
    }

    private void buscarProductos() {
        String termino = txtBuscarProducto.getText().toLowerCase();
        actualizarListaProductos();
    }

    private void agregarProducto() {
        int indice = listaProductosDisponibles.getSelectedIndex();
        if (indice == -1) return;

        Producto p = listaProductos.get(indice);
        int cantidad = (Integer) spnCantidad.getValue();

        elementosPedido.add(new ItemOrden(p, cantidad, 1));
        actualizarTablaPedido();
    }

    private void actualizarTablaPedido() {
        modeloTablaProductos.setRowCount(0);
        double subtotal = 0;

        for (ItemOrden ep : elementosPedido) {
            double precio = ep.getProducto().getPrecioBase() * ep.getCantidad();
            double totalLinea = precio * ep.getCantidad();
            modeloTablaProductos.addRow(new Object[]{
                    ep.getProducto().getNombre(),
                    "$" + precio,
                    ep.getCantidad(),
                    "$" + totalLinea,
                    "Eliminar"
            });
            subtotal += totalLinea;
        }
        lblSubtotal.setText(String.format("Subtotal: $%.2f", subtotal));
    }

    public boolean validarPedido() {
        // Validación 1: Mesa seleccionada
        if (mesaSeleccionada == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una mesa antes de enviar el pedido",
                    "Mesa no seleccionada",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validación 2: Elementos en el pedido
        if (elementosPedido.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El pedido no contiene productos",
                    "Pedido vacío",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validación 3: Cantidades válidas en todos los items
        for (ItemOrden item : elementosPedido) {
            if (item.getCantidad() <= 0) {
                JOptionPane.showMessageDialog(this,
                        "La cantidad debe ser mayor a cero en todos los productos",
                        "Cantidad inválida",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        try {
            // Obtener mesa y mesero (deberías inyectar el mesero actual)
            EstadoMesaDAO estadoMesaDAO = new EstadoMesaDAO();
            MesaDAO mesaDAO = new MesaDAO(estadoMesaDAO);
            Mesa mesa = mesaDAO.buscarPorId(mesaSeleccionada);

            // Obtener el mesero actual (esto debería venir de la sesión)
            Empleado mesero = obtenerMeseroActual(); // Implementar este metodo

            // Crear la orden
            Orden orden = Orden.builder()
                    .mesa(mesa)
                    .mesero(mesero)
                    .notas("") // Puedes agregar un campo para notas
                    .build();

            // Guardar la orden en la BD
            OrdenDAO ordenDAO = new OrdenDAO();
            orden = ordenDAO.guardar(orden);

            // Guardar los items de la orden
            ItemOrdenDAO itemOrdenDAO = new ItemOrdenDAO();
            for (ItemOrden item : elementosPedido) {
                item.setOrden(orden);
                itemOrdenDAO.guardar(item);
            }

            return true;

        } catch (DAOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el pedido: " + ex.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private Empleado obtenerMeseroActual() {
        return meseroActual;
    }

    public void limpiarPedido() {
        elementosPedido.clear();
        modeloTablaProductos.setRowCount(0);
        lblSubtotal.setText("Subtotal: $0.00");
        cmbMesas.setSelectedIndex(0);
    }

    private void actualizarMesaSeleccionada() {
        String mesa = (String) cmbMesas.getSelectedItem();
        if (mesa != null) {
            mesaSeleccionada = Integer.parseInt(mesa.split(" ")[1]);
        }
    }

    // Clases internas para renderizado de botones
    private class BotonEliminarRenderer extends JButton implements TableCellRenderer {
        public BotonEliminarRenderer() {
            setOpaque(true);
            setBackground(VistaMesero.COLOR_TERRACOTA);
            setForeground(Color.WHITE);
            setBorderPainted(false);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Eliminar");
            return this;
        }
    }

    private class BotonEliminarEditor extends DefaultCellEditor {
        private JButton button;
        private int currentRow;

        public BotonEliminarEditor() {
            super(new JCheckBox());
            button = new JButton("Eliminar");
            button.setBackground(VistaMesero.COLOR_TERRACOTA);
            button.setForeground(Color.WHITE);
            button.setBorderPainted(false);
            button.addActionListener(e -> {
                elementosPedido.remove(currentRow);
                actualizarTablaPedido();
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            return button;
        }
    }
}
