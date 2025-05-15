package com.dei.cafeteria.vista;

import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.dao.OrdenDAO;
import com.dei.cafeteria.dao.ItemOrdenDAO;
import com.dei.cafeteria.dao.PagoDAO;
import com.dei.cafeteria.dao.MetodoPagoDAO;
import com.dei.cafeteria.modelo.Orden;
import com.dei.cafeteria.modelo.ItemOrden;
import com.dei.cafeteria.modelo.Pago;
import com.dei.cafeteria.modelo.MetodoPago;
import com.dei.cafeteria.modelo.Empleado;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.LocalDateTime;
import java.text.DecimalFormat;

/**
 * Panel para procesar el pago de una orden
 */
public class PanelProcesarPago extends JPanel {

    private Empleado cajeroActual;
    private OrdenDAO ordenDAO;
    private ItemOrdenDAO itemOrdenDAO;
    private PagoDAO pagoDAO;
    private MetodoPagoDAO metodoPagoDAO;

    // Componentes de la interfaz
    private JLabel lblOrdenId;
    private JLabel lblMesaNum;
    private JLabel lblFechaCreacion;
    private JLabel lblEstado;
    private JLabel lblSubtotal;
    private JLabel lblIVA;
    private JLabel lblTotal;

    private JTable tablaItems;
    private DefaultTableModel modeloTabla;

    private JComboBox<MetodoPago> cbMetodoPago;
    private JLabel lblMontoRecibido;
    private JTextField txtMontoRecibido;
    private JLabel lblReferencia;
    private JTextField txtReferencia;
    private JLabel lblCambio;
    private JLabel lblCambioValor;

    private JButton btnCalcularCambio;
    private JButton btnProcesarPago;
    private JButton btnImprimirTicket;
    private JButton btnCancelar;

    // Orden actual que se está procesando
    private Orden ordenActual;

    // Formato para mostrar fechas y montos
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private DecimalFormat formatoMoneda = new DecimalFormat("$#,##0.00");

    public PanelProcesarPago(Empleado cajeroActual) {
        this.cajeroActual = cajeroActual;
        ordenDAO = new OrdenDAO();
        itemOrdenDAO = new ItemOrdenDAO();
        pagoDAO = new PagoDAO();
        metodoPagoDAO = new MetodoPagoDAO();

        inicializarComponentes();
        establecerEventos();
    }

    private void inicializarComponentes() {
        // Configurar layout principal con GridBagLayout para mayor flexibilidad
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(VistaCajero.COLOR_CREMA);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Panel de información de la orden
        JPanel panelInfoOrden = new JPanel(new GridLayout(2, 4, 10, 5));
        panelInfoOrden.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(VistaCajero.COLOR_AZUL),
                "Información de la Orden"));
        panelInfoOrden.setOpaque(false);

        // Primera fila de información
        panelInfoOrden.add(new JLabel("Orden #:", SwingConstants.RIGHT));
        lblOrdenId = new JLabel("-");
        lblOrdenId.setFont(lblOrdenId.getFont().deriveFont(Font.BOLD));
        panelInfoOrden.add(lblOrdenId);

        panelInfoOrden.add(new JLabel("Mesa:", SwingConstants.RIGHT));
        lblMesaNum = new JLabel("-");
        panelInfoOrden.add(lblMesaNum);

        // Segunda fila de información
        panelInfoOrden.add(new JLabel("Fecha:", SwingConstants.RIGHT));
        lblFechaCreacion = new JLabel("-");
        panelInfoOrden.add(lblFechaCreacion);

        panelInfoOrden.add(new JLabel("Estado:", SwingConstants.RIGHT));
        lblEstado = new JLabel("-");
        lblEstado.setForeground(VistaCajero.COLOR_TERRACOTA);
        lblEstado.setFont(lblEstado.getFont().deriveFont(Font.BOLD));
        panelInfoOrden.add(lblEstado);

        // Añadir panel de información
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(panelInfoOrden, gbc);

        // Panel de detalles de la orden (tabla de items)
        JPanel panelDetalles = new JPanel(new BorderLayout(0, 10));
        panelDetalles.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(VistaCajero.COLOR_AZUL),
                "Detalles de la Orden"));
        panelDetalles.setOpaque(false);

        // Configurar tabla de items
        String[] columnas = {"ID", "Producto", "Tamaño", "Cantidad", "Precio Unit.", "Subtotal", "IVA", "Total"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No permitir edición directa
            }
        };

        tablaItems = new JTable(modeloTabla);
        tablaItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaItems.getTableHeader().setReorderingAllowed(false);
        tablaItems.getTableHeader().setBackground(VistaCajero.COLOR_AZUL);
        tablaItems.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollTabla = new JScrollPane(tablaItems);
        scrollTabla.setBorder(BorderFactory.createLineBorder(VistaCajero.COLOR_AZUL, 1));
        panelDetalles.add(scrollTabla, BorderLayout.CENTER);

        // Panel para mostrar totales
        JPanel panelTotales = new JPanel(new GridLayout(1, 6, 5, 0));
        panelTotales.setOpaque(false);

        panelTotales.add(new JLabel(""));
        panelTotales.add(new JLabel(""));

        panelTotales.add(new JLabel("Subtotal:", SwingConstants.RIGHT));
        lblSubtotal = new JLabel("$0.00");
        lblSubtotal.setFont(lblSubtotal.getFont().deriveFont(Font.BOLD));
        panelTotales.add(lblSubtotal);

        panelTotales.add(new JLabel("IVA:", SwingConstants.RIGHT));
        lblIVA = new JLabel("$0.00");
        lblIVA.setFont(lblIVA.getFont().deriveFont(Font.BOLD));
        panelTotales.add(lblIVA);

        panelTotales.add(new JLabel("TOTAL:", SwingConstants.RIGHT));
        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));
        lblTotal.setForeground(VistaCajero.COLOR_TERRACOTA);
        panelTotales.add(lblTotal);

        panelDetalles.add(panelTotales, BorderLayout.SOUTH);

        // Añadir panel de detalles
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(panelDetalles, gbc);

        // Panel de pago
        JPanel panelPago = new JPanel(new GridBagLayout());
        panelPago.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(VistaCajero.COLOR_AZUL),
                "Procesar Pago"));
        panelPago.setOpaque(false);

        GridBagConstraints gbcPago = new GridBagConstraints();
        gbcPago.insets = new Insets(5, 10, 5, 10);
        gbcPago.anchor = GridBagConstraints.WEST;

        // Método de pago
        gbcPago.gridx = 0;
        gbcPago.gridy = 0;
        gbcPago.fill = GridBagConstraints.NONE;
        JLabel lblMetodoPago = new JLabel("Método de Pago: *");
        panelPago.add(lblMetodoPago, gbcPago);

        gbcPago.gridx = 1;
        gbcPago.fill = GridBagConstraints.HORIZONTAL;
        gbcPago.weightx = 1.0;
        cbMetodoPago = new JComboBox<>();
        panelPago.add(cbMetodoPago, gbcPago);

        // Monto recibido
        gbcPago.gridx = 0;
        gbcPago.gridy = 1;
        gbcPago.weightx = 0.0;
        gbcPago.fill = GridBagConstraints.NONE;
        lblMontoRecibido = new JLabel("Monto Recibido: *");
        panelPago.add(lblMontoRecibido, gbcPago);

        gbcPago.gridx = 1;
        gbcPago.fill = GridBagConstraints.HORIZONTAL;
        gbcPago.weightx = 1.0;
        txtMontoRecibido = new JTextField();
        panelPago.add(txtMontoRecibido, gbcPago);

        // Referencia (para tarjeta)
        gbcPago.gridx = 0;
        gbcPago.gridy = 2;
        gbcPago.weightx = 0.0;
        gbcPago.fill = GridBagConstraints.NONE;
        lblReferencia = new JLabel("Referencia: *");
        lblReferencia.setVisible(false); // Inicialmente oculto
        panelPago.add(lblReferencia, gbcPago);

        gbcPago.gridx = 1;
        gbcPago.fill = GridBagConstraints.HORIZONTAL;
        gbcPago.weightx = 1.0;
        txtReferencia = new JTextField();
        txtReferencia.setVisible(false); // Inicialmente oculto
        panelPago.add(txtReferencia, gbcPago);

        // Cambio
        gbcPago.gridx = 0;
        gbcPago.gridy = 3;
        gbcPago.weightx = 0.0;
        gbcPago.fill = GridBagConstraints.NONE;
        lblCambio = new JLabel("Cambio:");
        panelPago.add(lblCambio, gbcPago);

        gbcPago.gridx = 1;
        lblCambioValor = new JLabel("$0.00");
        lblCambioValor.setFont(lblCambioValor.getFont().deriveFont(Font.BOLD));
        lblCambioValor.setForeground(VistaCajero.COLOR_VERDE);
        panelPago.add(lblCambioValor, gbcPago);

        // Botón calcular cambio
        gbcPago.gridx = 0;
        gbcPago.gridy = 4;
        gbcPago.gridwidth = 2;
        gbcPago.fill = GridBagConstraints.NONE;
        gbcPago.anchor = GridBagConstraints.CENTER;
        btnCalcularCambio = new JButton("Calcular Cambio");
        estilizarBoton(btnCalcularCambio, VistaCajero.COLOR_VERDE);
        panelPago.add(btnCalcularCambio, gbcPago);

        // Añadir panel de pago
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(panelPago, gbc);

        // Panel de botones de acción
        JPanel panelAcciones = new JPanel(new GridLayout(3, 1, 0, 10));
        panelAcciones.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(VistaCajero.COLOR_AZUL),
                "Acciones"));
        panelAcciones.setOpaque(false);

        btnProcesarPago = new JButton("Procesar Pago");
        estilizarBoton(btnProcesarPago, VistaCajero.COLOR_TERRACOTA);
        btnProcesarPago.setEnabled(false); // Inicialmente deshabilitado

        btnImprimirTicket = new JButton("Imprimir Ticket");
        estilizarBoton(btnImprimirTicket, VistaCajero.COLOR_AZUL);
        btnImprimirTicket.setEnabled(false); // Inicialmente deshabilitado

        btnCancelar = new JButton("Cancelar");
        estilizarBoton(btnCancelar, Color.GRAY);

        panelAcciones.add(btnProcesarPago);
        panelAcciones.add(btnImprimirTicket);
        panelAcciones.add(btnCancelar);

        // Añadir panel de acciones
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        add(panelAcciones, gbc);

        // Cargar métodos de pago
        cargarMetodosPago();
    }

    private void estilizarBoton(JButton boton, Color colorFondo) {
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    }

    private void establecerEventos() {
        // Evento al cambiar método de pago
        cbMetodoPago.addActionListener(e -> {
            MetodoPago metodoPago = (MetodoPago) cbMetodoPago.getSelectedItem();
            if (metodoPago != null && metodoPago.getId() == 2) { // ID 2 = Tarjeta
                lblReferencia.setVisible(true);
                txtReferencia.setVisible(true);
            } else {
                lblReferencia.setVisible(false);
                txtReferencia.setVisible(false);
            }
        });

        // Evento para calcular cambio
        btnCalcularCambio.addActionListener(e -> calcularCambio());

        // Evento para procesar pago
        btnProcesarPago.addActionListener(e -> procesarPago());

        // Evento para imprimir ticket
        btnImprimirTicket.addActionListener(e -> imprimirTicket());

        // Evento para cancelar
        btnCancelar.addActionListener(e -> limpiarPantalla());
    }

    private void cargarMetodosPago() {
        try {
            List<MetodoPago> metodosPago = metodoPagoDAO.listarTodos();
            DefaultComboBoxModel<MetodoPago> model = new DefaultComboBoxModel<>();

            for (MetodoPago metodoPago : metodosPago) {
                model.addElement(metodoPago);
            }

            cbMetodoPago.setModel(model);

            // Seleccionar efectivo por defecto (suponiendo que ID 1 es efectivo)
            for (int i = 0; i < model.getSize(); i++) {
                if (model.getElementAt(i).getId() == 1) {
                    cbMetodoPago.setSelectedIndex(i);
                    break;
                }
            }
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar métodos de pago: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarOrden(int ordenId) {
        try {
            ordenActual = ordenDAO.buscarPorId(ordenId);

            if (ordenActual == null) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró la orden con ID: " + ordenId,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Actualizar información de la orden
            lblOrdenId.setText(String.valueOf(ordenActual.getId()));
            lblMesaNum.setText(String.valueOf(ordenActual.getMesaId()));
            lblFechaCreacion.setText(ordenActual.getFechaCreacion().format(formatter));

            // Actualizar estado
            String estado = obtenerNombreEstado(ordenActual.getEstadoId());
            lblEstado.setText(estado);

            // Cargar items de la orden
            cargarItemsOrden(ordenId);

            // Actualizar totales
            actualizarTotales();

            // Habilitar botones según el estado
            actualizarBotonesPorEstado();

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar la orden: " + e.getMessage(),
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

    private void cargarItemsOrden(int ordenId) {
        try {
            // Limpiar tabla
            modeloTabla.setRowCount(0);

            // Cargar items --************************+ ALERTA CUIDADO NO SË COMO DEBERIA FUNCIONAR
            ItemOrden item = itemOrdenDAO.buscarPorId(ordenId);


                Object[] fila = {
                        item.getId(),
                        item.getProducto().getNombre(),
                        item.getTamaño().getNombre(),
                        item.getCantidad(),
                        formatoMoneda.format(item.getPrecioUnitario()),
                        formatoMoneda.format(item.getSubtotal()),
                        formatoMoneda.format(item.getIva()),
                        formatoMoneda.format(item.getTotal())
                };
                modeloTabla.addRow(fila);

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar los items de la orden: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTotales() {
        if (ordenActual != null) {
            lblSubtotal.setText(formatoMoneda.format(ordenActual.getSubtotal()));
            lblIVA.setText(formatoMoneda.format(ordenActual.getIva()));
            lblTotal.setText(formatoMoneda.format(ordenActual.getTotal()));
        }
    }

    private void actualizarBotonesPorEstado() {
        if (ordenActual == null) {
            btnProcesarPago.setEnabled(false);
            btnImprimirTicket.setEnabled(false);
            return;
        }

        // Si la orden está en estado ENTREGADA (4), permitir procesar pago
        if (ordenActual.getEstadoId() == 4) {
            btnProcesarPago.setEnabled(true);
            btnImprimirTicket.setEnabled(false);
        }
        // Si la orden está en estado PAGADA (5), permitir imprimir ticket
        else if (ordenActual.getEstadoId() == 5) {
            btnProcesarPago.setEnabled(false);
            btnImprimirTicket.setEnabled(true);
        } else {
            btnProcesarPago.setEnabled(false);
            btnImprimirTicket.setEnabled(false);
        }
    }

    private void calcularCambio() {
        if (ordenActual == null) {
            return;
        }

        try {
            double montoRecibido = Double.parseDouble(txtMontoRecibido.getText().replace("$", "").replace(",", ""));
            double total = ordenActual.getTotal();

            if (montoRecibido < total) {
                JOptionPane.showMessageDialog(this,
                        "El monto recibido debe ser igual o mayor al total",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            double cambio = montoRecibido - total;
            lblCambioValor.setText(formatoMoneda.format(cambio));

            // Habilitar botón de procesar pago si todo está correcto
            MetodoPago metodoPago = (MetodoPago) cbMetodoPago.getSelectedItem();
            if (metodoPago != null) {
                if (metodoPago.getId() == 2) { // Tarjeta
                    btnProcesarPago.setEnabled(!txtReferencia.getText().trim().isEmpty());
                } else { // Efectivo
                    btnProcesarPago.setEnabled(true);
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese un monto válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void procesarPago() {
        if (ordenActual == null || ordenActual.getEstadoId() != 4) {
            JOptionPane.showMessageDialog(this,
                    "Solo se pueden procesar pagos de órdenes en estado ENTREGADA",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        MetodoPago metodoPago = (MetodoPago) cbMetodoPago.getSelectedItem();
        if (metodoPago == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un método de pago",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar campos según el método de pago
        if (metodoPago.getId() == 2 && txtReferencia.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "La referencia es obligatoria para pagos con tarjeta",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double montoRecibido = Double.parseDouble(txtMontoRecibido.getText().replace("$", "").replace(",", ""));

            if (montoRecibido < ordenActual.getTotal()) {
                JOptionPane.showMessageDialog(this,
                        "El monto recibido debe ser igual o mayor al total",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Crear y guardar el pago
            Pago pago = Pago.builder()
                    .orden(ordenActual)
                    .metodoPago(metodoPago)
                    .monto(montoRecibido)
                    .cambio(montoRecibido - ordenActual.getTotal())
                    .fechaHora(LocalDateTime.now())
                    .cajero(cajeroActual).build();


            // Si es tarjeta, guardar referencia
            if (metodoPago.getId() == 2) {
                pago.setReferencia(txtReferencia.getText().trim());
            }

            // Guardar pago
            pagoDAO.guardar(pago);

            // Actualizar orden a estado PAGADA (5)
            ordenActual.getEstado().setId(5); // PAGADA
            ordenDAO.actualizar(ordenActual);

            // Recargar orden actualizada
            cargarOrden(ordenActual.getId());

            JOptionPane.showMessageDialog(this,
                    "Pago procesado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            // Preguntar si desea imprimir el ticket
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Desea imprimir el ticket?",
                    "Imprimir Ticket",
                    JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                imprimirTicket();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese un monto válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al procesar el pago: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void imprimirTicket() {
        if (ordenActual == null) {
            return;
        }

        try {
            // Generar un número de ticket (orden ID + timestamp)
            String numeroTicket = ordenActual.getId() + "_" + System.currentTimeMillis();

            // Obtener información del pago
            Pago pago = pagoDAO.buscarPorId(ordenActual.getId());

            if (pago == null) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró información de pago para esta orden",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear ticket usando la utilidad GeneradorPDF
            /*
            GeneradorPDF generador = new GeneradorPDF();
            String rutaPDF = generador.generarTicket(
                    numeroTicket,
                    ordenActual,
                    itemOrdenDAO.buscarPorOrdenId(ordenActual.getId()),
                    pago,
                    cajeroActual);

            JOptionPane.showMessageDialog(this,
                    "Ticket generado exitosamente: " + rutaPDF,
                    "Ticket Generado",
                    JOptionPane.INFORMATION_MESSAGE); */

            // Opcional: Abrir el PDF
            try {
                //Desktop.getDesktop().open(new java.io.File(rutaPDF));
            } catch (Exception e) {
                System.out.println("No se pudo abrir el archivo PDF: " + e.getMessage());
            }

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el ticket: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarPantalla() {
        // Limpiar información de la orden
        ordenActual = null;
        lblOrdenId.setText("-");
        lblMesaNum.setText("-");
        lblFechaCreacion.setText("-");
        lblEstado.setText("-");

        // Limpiar tabla
        modeloTabla.setRowCount(0);

        // Limpiar totales
        lblSubtotal.setText("$0.00");
        lblIVA.setText("$0.00");
        lblTotal.setText("$0.00");

        // Limpiar campos de pago
        txtMontoRecibido.setText("");
        txtReferencia.setText("");
        lblCambioValor.setText("$0.00");

        // Deshabilitar botones
        btnProcesarPago.setEnabled(false);
        btnImprimirTicket.setEnabled(false);
    }

    public void actualizarOrdenSeleccionada() {
        // Este metodo se llama cuando se muestra el panel
        // Si hay una orden seleccionada (ordenActual != null), recargarla
        if (ordenActual != null) {
            cargarOrden(ordenActual.getId());
        }
    }
}