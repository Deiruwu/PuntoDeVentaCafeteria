package com.dei.cafeteria.vista;

import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.dao.ProductoDAO;
import com.dei.cafeteria.dao.CategoriaProductoDAO;
import com.dei.cafeteria.modelo.CategoriaProducto;
import com.dei.cafeteria.modelo.Producto;
import com.dei.cafeteria.util.ColorPaleta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

public class PanelGestionProductos extends JPanel {

    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregar, btnEditar, btnEliminar, btnRefrescar;
    private ProductoDAO productoDAO;
    private CategoriaProductoDAO categoriaProductoDAO;
    private final String SEPARADOR_RUTA = System.getProperty("file.separator");

    public PanelGestionProductos() {
        productoDAO = new ProductoDAO();
        categoriaProductoDAO = new CategoriaProductoDAO();
        inicializarComponentes();
        cargarProductos();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(ColorPaleta.CREMA.getColor());

        // Panel de título
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(ColorPaleta.CREMA.getColor());
        panelTitulo.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitulo = new JLabel("Gestión de Productos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(ColorPaleta.AZUL.getColor());
        panelTitulo.add(lblTitulo, BorderLayout.WEST);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(ColorPaleta.CREMA.getColor());

        btnAgregar = crearBoton("Agregar", ColorPaleta.VERDE.getColor());
        btnEditar = crearBoton("Editar", ColorPaleta.AMBAR.getColor());
        btnEliminar = crearBoton("Eliminar", ColorPaleta.TERRACOTA.getColor());
        btnRefrescar = crearBoton("Refrescar", ColorPaleta.AZUL.getColor());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRefrescar);

        // Configuración de eventos
        btnAgregar.addActionListener(e -> mostrarFormularioProducto(null));
        btnEditar.addActionListener(e -> editarProductoSeleccionado());
        btnEliminar.addActionListener(e -> eliminarProductoSeleccionado());
        btnRefrescar.addActionListener(e -> cargarProductos());

        panelTitulo.add(panelBotones, BorderLayout.EAST);
        add(panelTitulo, BorderLayout.NORTH);

        // Tabla de productos
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Precio Base");
        modeloTabla.addColumn("IVA");
        modeloTabla.addColumn("Categoría");
        modeloTabla.addColumn("Disponible");
        modeloTabla.addColumn("Stock Actual");
        modeloTabla.addColumn("Stock Mínimo");

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setRowHeight(25);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.setBackground(Color.WHITE);
        tablaProductos.setGridColor(new Color(230, 230, 230));
        tablaProductos.getTableHeader().setBackground(ColorPaleta.AZUL.getColor());
        tablaProductos.getTableHeader().setForeground(Color.WHITE);
        tablaProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Configurar selección en tabla
        tablaProductos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    boolean haySeleccion = tablaProductos.getSelectedRow() != -1;
                    btnEditar.setEnabled(haySeleccion);
                    btnEliminar.setEnabled(haySeleccion);
                }
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        scrollTabla.setBorder(BorderFactory.createLineBorder(ColorPaleta.AZUL.getColor(), 1));
        add(scrollTabla, BorderLayout.CENTER);

        // Estado inicial de botones
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    public void cargarProductos() {
        // Limpiar la tabla
        modeloTabla.setRowCount(0);

        // Obtener todos los productos
        List<Producto> productos = null;
        try {
            productos = productoDAO.listarTodos();
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        // Llenar la tabla con datos
        for (Producto producto : productos) {
            Object[] fila = new Object[8];
            fila[0] = producto.getId();
            fila[1] = producto.getNombre();
            fila[2] = producto.getPrecioBase();
            fila[3] = producto.getAplicaIva() ? "Sí" : "No";
            fila[4] = producto.getCategoria() != null ? producto.getCategoria().getNombre() : "Sin categoría";
            fila[5] = producto.getDisponible() ? "Sí" : "No";
            fila[6] = producto.getStockActual();
            fila[7] = producto.getStockMinimo();
            modeloTabla.addRow(fila);
        }

        // Reset botones
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private void mostrarFormularioProducto(Producto producto) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Producto", true);
        dialog.setSize(600, 650);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        FormularioProducto formulario = new FormularioProducto(producto);
        dialog.add(formulario);
        dialog.setVisible(true);
    }

    private void editarProductoSeleccionado() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada != -1) {
            Integer idProducto = (Integer) tablaProductos.getValueAt(filaSeleccionada, 0);
            Producto producto = null;
            try {
                producto = productoDAO.buscarPorId(idProducto);
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }
            if (producto != null) {
                mostrarFormularioProducto(producto);
            }
        }
    }

    private void eliminarProductoSeleccionado() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada != -1) {
            Integer idProducto = (Integer) tablaProductos.getValueAt(filaSeleccionada, 0);
            String nombreProducto = (String) tablaProductos.getValueAt(filaSeleccionada, 1);

            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de eliminar el producto: " + nombreProducto + "?",
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                boolean eliminado = false;
                try {
                    productoDAO.eliminar(idProducto);
                    eliminado = true;
                } catch (DAOException e) {
                    throw new RuntimeException(e);
                }
                if (eliminado) {
                    JOptionPane.showMessageDialog(this, "Producto eliminado correctamente");
                    cargarProductos();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el producto",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Clase interna para el formulario de producto
     */
    private class FormularioProducto extends JPanel {
        private Producto producto;
        private JTextField txtNombre, txtPrecioBase, txtStockActual, txtStockMinimo, txtImagenUrl;
        private JTextArea txtDescripcion;
        private JComboBox<CategoriaProducto> cmbCategoria;
        private JCheckBox chkAplicaIva, chkDisponible;
        private JButton btnGuardar, btnCancelar, btnSeleccionarImagen;
        private File archivoImagen;

        public FormularioProducto(Producto producto) {
            this.producto = producto;
            boolean esNuevo = (producto == null);

            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(20, 20, 20, 20));
            setBackground(ColorPaleta.CREMA.getColor());

            // Panel de título
            JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelTitulo.setBackground(ColorPaleta.CREMA.getColor());
            JLabel lblTitulo = new JLabel(esNuevo ? "Nuevo Producto" : "Editar Producto");
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblTitulo.setForeground(ColorPaleta.AZUL.getColor());
            panelTitulo.add(lblTitulo);

            // Panel de formulario
            JPanel panelFormulario = new JPanel(new GridBagLayout());
            panelFormulario.setBackground(ColorPaleta.CREMA.getColor());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Inicializar componentes
            txtNombre = new JTextField(20);
            txtPrecioBase = new JTextField(10);
            txtDescripcion = new JTextArea(4, 20);
            JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
            txtStockActual = new JTextField(10);
            txtStockMinimo = new JTextField(10);
            txtImagenUrl = new JTextField(20);
            txtImagenUrl.setEditable(false);

            cmbCategoria = new JComboBox<>();
            List<CategoriaProducto> categorias = null;
            try {
                categorias = categoriaProductoDAO.listarTodos();
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }
            DefaultComboBoxModel<CategoriaProducto> modeloCombo = new DefaultComboBoxModel<>();
            for (CategoriaProducto categoria : categorias) {
                modeloCombo.addElement(categoria);
            }
            cmbCategoria.setModel(modeloCombo);

            chkAplicaIva = new JCheckBox("Aplica IVA");
            chkAplicaIva.setBackground(ColorPaleta.CREMA.getColor());
            chkDisponible = new JCheckBox("Disponible");
            chkDisponible.setBackground(ColorPaleta.CREMA.getColor());

            btnSeleccionarImagen = new JButton("Seleccionar imagen");
            btnSeleccionarImagen.setBackground(ColorPaleta.AZUL.getColor());
            btnSeleccionarImagen.setForeground(Color.WHITE);
            btnSeleccionarImagen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    seleccionarImagen();
                }
            });

            // Si estamos editando, cargamos los datos del producto
            if (!esNuevo) {
                txtNombre.setText(producto.getNombre());
                txtPrecioBase.setText(String.valueOf(producto.getPrecioBase()));
                txtDescripcion.setText(producto.getDescripcion());
                txtStockActual.setText(String.valueOf(producto.getStockActual()));
                txtStockMinimo.setText(String.valueOf(producto.getStockMinimo()));
                txtImagenUrl.setText(producto.getImagenUrl());
                chkAplicaIva.setSelected(producto.getAplicaIva());
                chkDisponible.setSelected(producto.getDisponible());

                // Seleccionar la categoría correcta
                if (producto.getCategoria() != null) {
                    for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
                        CategoriaProducto cat = cmbCategoria.getItemAt(i);
                        if (cat.getId().equals(producto.getCategoria().getId())) {
                            cmbCategoria.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } else {
                // Valores por defecto para nuevo producto
                chkDisponible.setSelected(true);
                txtStockActual.setText("0.0");
                txtStockMinimo.setText("5.0");
                txtImagenUrl.setText("/imagenes/productos/default.png");
            }

            // Agregar los componentes al panel
            // Fila 0
            gbc.gridx = 0;
            gbc.gridy = 0;
            panelFormulario.add(new JLabel("Nombre:"), gbc);
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            panelFormulario.add(txtNombre, gbc);

            // Fila 1
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            panelFormulario.add(new JLabel("Precio Base:"), gbc);
            gbc.gridx = 1;
            panelFormulario.add(txtPrecioBase, gbc);
            gbc.gridx = 2;
            panelFormulario.add(chkAplicaIva, gbc);

            // Fila 2
            gbc.gridx = 0;
            gbc.gridy = 2;
            panelFormulario.add(new JLabel("Categoría:"), gbc);
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            panelFormulario.add(cmbCategoria, gbc);

            // Fila 3
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 1;
            panelFormulario.add(new JLabel("Descripción:"), gbc);
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.BOTH;
            panelFormulario.add(scrollDescripcion, gbc);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Fila 4
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 1;
            panelFormulario.add(new JLabel("Stock Actual:"), gbc);
            gbc.gridx = 1;
            panelFormulario.add(txtStockActual, gbc);
            gbc.gridx = 2;
            panelFormulario.add(chkDisponible, gbc);

            // Fila 5
            gbc.gridx = 0;
            gbc.gridy = 5;
            panelFormulario.add(new JLabel("Stock Mínimo:"), gbc);
            gbc.gridx = 1;
            panelFormulario.add(txtStockMinimo, gbc);

            // Fila 6
            gbc.gridx = 0;
            gbc.gridy = 6;
            panelFormulario.add(new JLabel("Imagen:"), gbc);
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            panelFormulario.add(txtImagenUrl, gbc);

            // Fila 7
            gbc.gridx = 1;
            gbc.gridy = 7;
            gbc.gridwidth = 1;
            panelFormulario.add(btnSeleccionarImagen, gbc);

            // Panel de botones
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelBotones.setBackground(ColorPaleta.CREMA.getColor());

            btnGuardar = new JButton("Guardar");
            btnGuardar.setBackground(ColorPaleta.VERDE.getColor());
            btnGuardar.setForeground(Color.WHITE);
            btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnGuardar.addActionListener(e -> guardarProducto());

            btnCancelar = new JButton("Cancelar");
            btnCancelar.setBackground(ColorPaleta.TERRACOTA.getColor());
            btnCancelar.setForeground(Color.WHITE);
            btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnCancelar.addActionListener(e -> ((Window) SwingUtilities.getWindowAncestor(this)).dispose());

            panelBotones.add(btnGuardar);
            panelBotones.add(btnCancelar);

            // Unir todo
            add(panelTitulo, BorderLayout.NORTH);
            add(panelFormulario, BorderLayout.CENTER);
            add(panelBotones, BorderLayout.SOUTH);
        }

        private void seleccionarImagen() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Seleccionar Imagen");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Imagen", "jpg", "jpeg", "png", "gif"));

            int resultado = fileChooser.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                archivoImagen = fileChooser.getSelectedFile();
                try {
                    // Obtener la ruta completa
                    String rutaCompleta = archivoImagen.getAbsolutePath();

                    // Mostrar la ruta en el campo de texto (ruta relativa para la base de datos)
                    String rutaRelativa = obtenerRutaRelativa(rutaCompleta);
                    txtImagenUrl.setText(rutaRelativa);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error al seleccionar la imagen: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private String obtenerRutaRelativa(String rutaCompleta) {
            // Verificar si la ruta contiene una referencia a imágenes/productos
            String rutaEstandarizada = rutaCompleta.replace("\\", "/");
            int indiceImagen = rutaEstandarizada.indexOf("imagenes/productos");

            if (indiceImagen != -1) {
                // Extraer solo la parte desde 'imagenes/productos' en adelante
                return "/" + rutaEstandarizada.substring(indiceImagen);
            } else {
                // Si no encuentra el patrón, devolver solo el nombre del archivo
                // y asumimos que va en la carpeta por defecto
                String nombreArchivo = new File(rutaCompleta).getName();
                return "/imagenes/productos/" + nombreArchivo;
            }
        }

        private void guardarProducto() {
            try {
                // Validaciones
                if (txtNombre.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
                    return;
                }

                double precioBase;
                try {
                    precioBase = Double.parseDouble(txtPrecioBase.getText().trim());
                    if (precioBase < 0) {
                        JOptionPane.showMessageDialog(this, "El precio base no puede ser negativo");
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "El precio base debe ser un número válido");
                    return;
                }

                double stockActual;
                try {
                    stockActual = Double.parseDouble(txtStockActual.getText().trim());
                    if (stockActual < 0) {
                        JOptionPane.showMessageDialog(this, "El stock actual no puede ser negativo");
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "El stock actual debe ser un número válido");
                    return;
                }

                double stockMinimo;
                try {
                    stockMinimo = Double.parseDouble(txtStockMinimo.getText().trim());
                    if (stockMinimo < 0) {
                        JOptionPane.showMessageDialog(this, "El stock mínimo no puede ser negativo");
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "El stock mínimo debe ser un número válido");
                    return;
                }

                // Crear o actualizar el producto
                Producto productoActualizado;
                boolean esNuevo = (producto == null);

                if (esNuevo) {
                    productoActualizado = Producto.builder()
                            .nombre(txtNombre.getText().trim())
                            .precioBase(precioBase)
                            .aplicaIva(chkAplicaIva.isSelected())
                            .descripcion(txtDescripcion.getText().trim())
                            .categoria((CategoriaProducto) cmbCategoria.getSelectedItem())
                            .disponible(chkDisponible.isSelected())
                            .stockActual(stockActual)
                            .stockMinimo(stockMinimo)
                            .imagenUrl(txtImagenUrl.getText().trim())
                            .fechaCreacion(LocalDateTime.now())
                            .fechaActualizacion(LocalDateTime.now())
                            .build();
                } else {
                    productoActualizado = producto;
                    productoActualizado.setNombre(txtNombre.getText().trim());
                    productoActualizado.setPrecioBase(precioBase);
                    productoActualizado.setAplicaIva(chkAplicaIva.isSelected());
                    productoActualizado.setDescripcion(txtDescripcion.getText().trim());
                    productoActualizado.setCategoria((CategoriaProducto) cmbCategoria.getSelectedItem());
                    productoActualizado.setDisponible(chkDisponible.isSelected());
                    productoActualizado.setStockActual(stockActual);
                    productoActualizado.setStockMinimo(stockMinimo);
                    productoActualizado.setImagenUrl(txtImagenUrl.getText().trim());
                    productoActualizado.setFechaActualizacion(LocalDateTime.now());
                }

                boolean guardadoExitoso = false;
                if (esNuevo) {
                    productoDAO.guardar(productoActualizado);
                    guardadoExitoso = true;
                } else {
                    productoDAO.actualizar(productoActualizado);
                    guardadoExitoso = true;

                }

                if (guardadoExitoso) {
                    JOptionPane.showMessageDialog(this, "Producto guardado correctamente");
                    // Cerrar el diálogo
                    ((Window) SwingUtilities.getWindowAncestor(this)).dispose();
                    // Refrescar la tabla de productos
                    cargarProductos();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al guardar el producto",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}