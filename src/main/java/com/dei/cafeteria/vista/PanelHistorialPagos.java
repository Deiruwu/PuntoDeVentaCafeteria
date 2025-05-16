package com.dei.cafeteria.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.dao.OrdenDAO;
import com.dei.cafeteria.dao.PagoDAO;
import com.dei.cafeteria.dao.ItemOrdenDAO;
import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.modelo.Pago;
import com.dei.cafeteria.modelo.Orden;
import com.dei.cafeteria.modelo.ItemOrden;
import com.dei.cafeteria.util.GeneradorPDF;

/**
 * Panel que muestra el historial de pagos y permite filtrarlos por fecha
 */
public class PanelHistorialPagos extends JPanel {

    // DAOs
    private PagoDAO pagoDAO;
    private OrdenDAO ordenDAO;

    private Empleado cajeroActual;

    // Componentes de la UI
    private JLabel lblTitulo;
    private JPanel panelFiltros;
    private JLabel lblFechaInicio;
    private JLabel lblFechaFin;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JButton btnFiltrar;
    private JButton btnHoy;
    private JButton btnSemana;
    private JButton btnMes;
    private JTable tablaPagos;
    private DefaultTableModel modeloTablaPagos;
    private JScrollPane scrollTablaPagos;
    private JPanel panelDetalle;
    private JButton btnVerDetalles;
    private JButton btnImprimirTicket;

    public PanelHistorialPagos(Empleado cajeroActual) {
        this.pagoDAO = new PagoDAO();
        this.ordenDAO = new OrdenDAO();
        this.cajeroActual = cajeroActual;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(VistaCajero.COLOR_CREMA);

        inicializarComponentes();
        establecerEventos();
    }

    private void inicializarComponentes() {
        // Panel de título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTitulo.setBackground(VistaCajero.COLOR_CREMA);
        lblTitulo = new JLabel("Historial de Pagos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(VistaCajero.COLOR_AZUL);
        panelTitulo.add(lblTitulo);

        // Panel de filtros
        panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBackground(VistaCajero.COLOR_CREMA);
        panelFiltros.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(VistaCajero.COLOR_AZUL),
                "Filtros",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                VistaCajero.COLOR_AZUL));

        lblFechaInicio = new JLabel("Fecha Inicio:");
        lblFechaInicio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblFechaInicio.setForeground(VistaCajero.COLOR_AZUL);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaHoy = sdf.format(new Date());

        txtFechaInicio = new JTextField(fechaHoy);
        txtFechaInicio.setPreferredSize(new Dimension(120, 30));
        txtFechaInicio.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        lblFechaFin = new JLabel("Fecha Fin:");
        lblFechaFin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblFechaFin.setForeground(VistaCajero.COLOR_AZUL);

        txtFechaFin = new JTextField(fechaHoy);
        txtFechaFin.setPreferredSize(new Dimension(120, 30));
        txtFechaFin.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnFiltrar = new JButton("Filtrar");
        estilizarBoton(btnFiltrar, VistaCajero.COLOR_AZUL, Color.WHITE);

        btnHoy = new JButton("Hoy");
        estilizarBoton(btnHoy, VistaCajero.COLOR_VERDE, Color.WHITE);

        btnSemana = new JButton("Semana");
        estilizarBoton(btnSemana, VistaCajero.COLOR_VERDE, Color.WHITE);

        btnMes = new JButton("Mes");
        estilizarBoton(btnMes, VistaCajero.COLOR_VERDE, Color.WHITE);

        panelFiltros.add(lblFechaInicio);
        panelFiltros.add(txtFechaInicio);
        panelFiltros.add(lblFechaFin);
        panelFiltros.add(txtFechaFin);
        panelFiltros.add(btnFiltrar);
        panelFiltros.add(btnHoy);
        panelFiltros.add(btnSemana);
        panelFiltros.add(btnMes);

        // Inicializar tabla de pagos
        String[] columnas = {
                "ID Pago", "ID Orden", "Fecha", "Método", "Total", "Monto Recibido", "Cambio", "Referencia"
        };

        modeloTablaPagos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPagos = new JTable(modeloTablaPagos);
        tablaPagos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaPagos.setRowHeight(25);
        tablaPagos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPagos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaPagos.getTableHeader().setBackground(VistaCajero.COLOR_AZUL);
        tablaPagos.getTableHeader().setForeground(Color.WHITE);

        // Crear sorter para permitir ordenamiento
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTablaPagos);
        tablaPagos.setRowSorter(sorter);

        scrollTablaPagos = new JScrollPane(tablaPagos);
        scrollTablaPagos.setBorder(BorderFactory.createLineBorder(VistaCajero.COLOR_AZUL));

        // Panel de detalles y botones de acción
        panelDetalle = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelDetalle.setBackground(VistaCajero.COLOR_CREMA);

        btnVerDetalles = new JButton("Ver Detalles");
        estilizarBoton(btnVerDetalles, VistaCajero.COLOR_AZUL, Color.WHITE);

        btnImprimirTicket = new JButton("Imprimir Ticket");
        estilizarBoton(btnImprimirTicket, VistaCajero.COLOR_TERRACOTA, Color.WHITE);

        panelDetalle.add(btnVerDetalles);
        panelDetalle.add(btnImprimirTicket);

        // Agregar todo al panel principal
        add(panelTitulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBackground(VistaCajero.COLOR_CREMA);
        panelCentral.add(panelFiltros, BorderLayout.NORTH);
        panelCentral.add(scrollTablaPagos, BorderLayout.CENTER);
        panelCentral.add(panelDetalle, BorderLayout.SOUTH);

        add(panelCentral, BorderLayout.CENTER);
    }

    private void establecerEventos() {
        // Evento para filtrar por rango de fechas
        btnFiltrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarHistorialPagos();
            }
        });

        // Eventos para los botones de filtros rápidos
        btnHoy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String hoy = sdf.format(new Date());
                txtFechaInicio.setText(hoy);
                txtFechaFin.setText(hoy);
                cargarHistorialPagos();
            }
        });

        btnSemana.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para establecer fechas de la semana actual
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(java.util.Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                txtFechaInicio.setText(sdf.format(cal.getTime()));

                cal.add(java.util.Calendar.DAY_OF_WEEK, 6);
                txtFechaFin.setText(sdf.format(cal.getTime()));

                cargarHistorialPagos();
            }
        });

        btnMes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para establecer fechas del mes actual
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                txtFechaInicio.setText(sdf.format(cal.getTime()));

                cal.set(java.util.Calendar.DAY_OF_MONTH, cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
                txtFechaFin.setText(sdf.format(cal.getTime()));

                cargarHistorialPagos();
            }
        });

        // Evento para ver detalles de un pago
        btnVerDetalles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDetallesPago();
            }
        });

        // Evento para imprimir ticket
        btnImprimirTicket.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarTicketPDF();
            }
        });
    }

    public void cargarHistorialPagos() {
        try {
            // Limpiar la tabla
            modeloTablaPagos.setRowCount(0);

            // Obtener fechas de los campos de texto
            String fechaInicio = txtFechaInicio.getText().trim();
            String fechaFin = txtFechaFin.getText().trim();

            // Validar formato de fechas
            if (!fechaInicio.matches("\\d{4}-\\d{2}-\\d{2}") || !fechaFin.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this,
                        "Formato de fecha inválido. Use YYYY-MM-DD",
                        "Error de validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Convertir a timestamp agregando horas
            String fechaInicioConHora = fechaInicio + " 00:00:00";
            String fechaFinConHora = fechaFin + " 23:59:59";

            // Validar orden de fechas
            if (fechaInicioConHora.compareTo(fechaFinConHora) > 0) {
                JOptionPane.showMessageDialog(this,
                        "La fecha inicial no puede ser mayor a la fecha final",
                        "Error de validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Consultar pagos por rango de fechas
            List<Pago> pagos = pagoDAO.buscarPorRangoFechas(fechaInicioConHora, fechaFinConHora);

            // Llenar la tabla con los resultados (igual que antes)
            for (Pago pago : pagos) {
                LocalDateTime ldt = pago.getFechaCreacion(); // tu LocalDateTime
                Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String fechaFormateada = sdf.format(date);

                String metodoPago = pago.getMetodoPagoId() == 1 ? "Efectivo" : "Tarjeta";

                Object[] fila = {
                        pago.getId(),
                        pago.getOrdenId(),
                        fechaFormateada,
                        metodoPago,
                        String.format("$%.2f", pago.getTotal()),
                        String.format("$%.2f", pago.getMonto() != null ? pago.getMonto() : 0.00),
                        String.format("$%.2f", pago.getCambio()),
                        pago.getReferencia() != null ? pago.getReferencia() : "-"
                };
                modeloTablaPagos.addRow(fila);
            }

            // Mensaje si no hay resultados
            if (pagos.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se encontraron pagos en el rango de fechas seleccionado.",
                        "Sin resultados",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar el historial de pagos: " + e.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Muestra los detalles de un pago seleccionado
     */
    private void mostrarDetallesPago() {
        int filaSeleccionada = tablaPagos.getSelectedRow();

        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un pago para ver sus detalles.",
                    "Selección requerida",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Convertir índice de fila en caso de que la tabla esté ordenada
        int filaModelo = tablaPagos.convertRowIndexToModel(filaSeleccionada);

        // Obtener ID del pago y de la orden
        int pagoId = Integer.parseInt(modeloTablaPagos.getValueAt(filaModelo, 0).toString());
        int ordenId = Integer.parseInt(modeloTablaPagos.getValueAt(filaModelo, 1).toString());

        try {
            // Buscar pago y orden en la base de datos
            Pago pago = pagoDAO.buscarPorId(pagoId);

            // Crear y mostrar diálogo de detalles
            DialogoDetallePago dialogo = new DialogoDetallePago(SwingUtilities.getWindowAncestor(this), pago);
            dialogo.setVisible(true);

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar los detalles del pago: " + e.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Genera un ticket en PDF para el pago seleccionado
     */
    private void generarTicketPDF() {
        int filaSeleccionada = tablaPagos.getSelectedRow();

        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un pago para generar el ticket.",
                    "Selección requerida",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Convertir índice de fila en caso de que la tabla esté ordenada
        int filaModelo = tablaPagos.convertRowIndexToModel(filaSeleccionada);

        // Obtener ID del pago
        int pagoId = Integer.parseInt(modeloTablaPagos.getValueAt(filaModelo, 0).toString());

        try {
            // Buscar pago en la base de datos
            Pago pago = pagoDAO.buscarPorId(pagoId);

            // Generar PDF utilizando la clase utilitaria
            GeneradorPDF generador = new GeneradorPDF();
            String rutaPDF = generador.generarTicket(pago);

            // Mostrar confirmación y abrir el archivo
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "Ticket generado correctamente en: " + rutaPDF + "\n¿Desea abrirlo ahora?",
                    "Ticket generado",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(new java.io.File(rutaPDF));
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "No se puede abrir el archivo automáticamente en este sistema.",
                                "Aviso",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error al abrir el archivo: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el ticket: " + e.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el PDF: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void estilizarBoton(JButton boton, Color colorFondo, Color colorTexto) {
        boton.setBackground(colorFondo);
        boton.setForeground(colorTexto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Clase interna para el diálogo de detalle de pago
     */
    private class DialogoDetallePago extends JDialog {

        public DialogoDetallePago(Window owner, Pago pago) {
            super(owner, "Detalle de Pago", ModalityType.APPLICATION_MODAL);
            setSize(500, 500);
            setLocationRelativeTo(owner);

            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(new EmptyBorder(20, 20, 20, 20));
            panel.setBackground(VistaCajero.COLOR_CREMA);

            // Título
            JLabel lblTitulo = new JLabel("Detalle del Pago #" + pago.getId());
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblTitulo.setForeground(VistaCajero.COLOR_AZUL);
            panel.add(lblTitulo, BorderLayout.NORTH);

            // Panel de información
            JPanel panelInfo = new JPanel(new GridLayout(0, 2, 10, 10));
            panelInfo.setBackground(VistaCajero.COLOR_CREMA);

            agregarCampoDetalle(panelInfo, "ID Pago:", String.valueOf(pago.getId()));
            agregarCampoDetalle(panelInfo, "ID Orden:", String.valueOf(pago.getOrdenId()));

            LocalDateTime ldt = pago.getFechaCreacion();
            Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fechaFormateada = sdf.format(date);
            agregarCampoDetalle(panelInfo, "Fecha:", fechaFormateada);

            String metodoPago = pago.getMetodoPagoId() == 1 ? "Efectivo" : "Tarjeta";
            agregarCampoDetalle(panelInfo, "Método de Pago:", metodoPago);

            agregarCampoDetalle(panelInfo, "Total:", String.format("$%.2f", pago.getTotal()));
            agregarCampoDetalle(panelInfo, "Monto Recibido:", String.format("$%.2f", pago.getMonto()));
            agregarCampoDetalle(panelInfo, "Cambio:", String.format("$%.2f", pago.getCambio()));

            if (pago.getMetodoPagoId() == 2) { // Si es tarjeta
                agregarCampoDetalle(panelInfo, "Referencia:", pago.getReferencia());
            }

            // Intentar cargar detalles de la orden
            try {
                // Aquí iría el código para cargar los ítems de la orden
                // usando itemOrdenDAO.buscarPorOrdenId(pago.getOrdenId())

                // Este es un placeholder. En una implementación real, cargarías los ítems
                // y los mostrarías en una tabla o lista
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error al cargar los detalles de la orden: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            JScrollPane scrollPanel = new JScrollPane(panelInfo);
            scrollPanel.setBorder(null);
            panel.add(scrollPanel, BorderLayout.CENTER);

            // Botón de cerrar
            JButton btnCerrar = new JButton("Cerrar");
            btnCerrar.setBackground(VistaCajero.COLOR_AZUL);
            btnCerrar.setForeground(Color.WHITE);
            btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnCerrar.addActionListener(e -> dispose());

            JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelBoton.setBackground(VistaCajero.COLOR_CREMA);
            panelBoton.add(btnCerrar);
            panel.add(panelBoton, BorderLayout.SOUTH);

            setContentPane(panel);
        }

        private void agregarCampoDetalle(JPanel panel, String etiqueta, String valor) {
            JLabel lblEtiqueta = new JLabel(etiqueta);
            lblEtiqueta.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblEtiqueta.setForeground(VistaCajero.COLOR_AZUL);

            JLabel lblValor = new JLabel(valor != null ? valor : "-");
            lblValor.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            panel.add(lblEtiqueta);
            panel.add(lblValor);
        }
    }
}