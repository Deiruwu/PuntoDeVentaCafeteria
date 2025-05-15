package com.dei.cafeteria.vista;

import com.dei.cafeteria.controlador.ControladorProductos;
import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.modelo.Producto;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
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
        // Agregar placeholder
        TextPrompt placeholder = new TextPrompt("Buscar por nombre de producto...", txtBuscar);
        placeholder.changeAlpha(0.5f); // Transparencia del texto
        placeholder.changeStyle(Font.ITALIC); // Estilo itálica

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
        panelBotonesCategorias = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 5));
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

        // Panel principal para organizar los componentes
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(VistaMesero.COLOR_CREMA);

        // Añadir el panel de botones al panel principal
        panelPrincipal.add(panelBotonesCategorias, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Añadir componentes al panel principal
        add(panelSuperior, BorderLayout.NORTH);
        add(panelPrincipal, BorderLayout.CENTER);

        // Configurar evento de búsqueda
        btnBuscar.addActionListener(e -> buscarProductos());

        // Implementar patrón Observer para actualización inmediata
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                buscarProductos();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                buscarProductos();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                buscarProductos();
            }
        });

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
                BorderFactory.createEmptyBorder(5, 20, 5, 20)
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
                        BorderFactory.createEmptyBorder(5, 8, 5, 8)
                ));
            }
        }

        // Destacar el botón seleccionado
        categoriaSeleccionada.setBackground(VistaMesero.COLOR_AZUL);
        categoriaSeleccionada.setForeground(Color.WHITE);
        categoriaSeleccionada.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VistaMesero.COLOR_AZUL, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
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
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); // Aumentado para acomodar la imagen
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Panel para la imagen con efecto de zoom
        JLayeredPane panelImagenZoom = new JLayeredPane();
        panelImagenZoom.setPreferredSize(new Dimension(80, 80));

        JPanel panelImagen = new JPanel(new BorderLayout());
        panelImagen.setBackground(Color.WHITE);
        panelImagen.setBounds(0, 0, 80, 80);
        panelImagen.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240), 1));

        // Cargar la imagen del producto
        JLabel lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(JLabel.CENTER);
        lblImagen.setVerticalAlignment(JLabel.CENTER);

        // Para el efecto de zoom
        final JLabel lblImagenZoom = new JLabel();
        lblImagenZoom.setHorizontalAlignment(JLabel.CENTER);
        lblImagenZoom.setVerticalAlignment(JLabel.CENTER);
        lblImagenZoom.setVisible(false);

        // Panel para la imagen ampliada
        JPanel panelZoom = new JPanel(new BorderLayout());
        panelZoom.setOpaque(false);
        panelZoom.setBounds(-50, -50, 180, 180); // Panel más grande para mostrar la imagen ampliada
        panelZoom.setVisible(false);
        panelZoom.add(lblImagenZoom, BorderLayout.CENTER);

        BufferedImage originalImage = null;
        boolean tieneImagen = false;

        try {
            // Procesar la URL de la imagen
            String rutaImagen = producto.getImagenUrl();
            if (rutaImagen != null && !rutaImagen.trim().isEmpty()) {
                // Convertir a ruta absoluta si es necesario
                File imgFile = obtenerArchivoImagen(rutaImagen);

                if (imgFile.exists()) {
                    // Cargar la imagen original
                    originalImage = ImageIO.read(imgFile);
                    if (originalImage != null) {
                        tieneImagen = true;
                        // Imagen para visualización normal
                        Image scaledImage = originalImage.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                        lblImagen.setIcon(new ImageIcon(scaledImage));

                        // Imagen para efecto de zoom
                        Image zoomedImage = originalImage.getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                        lblImagenZoom.setIcon(new ImageIcon(zoomedImage));
                    }
                } else {
                    // Imagen no encontrada, mostrar un placeholder
                    lblImagen.setText("Sin imagen");
                    lblImagen.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                    lblImagen.setForeground(Color.GRAY);
                }
            } else {
                // No hay URL de imagen
                lblImagen.setText("Sin imagen");
                lblImagen.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                lblImagen.setForeground(Color.GRAY);
            }
        } catch (Exception e) {
            e.printStackTrace();
            lblImagen.setText("Error");
            lblImagen.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblImagen.setForeground(Color.RED);
        }

        // Si hay imagen, configurar el efecto de zoom
        if (tieneImagen) {
            // Agregar efecto de zoom al pasar el ratón
            panelImagen.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    panelZoom.setVisible(true);
                    panelImagen.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    panelZoom.setVisible(false);
                    panelImagen.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });

            // Efecto de seguimiento del cursor
            panelImagen.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (panelZoom.isVisible()) {
                        // Calcular posición del zoom basada en la posición del ratón
                        // Ajustar para que la imagen ampliada siga fluídamente el cursor
                        int x = e.getX() - panelZoom.getWidth() / 2;
                        int y = e.getY() - panelZoom.getHeight() / 2;

                        // Añadir un poco de suavizado para evitar movimientos bruscos
                        int smoothX = Math.max(-60, Math.min(x, 20));
                        int smoothY = Math.max(-60, Math.min(y, 20));

                        panelZoom.setBounds(smoothX, smoothY, 180, 180);
                    }
                }
            });
        }

        panelImagen.add(lblImagen, BorderLayout.CENTER);

        // Añadir componentes al panel con capas
        panelImagenZoom.add(panelImagen, JLayeredPane.DEFAULT_LAYER);
        panelImagenZoom.add(panelZoom, JLayeredPane.POPUP_LAYER);

        // Efecto de sombra para el panel de zoom
        if (tieneImagen) {
            panelZoom.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 0, 0, 100), 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        }

        // Panel central (nombre y descripción)
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
        panel.add(panelImagenZoom, BorderLayout.WEST);
        panel.add(panelInfo, BorderLayout.CENTER);
        panel.add(panelPrecio, BorderLayout.EAST);

        // Efecto hover
        MouseAdapter hoverListener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(245, 245, 245));
                panelInfo.setBackground(new Color(245, 245, 245));
                panelPrecio.setBackground(new Color(245, 245, 245));
                if (panelImagen != null) panelImagen.setBackground(new Color(245, 245, 245));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE);
                panelInfo.setBackground(Color.WHITE);
                panelPrecio.setBackground(Color.WHITE);
                if (panelImagen != null) panelImagen.setBackground(Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Crear un panel personalizado para mostrar la imagen más grande junto con los detalles
                JPanel customPanel = new JPanel(new BorderLayout(15, 10));
                customPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                // Panel para la imagen grande
                JPanel imagenGrandePanel = new JPanel(new BorderLayout());
                JLabel imagenGrande = new JLabel();

                try {
                    String rutaImagen = producto.getImagenUrl();
                    if (rutaImagen != null && !rutaImagen.trim().isEmpty()) {
                        File imgFile = obtenerArchivoImagen(rutaImagen);

                        if (imgFile.exists()) {
                            BufferedImage originalImage = ImageIO.read(imgFile);
                            if (originalImage != null) {
                                // Mostrar imagen más grande pero mantener proporciones razonables
                                int maxWidth = 200;
                                int maxHeight = 200;
                                int width = originalImage.getWidth();
                                int height = originalImage.getHeight();

                                double scale = Math.min((double)maxWidth / width, (double)maxHeight / height);
                                int scaledWidth = (int)(width * scale);
                                int scaledHeight = (int)(height * scale);

                                Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                                imagenGrande.setIcon(new ImageIcon(scaledImage));
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                imagenGrandePanel.add(imagenGrande, BorderLayout.CENTER);

                // Panel para los detalles
                JPanel detallesPanel = new JPanel();
                detallesPanel.setLayout(new BoxLayout(detallesPanel, BoxLayout.Y_AXIS));

                JLabel nombreDetalle = new JLabel("Producto: " + producto.getNombre());
                nombreDetalle.setFont(new Font("Segoe UI", Font.BOLD, 14));
                JLabel descDetalle = new JLabel("Descripción: " + producto.getDescripcion());
                JLabel precioDetalle = new JLabel(String.format("Precio: $%.2f", producto.getPrecioBase()));
                JLabel catDetalle = new JLabel("Categoría: " + producto.getCategoria().getNombre());

                detallesPanel.add(nombreDetalle);
                detallesPanel.add(Box.createVerticalStrut(5));
                detallesPanel.add(descDetalle);
                detallesPanel.add(Box.createVerticalStrut(5));
                detallesPanel.add(precioDetalle);
                detallesPanel.add(Box.createVerticalStrut(5));
                detallesPanel.add(catDetalle);

                customPanel.add(imagenGrandePanel, BorderLayout.WEST);
                customPanel.add(detallesPanel, BorderLayout.CENTER);

                JOptionPane.showMessageDialog(panel,
                        customPanel,
                        "Detalles del Producto",
                        JOptionPane.PLAIN_MESSAGE);
            }
        };

        panel.addMouseListener(hoverListener);

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    /**
     * Obtiene un objeto File para la imagen, convirtiendo a ruta absoluta si es necesario.
     * Siempre trata de usar rutas relativas al directorio del programa.
     *
     * @param rutaImagen La URL o ruta de la imagen
     * @return El objeto File que representa la imagen
     */
    private File obtenerArchivoImagen(String rutaImagen) {
        String subruta;

        int index = rutaImagen.indexOf("imagenes");
        if (index != -1) {
            subruta = rutaImagen.substring(index); // Solo la parte desde "imagenes/..."
        } else {
            // Si no contiene "imagenes", asumir que es solo el nombre del archivo
            subruta = "imagenes" + File.separator + new File(rutaImagen).getName();
        }

        return new File(subruta);
    }

}