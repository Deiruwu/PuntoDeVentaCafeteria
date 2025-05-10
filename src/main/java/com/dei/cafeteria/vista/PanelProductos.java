package com.dei.cafeteria.vista;

import com.dei.cafeteria.controlador.ControladorProductos;
import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.modelo.Producto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel para mostrar y buscar productos
 */
class PanelProductos extends JPanel {

    private JPanel panelSuperior;
    private JPanel panelBotonesCategorias;
    private JPanel panelListaProductos;
    private JScrollPane scrollPane;
    private JLabel lblTitulo;
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JButton btnTodos;
    private JButton btnBebidasCalientes;
    private JButton btnBebidasFrias;
    private JButton btnPostres;
    private JButton btnPlatosprincipales;
    private JButton btnSnacks;

    private JButton categoriaSeleccionada;

    // Lista simulada de productos (en producción se obtendría del DAO)
    private ControladorProductos controladorProductos;
    private List<Producto> listaProductos;
    private List<Producto> listaProductosFiltrada = new ArrayList<>();

    // IDs de categorías - Constantes para evitar "magic numbers"
    private static final Integer CATEGORIA_BEBIDAS_CALIENTES = 1;
    private static final Integer CATEGORIA_BEBIDAS_FRIAS = 2;
    private static final Integer CATEGORIA_POSTRES = 3;
    private static final Integer CATEGORIA_PLATOSPP = 4;
    private static final Integer CATEGORIA_SNACKS = 5;

    public PanelProductos() {
        this.controladorProductos = new ControladorProductos();
        setLayout(new BorderLayout());
        setBackground(VistaMesero.COLOR_CREMA);

        inicializarComponentes();
        cargarProductosDesdeControlador();
    }

    private void inicializarComponentes() {
        // Panel superior con título y búsqueda
        panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(VistaMesero.COLOR_CREMA);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Título
        lblTitulo = new JLabel("Catálogo de Productos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(VistaMesero.COLOR_AZUL);

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBusqueda.setBackground(VistaMesero.COLOR_CREMA);

        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, VistaMesero.COLOR_AZUL),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBuscar.setBackground(VistaMesero.COLOR_AMBAR);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorderPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);

        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        panelSuperior.add(panelBusqueda, BorderLayout.EAST);

        // Panel de botones para categorías
        panelBotonesCategorias = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelBotonesCategorias.setBackground(VistaMesero.COLOR_CREMA);
        panelBotonesCategorias.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        // Inicializar los botones de categorías
        btnTodos = crearBotonCategoria("Todos");
        btnBebidasCalientes = crearBotonCategoria("Bebidas Calientes");
        btnBebidasFrias = crearBotonCategoria("Bebidas Frías");
        btnPostres = crearBotonCategoria("Postres");
        btnPlatosprincipales = crearBotonCategoria("Platos Principales");
        btnSnacks = crearBotonCategoria("Snacks");

        // Agregar los botones al panel
        panelBotonesCategorias.add(btnTodos);
        panelBotonesCategorias.add(btnBebidasCalientes);
        panelBotonesCategorias.add(btnBebidasFrias);
        panelBotonesCategorias.add(btnPostres);
        panelBotonesCategorias.add(btnPlatosprincipales);
        panelBotonesCategorias.add(btnSnacks);

        // Panel para la lista de productos
        panelListaProductos = new JPanel();
        panelListaProductos.setLayout(new BoxLayout(panelListaProductos, BoxLayout.Y_AXIS));
        panelListaProductos.setBackground(VistaMesero.COLOR_CREMA);
        panelListaProductos.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // ScrollPane para la lista de productos
        scrollPane = new JScrollPane(panelListaProductos);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Añadir componentes al panel principal
        add(panelSuperior, BorderLayout.NORTH);
        add(panelBotonesCategorias, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Configurar evento de búsqueda
        btnBuscar.addActionListener(e -> buscarProductos());
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarProductos();
                }
            }
        });

        // Configurar eventos para los botones de categorías
        btnTodos.addActionListener(e -> filtrarPorCategoria(null));
        btnBebidasCalientes.addActionListener(e -> filtrarPorCategoria(CATEGORIA_BEBIDAS_CALIENTES));
        btnBebidasFrias.addActionListener(e -> filtrarPorCategoria(CATEGORIA_BEBIDAS_FRIAS));
        btnPostres.addActionListener(e -> filtrarPorCategoria(CATEGORIA_POSTRES));
        btnPlatosprincipales.addActionListener(e -> filtrarPorCategoria(CATEGORIA_PLATOSPP));
        btnSnacks.addActionListener(e -> filtrarPorCategoria(CATEGORIA_SNACKS));

        // Establecer el botón "Todos" como seleccionado inicialmente
        categoriaSeleccionada = btnTodos;
        actualizarEstiloBotonSeleccionado();
    }

    private JButton crearBotonCategoria(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        boton.setForeground(VistaMesero.COLOR_AZUL);
        boton.setBackground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return boton;
    }

    private void actualizarEstiloBotonSeleccionado() {
        // Resetear todos los botones
        for (Component comp : panelBotonesCategorias.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setBackground(Color.WHITE);
                btn.setForeground(VistaMesero.COLOR_AZUL);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
        }

        // Destacar el botón seleccionado
        categoriaSeleccionada.setBackground(VistaMesero.COLOR_AZUL);
        categoriaSeleccionada.setForeground(Color.WHITE);
        categoriaSeleccionada.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VistaMesero.COLOR_AZUL, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }

    private void cargarProductosDesdeControlador() {
        try {
            List<Producto> productos = controladorProductos.obtenerTodosLosProductos();
            listaProductos = new ArrayList<>(productos);
            listaProductosFiltrada = new ArrayList<>(productos);
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar los productos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            listaProductos = new ArrayList<>();
        }
        actualizarVistaProductos();
    }

    private void buscarProductos() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();

        if (textoBusqueda.isEmpty() && categoriaSeleccionada == btnTodos) {
            // Si no hay texto de búsqueda y la categoría es "Todos", mostramos todos los productos
            listaProductosFiltrada = new ArrayList<>(listaProductos);
        } else {
            // Filtrar por nombre de producto
            listaProductosFiltrada = listaProductos.stream()
                    .filter(p -> textoBusqueda.isEmpty() ||
                            p.getNombre().toLowerCase().contains(textoBusqueda))
                    .toList();

            // Si hay una categoría seleccionada diferente a "Todos", aplicamos ese filtro también
            if (categoriaSeleccionada != btnTodos) {
                Integer categoriaId = obtenerCategoriaIdDesdeBoton(categoriaSeleccionada);
                if (categoriaId != null) {
                    listaProductosFiltrada = listaProductosFiltrada.stream()
                            .filter(p -> p.getCategoria() != null &&
                                    p.getCategoria().getId().equals(categoriaId))
                            .toList();
                }
            }
        }

        actualizarVistaProductos();
    }

    private void filtrarPorCategoria(Integer categoriaId) {
        // Actualizar el botón seleccionado
        if (categoriaId == null) {
            categoriaSeleccionada = btnTodos;
        } else if (categoriaId.equals(CATEGORIA_BEBIDAS_CALIENTES)) {
            categoriaSeleccionada = btnBebidasCalientes;
        } else if (categoriaId.equals(CATEGORIA_BEBIDAS_FRIAS)) {
            categoriaSeleccionada = btnBebidasFrias;
        } else if (categoriaId.equals(CATEGORIA_POSTRES)) {
            categoriaSeleccionada = btnPostres;
        } else if (categoriaId.equals(CATEGORIA_PLATOSPP)) {
            categoriaSeleccionada = btnPlatosprincipales;
        } else if (categoriaId.equals(CATEGORIA_SNACKS)) {
            categoriaSeleccionada = btnSnacks;
        }

        actualizarEstiloBotonSeleccionado();

        try {
            if (categoriaId == null) {
                // Mostrar todos los productos si no hay categoría seleccionada
                listaProductosFiltrada = new ArrayList<>(listaProductos);
            } else {
                // Usar el DAO para filtrar por categoría
                listaProductosFiltrada = controladorProductos.buscarPorCategoriaId(categoriaId);
            }

            // Si hay texto en el campo de búsqueda, aplicar ese filtro también
            String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
            if (!textoBusqueda.isEmpty()) {
                listaProductosFiltrada = listaProductosFiltrada.stream()
                        .filter(p -> p.getNombre().toLowerCase().contains(textoBusqueda))
                        .toList();
            }

            actualizarVistaProductos();
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al filtrar productos por categoría: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Integer obtenerCategoriaIdDesdeBoton(JButton boton) {
        if (boton == btnBebidasCalientes) return CATEGORIA_BEBIDAS_CALIENTES;
        if (boton == btnBebidasFrias) return CATEGORIA_BEBIDAS_FRIAS;
        if (boton == btnPostres) return CATEGORIA_POSTRES;
        if (boton == btnPlatosprincipales) return CATEGORIA_PLATOSPP;
        if (boton == btnSnacks) return CATEGORIA_SNACKS;
        return null; // Si es el botón "Todos" o no coincide con ninguna categoría
    }

    private void actualizarVistaProductos() {
        panelListaProductos.removeAll();

        // Agrupar por categoría
        String categoriaActual = "";

        for (Producto producto : listaProductosFiltrada) {
            // Verifica si la categoría es nula y asigna un valor predeterminado si lo es
            String categoriaNombre = (producto.getCategoria() != null && producto.getCategoria().getNombre() != null)
                    ? producto.getCategoria().getNombre()
                    : "Categoría desconocida";

            // Ahora puedes comparar sin problemas
            if (!categoriaNombre.equalsIgnoreCase(categoriaActual)) {
                categoriaActual = categoriaNombre;
                JLabel lblCategoria = new JLabel(categoriaActual);
                lblCategoria.setFont(new Font("Segoe UI", Font.BOLD, 18));
                lblCategoria.setForeground(VistaMesero.COLOR_AZUL);
                lblCategoria.setBorder(BorderFactory.createEmptyBorder(15, 5, 10, 0));
                lblCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);

                panelListaProductos.add(lblCategoria);
            }

            panelListaProductos.add(crearPanelProducto(producto));
        }

        // Si no hay productos
        if (listaProductosFiltrada.isEmpty()) {
            JLabel lblNoResultados = new JLabel("No se encontraron productos");
            lblNoResultados.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            lblNoResultados.setForeground(VistaMesero.COLOR_AZUL);
            lblNoResultados.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblNoResultados.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

            panelListaProductos.add(Box.createVerticalGlue());
            panelListaProductos.add(lblNoResultados);
            panelListaProductos.add(Box.createVerticalGlue());
        }

        panelListaProductos.revalidate();
        panelListaProductos.repaint();
    }

    private JPanel crearPanelProducto(Producto producto) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Panel izquierdo (nombre y descripción)
        JPanel panelInfo = new JPanel(new GridLayout(2, 1));
        panelInfo.setBackground(Color.WHITE);

        JLabel lblNombre = new JLabel(producto.getNombre());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombre.setForeground(VistaMesero.COLOR_AZUL);

        JLabel lblDescripcion = new JLabel(producto.getDescripcion());
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDescripcion.setForeground(Color.GRAY);

        panelInfo.add(lblNombre);
        panelInfo.add(lblDescripcion);

        // Panel derecho (precio)
        JPanel panelPrecio = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelPrecio.setBackground(Color.WHITE);

        JLabel lblPrecio = new JLabel(String.format("$%.2f", producto.getPrecioBase()));
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPrecio.setForeground(VistaMesero.COLOR_AMBAR);

        panelPrecio.add(lblPrecio);

        // Añadir componentes al panel principal
        panel.add(panelInfo, BorderLayout.WEST);
        panel.add(panelPrecio, BorderLayout.EAST);

        // Efecto hover
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(245, 245, 245));
                panelInfo.setBackground(new Color(245, 245, 245));
                panelPrecio.setBackground(new Color(245, 245, 245));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE);
                panelInfo.setBackground(Color.WHITE);
                panelPrecio.setBackground(Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Aquí se podría abrir un diálogo con más detalles
                // o agregar directamente al pedido
                JOptionPane.showMessageDialog(panel,
                        "Producto: " + producto.getNombre() + "\n" +
                                "Descripción: " + producto.getDescripcion() + "\n" +
                                "Precio: $" + producto.getPrecioBase() + "\n" +
                                "Categoría: " + producto.getCategoria().getNombre(),
                        "Detalles del Producto",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }
}