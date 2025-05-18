package com.dei.cafeteria.vista;

import com.dei.cafeteria.controlador.ControladorMesas;
import com.dei.cafeteria.controlador.ControladorProductos;
import com.dei.cafeteria.dao.*;
import com.dei.cafeteria.modelo.*;
import com.dei.cafeteria.util.ColorPaleta;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel para tomar órdenes/pedidos
 */
public class PanelTomarOrden extends JPanel {

    private Empleado meseroActual;

    // Componentes de la interfaz
    private JComboBox<String> cmbMesas;
    private JTextField txtBuscarProducto;
    private JList<Producto> listaProductosDisponibles; // Cambiado a JList<Producto>
    private DefaultListModel<Producto> modeloProductosDisponibles; // Cambiado a DefaultListModel<Producto>
    private JSpinner spnCantidad;
    private JTable tablaProductosOrden;
    private DefaultTableModel modeloTablaProductos;
    private JLabel lblSubtotal;
    private JLabel lblStockDisponible;

    // Datos simulados
    private List<Mesa> listaMesas;
    private List<Producto> listaProductos;
    private Map<String, ItemOrden> elementosPedido; // Clave: "idProducto-idTamaño"
    private List<String> clavesPedido; // Para mapear filas a claves
    private int mesaSeleccionada;

    public PanelTomarOrden(Empleado meseroActual) {
        this.meseroActual = meseroActual;
        setLayout(new BorderLayout());
        setBackground(ColorPaleta.CREMA.getColor());
        elementosPedido = new HashMap<>();
        clavesPedido = new ArrayList<>();


        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        // Panel superior con título y selección de mesa
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(ColorPaleta.CREMA.getColor());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("Tomar Orden");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(ColorPaleta.AZUL.getColor());

        JPanel panelMesa = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelMesa.setBackground(ColorPaleta.CREMA.getColor());

        JLabel lblSeleccionMesa = new JLabel("Mesa:");
        lblSeleccionMesa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSeleccionMesa.setForeground(ColorPaleta.AZUL.getColor());

        cmbMesas = new JComboBox<>();
        cmbMesas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbMesas.setPreferredSize(new Dimension(150, 30));
        cmbMesas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPaleta.AZUL.getColor()),
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
        panelBusqueda.setBackground(ColorPaleta.CREMA.getColor());

        txtBuscarProducto = new JTextField();
        txtBuscarProducto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPaleta.AZUL.getColor()),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        txtBuscarProducto.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBuscar.setBackground(ColorPaleta.AMBAR.getColor());
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setBorderPainted(false);
        btnBuscar.setFocusPainted(false);

        panelBusqueda.add(txtBuscarProducto, BorderLayout.CENTER);
        panelBusqueda.add(btnBuscar, BorderLayout.EAST);

        // Cambio del modelo y JList para usar objetos Producto directamente
        modeloProductosDisponibles = new DefaultListModel<>();
        listaProductosDisponibles = new JList<>(modeloProductosDisponibles);
        listaProductosDisponibles.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listaProductosDisponibles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Renderer personalizado para mostrar nombre, precio y stock
        listaProductosDisponibles.setCellRenderer(new ProductoListCellRenderer());

        JScrollPane scrollProductos = new JScrollPane(listaProductosDisponibles);
        scrollProductos.setBorder(null);

        panelProductos.add(panelBusqueda, BorderLayout.NORTH);
        panelProductos.add(scrollProductos, BorderLayout.CENTER);

        // Panel de información de stock y cantidad - CORREGIDO: Mejor espaciado
        JPanel panelInfoAgregar = new JPanel(new BorderLayout(10, 0)); // Agregado espacio horizontal
        panelInfoAgregar.setBackground(ColorPaleta.CREMA.getColor());
        panelInfoAgregar.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5)); // Agregado padding

        // Panel para mostrar el stock disponible
        JPanel panelStock = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelStock.setBackground(ColorPaleta.CREMA.getColor());

        lblStockDisponible = new JLabel("Stock disponible: -");
        lblStockDisponible.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStockDisponible.setForeground(ColorPaleta.AZUL.getColor());
        lblStockDisponible.setPreferredSize(new Dimension(180, 30)); // Asegurar tamaño fijo

        panelStock.add(lblStockDisponible);

        // Panel de cantidad y agregar
        JPanel panelAgregar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // Aumentado el espacio
        panelAgregar.setBackground(ColorPaleta.CREMA.getColor());

        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCantidad.setForeground(ColorPaleta.AZUL.getColor());

        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        spnCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spnCantidad.setPreferredSize(new Dimension(60, 30));

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnAgregar.setBackground(ColorPaleta.VERDE.getColor());
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setBorderPainted(false);
        btnAgregar.setFocusPainted(false);

        panelAgregar.add(lblCantidad);
        panelAgregar.add(spnCantidad);
        panelAgregar.add(btnAgregar);

        panelInfoAgregar.add(panelStock, BorderLayout.WEST);
        panelInfoAgregar.add(panelAgregar, BorderLayout.EAST);

        panelProductos.add(panelInfoAgregar, BorderLayout.SOUTH);

        // Panel de pedido
        JPanel panelPedido = new JPanel(new BorderLayout());
        panelPedido.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTablaProductos = new DefaultTableModel(
                new Object[]{"Producto", "Tamaño", "Precio", "Cantidad", "Subtotal", ""}, 0) {            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Solo la columna del botón eliminar es editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) {
                    return Button.class; // La columna 5 es de tipo botón
                }
                return Object.class;
            }
        };

        tablaProductosOrden = new JTable(modeloTablaProductos);
        tablaProductosOrden.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaProductosOrden.getTableHeader().setBackground(ColorPaleta.CREMA.getColor());
        tablaProductosOrden.setRowHeight(30);
        tablaProductosOrden.setShowGrid(false);

        tablaProductosOrden.getColumnModel().getColumn(5).setCellRenderer(new BotonEliminarRenderer());
        tablaProductosOrden.getColumnModel().getColumn(5).setCellEditor(new BotonEliminarEditor(tablaProductosOrden));

        JScrollPane scrollPedido = new JScrollPane(tablaProductosOrden);
        scrollPedido.setBorder(null);

        // Panel subtotal
        JPanel panelSubtotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSubtotal.setBackground(ColorPaleta.CREMA.getColor());

        lblSubtotal = new JLabel("Subtotal: $0.00");
        lblSubtotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSubtotal.setForeground(ColorPaleta.TERRACOTA.getColor());

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

        // Evento para mostrar el stock cuando se selecciona un producto
        listaProductosDisponibles.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Producto productoSeleccionado = listaProductosDisponibles.getSelectedValue();
                if (productoSeleccionado != null) {
                    double stock = productoSeleccionado.getStockActual();

                    lblStockDisponible.setText("Stock disponible: " + stock);

                    SpinnerNumberModel modelo = (SpinnerNumberModel) spnCantidad.getModel();

                    // Asegurar mínimo 1 y máximo stock (o 1 si stock es 0 o menor)
                    int nuevoMaximo = (int) Math.max(1, stock);
                    modelo.setMaximum(nuevoMaximo);

                    // Si el valor actual excede el nuevo máximo, ajustarlo
                    int valorActual = (Integer) modelo.getValue();
                    if (valorActual > nuevoMaximo) {
                        modelo.setValue(nuevoMaximo);
                    }
                } else {
                    lblStockDisponible.setText("Stock disponible: -");
                }
            }
        });
    }

    private void cargarDatos() {
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
        listaProductos.stream()
                .filter(p -> p.getStockActual() > 0) // Solo mostrar productos con stock
                .forEach(modeloProductosDisponibles::addElement);
    }

    private void buscarProductos() {
        String termino = txtBuscarProducto.getText().toLowerCase();
        modeloProductosDisponibles.clear();

        listaProductos.stream()
                .filter(p -> p.getStockActual() > 0)
                .filter(p -> p.getNombre().toLowerCase().contains(termino))
                .forEach(modeloProductosDisponibles::addElement);
    }

    private void agregarProducto() {
        Producto productoSeleccionado = listaProductosDisponibles.getSelectedValue();
        if (productoSeleccionado == null) return;

        int cantidad = (Integer) spnCantidad.getValue();

        // Validar stock
        if (cantidad > productoSeleccionado.getStockActual()) {
            JOptionPane.showMessageDialog(this,
                    "Stock insuficiente. Disponible: " + productoSeleccionado.getStockActual(),
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Crear diálogo para selección de tamaño y notas
        JPanel panelDialogo = new JPanel(new GridLayout(0, 1));

        // Configurar opciones de tamaño según categoría
        CategoriaProducto categoria = productoSeleccionado.getCategoria();
        List<TamañoProducto> tamanosDisponibles = new ArrayList<>();
        TamañoProductoDAO tamanoDAO = new TamañoProductoDAO();

        try {
            if (categoria.getId() == 1 || categoria.getId() == 2) { // Bebidas
                tamanosDisponibles.add(tamanoDAO.buscarPorId(1));
                tamanosDisponibles.add(tamanoDAO.buscarPorId(2));
                tamanosDisponibles.add(tamanoDAO.buscarPorId(3));
            } else if (categoria.getId() == 3) { // Postres
                tamanosDisponibles.add(tamanoDAO.buscarPorId(4));
                tamanosDisponibles.add(tamanoDAO.buscarPorId(5));
            } else {
                tamanosDisponibles.add(tamanoDAO.buscarPorId(1));
            }
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        JComboBox<TamañoProducto> cmbTamanos = new JComboBox<>(new DefaultComboBoxModel<>(tamanosDisponibles.toArray(new TamañoProducto[0])));
        JTextField txtNotas = new JTextField(20);

        if (tamanosDisponibles.size() > 1) {
            panelDialogo.add(new JLabel("Tamaño:"));
            panelDialogo.add(cmbTamanos);
        }

        panelDialogo.add(new JLabel("Notas:"));
        panelDialogo.add(txtNotas);

        int resultado = JOptionPane.showConfirmDialog(this, panelDialogo,
                "Seleccione opciones", JOptionPane.OK_CANCEL_OPTION);

        if (resultado != JOptionPane.OK_OPTION) return;

        TamañoProducto tamanoSeleccionado = (TamañoProducto) cmbTamanos.getSelectedItem();
        String notas = txtNotas.getText();

        // Crear clave única para el mapa
        String clave = productoSeleccionado.getId() + "-" + tamanoSeleccionado;

        // Verificar si ya existe el mismo producto con el mismo tamaño
        ItemOrden itemExistente = elementosPedido.get(clave);

        if (elementosPedido.containsKey(clave)) {
            // Actualizar cantidad si ya existe
            ItemOrden existente = elementosPedido.get(clave);
            existente.setCantidad(existente.getCantidad() + cantidad);
            itemExistente.setNotas(notas);
        } else {
            // Crear nuevo ítem
            ItemOrden nuevoItem = ItemOrden.builder()
                    .producto(productoSeleccionado)
                    .cantidad( (double) cantidad)
                    .tamaño(tamanoSeleccionado)
                    .notas(notas)
                    .build();

            elementosPedido.put(clave, nuevoItem);
        }

        actualizarTablaPedido();
    }

    private void actualizarTablaPedido() {
        modeloTablaProductos.setRowCount(0);
        clavesPedido.clear();
        double subtotal = 0;


        for (Map.Entry<String, ItemOrden> entry : elementosPedido.entrySet()) {
            ItemOrden item = entry.getValue();
            double precio = item.getProducto().getPrecioBase() * item.getTamaño().getFactorPrecio();
            double subtotalLinea = precio * item.getCantidad();

            modeloTablaProductos.addRow(new Object[]{
                    item.getProducto(),
                    item.getTamaño().getNombre(),
                    String.format("$%.2f", precio),
                    item.getCantidad(),
                    String.format("$%.2f", subtotalLinea),
                    "Eliminar"
            });
            clavesPedido.add(entry.getKey()); // Guardar clave para referencia
            subtotal += subtotalLinea;
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
        for (ItemOrden item : elementosPedido.values()) {
            if (item.getCantidad() <= 0) {
                JOptionPane.showMessageDialog(this,
                        "La cantidad debe ser mayor a cero en todos los productos",
                        "Cantidad inválida",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Validación 4: Stock suficiente
            if (item.getCantidad() > item.getProducto().getStockActual()) {
                JOptionPane.showMessageDialog(this,
                        "No hay suficiente stock de " + item.getProducto().getNombre() +
                                ". Stock disponible: " + item.getProducto().getStockActual(),
                        "Stock insuficiente",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        try {
            EstadoMesaDAO estadoMesaDAO = new EstadoMesaDAO();
            MesaDAO mesaDAO = new MesaDAO(estadoMesaDAO);

            Mesa mesa = mesaDAO.buscarPorId(mesaSeleccionada);
            Empleado mesero = obtenerMeseroActual();

            // Crear la orden
            Orden orden = Orden.builder()
                    .mesa(mesa)
                    .mesero(mesero)
                    .notas("")
                    .build();

            OrdenDAO ordenDAO = new OrdenDAO();
            orden = ordenDAO.guardar(orden);

            // Guardar los items de la orden
            ItemOrdenDAO itemOrdenDAO = new ItemOrdenDAO();
            TamañoProductoDAO tamañoProductoDAO = new TamañoProductoDAO();
            // IMPORTANTE: Crear NUEVOS objetos ItemOrden para evitar duplicaciones
            for (ItemOrden itemOriginal : elementosPedido.values()) {
                // Los valores como precio unitario serán generados por los triggers de la BD
                int tamañoProducto = itemOriginal.getTamaño().getId();

                ItemOrden nuevoItem = ItemOrden.builder()
                        .orden(itemOriginal.getOrden())
                        .producto(itemOriginal.getProducto())
                        .tamaño(tamañoProductoDAO.buscarPorId(tamañoProducto))
                        .cantidad(itemOriginal.getCantidad())
                        .notas(itemOriginal.getNotas()).build();
                // Establecer la orden
                nuevoItem.setOrden(orden);

                // Guardar el nuevo item (permitiendo que los triggers de la BD hagan su trabajo)
                itemOrdenDAO.guardar(nuevoItem);
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

    private void eliminarProductoDePedido(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < clavesPedido.size()) {
            String clave = clavesPedido.get(rowIndex);
            elementosPedido.remove(clave);
            actualizarTablaPedido();
        }
    }

    // Renderer personalizado para mostrar productos con su stock
    private class ProductoListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Producto) {
                Producto producto = (Producto) value;
                setText(producto.getNombre() + " - $" + producto.getPrecioBase());
            }

            return this;
        }
    }

    // Clases internas para renderizado de botones - CORREGIDA
    private class BotonEliminarRenderer extends JButton implements TableCellRenderer {
        public BotonEliminarRenderer() {
            setOpaque(true);
            setBackground(ColorPaleta.TERRACOTA.getColor());
            setForeground(Color.WHITE);
            setBorderPainted(false);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Eliminar");
            return this;
        }
    }

    // CORREGIDO: Implementación del editor de botón eliminar
    private class BotonEliminarEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isPushed;
        private JTable tabla;
        private int currentRow;

        public BotonEliminarEditor(JTable tabla) {
            super(new JCheckBox());
            this.tabla = tabla;
            button = new JButton("Eliminar");
            button.setBackground(ColorPaleta.TERRACOTA.getColor());
            button.setForeground(Color.WHITE);
            button.setBorderPainted(false);
            button.setFocusPainted(false);

            button.addActionListener(e -> {
                fireEditingStopped();
                // Al hacer clic en el botón, se elimina el producto de la lista
                eliminarProductoDePedido(currentRow);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            isPushed = true;
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return "Eliminar";
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}