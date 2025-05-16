package com.dei.cafeteria.vista;

import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.dao.OrdenDAO;
import com.dei.cafeteria.modelo.Orden;
import com.dei.cafeteria.modelo.EstadoOrden;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel que muestra las órdenes pendientes de pago (estado ENTREGADA)
 */
public class PanelOrdenesPendientes extends JPanel {

    private OrdenDAO ordenDAO;
    private JTable tablaOrdenes;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JButton btnRefrescar;
    private JButton btnSeleccionar;
    private JComboBox<String> cbFiltroEstado;

    // Formato para mostrar fechas
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PanelOrdenesPendientes() {
        ordenDAO = new OrdenDAO();
        inicializarComponentes();
        establecerEventos();
        cargarOrdenesPendientes();
    }

    private void inicializarComponentes() {
        // Configurar layout
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel superior para filtros y búsqueda
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 0));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelSuperior.setOpaque(false);

        // Panel para búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setOpaque(false);

        JLabel lblBuscar = new JLabel("Buscar:");
        txtBuscar = new JTextField(15);
        btnRefrescar = new JButton("Refrescar");
        estilizarBoton(btnRefrescar, VistaCajero.COLOR_VERDE);

        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnRefrescar);

        // Panel para filtro de estado
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelFiltro.setOpaque(false);

        JLabel lblFiltro = new JLabel("Estado:");
        cbFiltroEstado = new JComboBox<>(new String[]{"Todos", "ENTREGADA", "PENDIENTE", "EN PREPARACIÓN"});
        cbFiltroEstado.setSelectedItem("ENTREGADA"); // Por defecto, filtrar por órdenes entregadas

        panelFiltro.add(lblFiltro);
        panelFiltro.add(cbFiltroEstado);

        panelSuperior.add(panelBusqueda, BorderLayout.WEST);
        panelSuperior.add(panelFiltro, BorderLayout.EAST);

        // Configurar tabla de órdenes
        String[] columnas = {"Orden", "Mesa", "Mesero", "Estado", "Total", "Fecha Creación", "Fecha Actualización"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No permitir edición directa
            }
        };

        tablaOrdenes = new JTable(modeloTabla);
        tablaOrdenes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaOrdenes.getTableHeader().setReorderingAllowed(false);
        tablaOrdenes.getTableHeader().setBackground(VistaCajero.COLOR_AZUL);
        tablaOrdenes.getTableHeader().setForeground(Color.WHITE);

        // Configurar sorter para permitir ordenamiento
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        tablaOrdenes.setRowSorter(sorter);

        JScrollPane scrollTabla = new JScrollPane(tablaOrdenes);
        scrollTabla.setBorder(BorderFactory.createLineBorder(VistaCajero.COLOR_AZUL, 1));

        // Panel inferior para botones de acción
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.setOpaque(false);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        btnSeleccionar = new JButton("Procesar Pago");
        estilizarBoton(btnSeleccionar, VistaCajero.COLOR_TERRACOTA);
        panelInferior.add(btnSeleccionar);

        // Añadir componentes al panel principal
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void estilizarBoton(JButton boton, Color colorFondo) {
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void establecerEventos() {
        btnRefrescar.addActionListener(e -> cargarOrdenesPendientes());

        txtBuscar.addActionListener(e -> filtrarOrdenes());

        cbFiltroEstado.addActionListener(e -> cargarOrdenesPendientes());

        btnSeleccionar.addActionListener(e -> procesarPagoOrdenSeleccionada());

        // Filtrar al escribir en el campo de búsqueda
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filtrarOrdenes();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filtrarOrdenes();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filtrarOrdenes();
            }
        });
    }

    public void cargarOrdenesPendientes() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);

        try {
            List<Orden> ordenes;
            String filtroEstado = (String) cbFiltroEstado.getSelectedItem();

            // Aplicar filtro si no es "Todos"
            if (!"Todos".equals(filtroEstado)) {
                // Convertir nombre del estado a ID según la clase EstadoOrden
                int estadoId = 0;
                switch (filtroEstado) {
                    case "PENDIENTE":
                        estadoId = 1;
                        break;
                    case "EN PREPARACIÓN":
                        estadoId = 2;
                        break;
                    case "LISTA":
                        estadoId = 3;
                        break;
                    case "ENTREGADA":
                        estadoId = 4;
                        break;
                    case "PAGADA":
                        estadoId = 5;
                        break;
                }
                ordenes = ordenDAO.buscarPorEstado(estadoId);
            } else {
                ordenes = ordenDAO.listarTodos();
            }

            // Llenar tabla con las órdenes
            for (Orden orden : ordenes) {
                Object[] fila = {
                        orden.getId(),
                        orden.getMesaId(),
                        orden.getMesero(),
                        obtenerNombreEstado(orden.getEstadoId()),
                        String.format("$%.2f", orden.getTotal()),
                        orden.getFechaCreacion().format(formatter),
                        orden.getFechaActualizacion().format(formatter)
                };
                modeloTabla.addRow(fila);
            }

            // Aplicar filtro de búsqueda si hay texto
            if (!txtBuscar.getText().isEmpty()) {
                filtrarOrdenes();
            }

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar las órdenes: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String obtenerNombreEstado(int estadoId) {
        switch (estadoId) {
            case 1: return "PENDIENTE";
            case 2: return "EN PREPARACIÓN";
            case 3: return "LISTA";
            case 4: return "ENTREGADA";
            case 5: return "PAGADA";
            default: return "DESCONOCIDO";
        }
    }

    private void filtrarOrdenes() {
        String textoBusqueda = txtBuscar.getText().toLowerCase().trim();

        if (textoBusqueda.isEmpty()) {
            tablaOrdenes.setRowSorter(new TableRowSorter<>(modeloTabla));
            return;
        }

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        tablaOrdenes.setRowSorter(sorter);

        // Filtrar según el texto de búsqueda (por id, mesa o total)
        RowFilter<DefaultTableModel, Object> rf = RowFilter.orFilter(List.of(
                RowFilter.regexFilter("(?i)" + textoBusqueda, 0), // ID
                RowFilter.regexFilter("(?i)" + textoBusqueda, 1), // Mesa
                RowFilter.regexFilter("(?i)" + textoBusqueda, 4)  // Total
        ));

        sorter.setRowFilter(rf);
    }

    private void procesarPagoOrdenSeleccionada() {
        int filaSeleccionada = tablaOrdenes.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una orden para procesar el pago",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Obtener el ID de la orden seleccionada
        int ordenId = (int) tablaOrdenes.getValueAt(filaSeleccionada, 0);

        // Verificar que la orden esté en estado ENTREGADA
        String estado = (String) tablaOrdenes.getValueAt(filaSeleccionada, 3);
        if (!"ENTREGADA".equals(estado)) {
            JOptionPane.showMessageDialog(this,
                    "Solo se pueden procesar pagos de órdenes ENTREGADAS",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener la instancia de PanelProcesarPago para cargar la orden
        Container container = getParent();
        if (container instanceof JPanel) {
            CardLayout cl = (CardLayout) container.getLayout();

            // Buscar el panel de pago
            Component[] components = container.getComponents();
            for (Component component : components) {
                if (component instanceof PanelProcesarPago) {
                    PanelProcesarPago panelPago = (PanelProcesarPago) component;
                    panelPago.cargarOrden(ordenId);

                    // Mostrar el panel de pagos
                    cl.show(container, "pagos");
                    break;
                }
            }
        }
    }
}