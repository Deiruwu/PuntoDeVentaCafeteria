package com.dei.cafeteria.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que extiende JTable para crear una tabla con diseño mejorado
 */
public class TablaPersonalizada extends JTable {
    // Colores para el tema
    private static final Color COLOR_CREMA = new Color(255, 253, 241);
    private static final Color COLOR_TERRACOTA = new Color(192, 77, 46);
    private static final Color COLOR_VERDE = new Color(56, 142, 60);
    private static final Color COLOR_AZUL = new Color(41, 98, 255);
    private static final Color COLOR_AMBAR = new Color(216, 144, 0);

    // Colores para la tabla
    private static final Color COLOR_FILA_PAR = new Color(240, 240, 245);
    private static final Color COLOR_FILA_IMPAR = new Color(255, 255, 255);
    private static final Color COLOR_HOVER = new Color(230, 237, 247);
    private static final Color COLOR_SELECCION = new Color(197, 217, 241);
    private static final Color COLOR_CABECERA_INICIO = new Color(61, 118, 225);
    private static final Color COLOR_CABECERA_FIN = new Color(41, 98, 255);

    // Iconos para estados
    private final ImageIcon iconoActivo;
    private final ImageIcon iconoInactivo;
    private final ImageIcon iconoBloqueado;

    // Mapa para iconos de columnas
    private final Map<Integer, ImageIcon> iconosColumnas = new HashMap<>();

    // Fila sobre la que está el mouse
    private int filaHover = -1;

    /**
     * Constructor de la tabla personalizada
     */
    public TablaPersonalizada(TableModel mt) {
        super(mt);
        configurarTabla();

        // Cargar iconos (aquí se usarían rutas reales a los iconos)
        iconoActivo = crearIconoColor(COLOR_VERDE, 12);
        iconoInactivo = crearIconoColor(COLOR_AMBAR, 12);
        iconoBloqueado = crearIconoColor(COLOR_TERRACOTA, 12);

        // Configurar el efecto hover
        configurarHover();
    }

    /**
     * Configura los aspectos básicos de la tabla
     */
    private void configurarTabla() {
        // Configuración general
        setRowHeight(30);
        setIntercellSpacing(new Dimension(5, 0));
        setShowVerticalLines(false);
        setShowHorizontalLines(true);
        setGridColor(new Color(220, 220, 220));
        setSelectionBackground(COLOR_SELECCION);
        setSelectionForeground(Color.BLACK);
        setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Configurar cabecera
        JTableHeader header = getTableHeader();
        header.setDefaultRenderer(new GradientHeaderRenderer());
        header.setReorderingAllowed(false);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 35));

        // Configurar selección
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Configurar el renderizador de celdas
        setDefaultRenderer(Object.class, new CeldaPersonalizadaRenderer());
    }

    /**
     * Configura el efecto hover para las filas
     */
    private void configurarHover() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point point = e.getPoint();
                int row = rowAtPoint(point);
                if (row != filaHover) {
                    filaHover = row;
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                filaHover = -1;
                repaint();
            }
        });
    }

    /**
     * Agrega un icono a una columna específica
     * @param columna índice de la columna
     * @param icono icono a mostrar
     */
    public void agregarIconoColumna(int columna, ImageIcon icono) {
        iconosColumnas.put(columna, icono);
    }

    /**
     * Crea un icono de color circular para estados
     * @param color color del icono
     * @param tamaño tamaño del icono
     * @return ImageIcon creado
     */
    private ImageIcon crearIconoColor(Color color, int tamaño) {
        BufferedImage imagen = new BufferedImage(tamaño, tamaño, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = imagen.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.fillOval(0, 0, tamaño, tamaño);
        g2d.dispose();
        return new ImageIcon(imagen);
    }

    /**
     * Crea un JScrollPane personalizado para esta tabla
     * @return JScrollPane configurado
     */
    public JScrollPane crearScrollPane() {
        JScrollPane scrollPane = new JScrollPane(this);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(COLOR_CREMA);

        // Personalizar la barra de desplazamiento vertical
        scrollPane.getVerticalScrollBar().setUI(new ScrollBarPersonalizadaUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));

        // Personalizar la barra de desplazamiento horizontal
        scrollPane.getHorizontalScrollBar().setUI(new ScrollBarPersonalizadaUI());
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));

        return scrollPane;
    }

    /**
     * Renderer personalizado para la cabecera con gradiente
     */
    private class GradientHeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

            // Agregar icono si existe para la columna
            if (iconosColumnas.containsKey(column)) {
                label.setIcon(iconosColumnas.get(column));
                label.setHorizontalTextPosition(SwingConstants.RIGHT);
                label.setIconTextGap(8);
            }

            return new HeaderPanel(label);
        }

        /**
         * Panel para dibujar el gradiente de la cabecera
         */
        private class HeaderPanel extends JPanel {
            private final JLabel label;

            public HeaderPanel(JLabel label) {
                super(new BorderLayout());
                this.label = label;
                add(label, BorderLayout.CENTER);
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dibujar gradiente
                GradientPaint gp = new GradientPaint(
                        0, 0, COLOR_CABECERA_INICIO,
                        0, getHeight(), COLOR_CABECERA_FIN);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Dibujar línea inferior para efecto de sombra
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

                g2d.dispose();
                super.paintComponent(g);
            }
        }
    }

    /**
     * Renderer personalizado para celdas con colores alternos y estados
     */
    private class CeldaPersonalizadaRenderer extends DefaultTableCellRenderer {
        private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            // Configurar borde y alineación
            label.setBorder(new EmptyBorder(0, 5, 0, 5));

            // Definir alineación según el tipo de datos
            if (column == 0) { // ID
                label.setHorizontalAlignment(SwingConstants.CENTER);
            } else if (value instanceof Date) {
                label.setText(formatoFecha.format((Date) value));
                label.setHorizontalAlignment(SwingConstants.CENTER);
            } else if (value instanceof Number) {
                label.setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                label.setHorizontalAlignment(SwingConstants.LEFT);
            }

            // Columna de estado (asumir que es la columna 5 basado en el código original)
            if (column == 5 && value != null) {
                String estado = value.toString();
                switch (estado) {
                    case "Activo":
                        label.setIcon(iconoActivo);
                        break;
                    case "Inactivo":
                        label.setIcon(iconoInactivo);
                        break;
                    case "Bloqueado":
                    case "Suspendido":
                        label.setIcon(iconoBloqueado);
                        break;
                    default:
                        label.setIcon(null);
                }

                label.setHorizontalTextPosition(SwingConstants.RIGHT);
                label.setIconTextGap(8);
            } else {
                label.setIcon(null);
            }

            // Aplicar color según selección y efecto hover
            if (!isSelected) {
                if (row == filaHover) {
                    label.setBackground(COLOR_HOVER);
                } else {
                    // Colores alternos para filas pares e impares
                    label.setBackground(row % 2 == 0 ? COLOR_FILA_PAR : COLOR_FILA_IMPAR);
                }
            }

            return label;
        }
    }

    /**
     * UI personalizada para las barras de desplazamiento
     */
    private class ScrollBarPersonalizadaUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = COLOR_AZUL;
            thumbDarkShadowColor = COLOR_AZUL.darker();
            thumbHighlightColor = COLOR_AZUL.brighter();
            thumbLightShadowColor = COLOR_AZUL;
            trackColor = COLOR_CREMA;
            trackHighlightColor = COLOR_CREMA;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Dibujar thumb con bordes redondeados
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y,
                    thumbBounds.width, thumbBounds.height, 10, 10);

            g2.dispose();
        }
    }
}