package com.dei.cafeteria.util;

import com.dei.cafeteria.modelo.Pago;
import com.dei.cafeteria.modelo.Orden;
import com.dei.cafeteria.modelo.Mesa;
import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.modelo.ItemOrden;
import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.dao.ItemOrdenDAO; // Asumiendo que existe este DAO
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GeneradorPDF {

    private static final float MARGIN = 50;
    private static final float FONT_SIZE_TITLE = 14;
    private static final float FONT_SIZE_SUBTITLE = 12;
    private static final float FONT_SIZE_NORMAL = 10;
    private static final float FONT_SIZE_SMALL = 8;
    private static final float LINE_HEIGHT = 15;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Genera un ticket de pago en formato PDF.
     * @param pago Objeto Pago con la información a mostrar en el ticket
     * @return Ruta del archivo PDF generado
     */
    public String generarTicket(Pago pago) {
        String nombreArchivo = "ticket_" + pago.getOrdenId() + "_" + UUID.randomUUID().toString() + ".pdf";
        String rutaArchivo = System.getProperty("java.io.tmpdir") + File.separator + nombreArchivo;

        try (PDDocument document = new PDDocument()) {
            // Crear página con tamaño de ticket (80mm x 200mm)
            PDPage page = new PDPage(new PDRectangle(226, 566)); // 80mm x 200mm convertido a puntos (1 mm = 2.83 puntos)
            document.addPage(page);

            // Inicializar variables para control de páginas
            float yPosition = page.getMediaBox().getHeight() - MARGIN;
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            try {
                // Cabecera del ticket
                centrarTexto(contentStream, page, "CAFETERÍA DEI", FONT_SIZE_TITLE, yPosition);
                yPosition -= LINE_HEIGHT * 1.5;

                centrarTexto(contentStream, page, "TICKET DE COMPRA", FONT_SIZE_SUBTITLE, yPosition);
                yPosition -= LINE_HEIGHT * 2;

                // Información del ticket
                Orden orden = pago.getOrden();
                Mesa mesa = orden.getMesa();
                Empleado mesero = orden.getMesero();
                Empleado cajero = pago.getCajero();

                escribirTexto(contentStream, "Fecha: " + pago.getFechaHora().format(FORMATTER), FONT_SIZE_NORMAL, MARGIN, yPosition);
                yPosition -= LINE_HEIGHT;

                escribirTexto(contentStream, "Orden #: " + pago.getOrdenId(), FONT_SIZE_NORMAL, MARGIN, yPosition);
                yPosition -= LINE_HEIGHT;

                escribirTexto(contentStream, "Mesa: " + (mesa != null ? mesa.getNumero() : "N/A"), FONT_SIZE_NORMAL, MARGIN, yPosition);
                yPosition -= LINE_HEIGHT;

                escribirTexto(contentStream, "Mesero: " + (mesero != null ? mesero.getNombre() : "N/A"), FONT_SIZE_NORMAL, MARGIN, yPosition);
                yPosition -= LINE_HEIGHT;

                escribirTexto(contentStream, "Cajero: " + (cajero != null ? cajero.getNombre() : "N/A"), FONT_SIZE_NORMAL, MARGIN, yPosition);
                yPosition -= LINE_HEIGHT * 1.5;

                // Línea separadora
                contentStream.moveTo(MARGIN, yPosition);
                contentStream.lineTo(page.getMediaBox().getWidth() - MARGIN, yPosition);
                contentStream.stroke();
                yPosition -= LINE_HEIGHT;

                // Detalle de la orden
                escribirTexto(contentStream, "DETALLE DE LA ORDEN", FONT_SIZE_SUBTITLE, MARGIN, yPosition);
                yPosition -= LINE_HEIGHT * 1.5;

                // Encabezados de columnas
                float colProducto = MARGIN;
                float colCantidad = page.getMediaBox().getWidth() - MARGIN - 100;
                float colPrecio = page.getMediaBox().getWidth() - MARGIN - 50;

                escribirTexto(contentStream, "Producto", FONT_SIZE_SMALL, colProducto, yPosition);
                escribirTexto(contentStream, "Cant", FONT_SIZE_SMALL, colCantidad, yPosition);
                escribirTexto(contentStream, "Precio", FONT_SIZE_SMALL, colPrecio, yPosition);
                yPosition -= LINE_HEIGHT;

                // Obtener los items de la orden
                try {
                    ItemOrdenDAO itemOrdenDAO = new ItemOrdenDAO();
                    List<ItemOrden> items = itemOrdenDAO.buscarPorOrdenID(orden.getId());

                    if (items != null && !items.isEmpty()) {
                        for (ItemOrden item : items) {
                            // Verificamos si necesitamos una nueva página
                            if (yPosition < 100) {
                                // Cerramos el stream actual
                                contentStream.close();

                                // Creamos una nueva página
                                page = new PDPage(new PDRectangle(226, 566));
                                document.addPage(page);

                                // Creamos un nuevo stream para la nueva página
                                contentStream = new PDPageContentStream(document, page);
                                yPosition = page.getMediaBox().getHeight() - MARGIN;
                            }

                            String nombreProducto = item.getProducto().getNombre();
                            if (item.getTamaño() != null) {
                                nombreProducto += " (" + item.getTamaño().getNombre() + ")";
                            }

                            // Si el nombre es muy largo, lo acortamos
                            if (nombreProducto.length() > 15) {
                                nombreProducto = nombreProducto.substring(0, 12) + "...";
                            }

                            escribirTexto(contentStream, nombreProducto, FONT_SIZE_SMALL, colProducto, yPosition);
                            escribirTexto(contentStream, String.format("%.0f", item.getCantidad()), FONT_SIZE_SMALL, colCantidad, yPosition);
                            escribirTexto(contentStream, String.format("$%.2f", item.getTotal()), FONT_SIZE_SMALL, colPrecio, yPosition);
                            yPosition -= LINE_HEIGHT;

                            // Si hay notas sobre el item, las mostramos
                            if (item.getNotas() != null && !item.getNotas().isEmpty()) {
                                escribirTexto(contentStream, "  Nota: " + item.getNotas(), FONT_SIZE_SMALL, colProducto, yPosition);
                                yPosition -= LINE_HEIGHT;
                            }
                        }
                    } else {
                        escribirTexto(contentStream, "No hay detalles disponibles", FONT_SIZE_NORMAL, MARGIN, yPosition);
                        yPosition -= LINE_HEIGHT;
                    }
                } catch (DAOException e) {
                    escribirTexto(contentStream, "Error al cargar detalles", FONT_SIZE_NORMAL, MARGIN, yPosition);
                    yPosition -= LINE_HEIGHT;
                    e.printStackTrace();
                }

                // Línea separadora
                yPosition -= LINE_HEIGHT;
                contentStream.moveTo(MARGIN, yPosition);
                contentStream.lineTo(page.getMediaBox().getWidth() - MARGIN, yPosition);
                contentStream.stroke();
                yPosition -= LINE_HEIGHT;

                // Totales
                float xPosition = page.getMediaBox().getWidth() - MARGIN - 80;

                escribirTexto(contentStream, "Subtotal:", FONT_SIZE_NORMAL, MARGIN, yPosition);
                escribirTexto(contentStream, String.format("$%.2f", orden.getSubtotal()), FONT_SIZE_NORMAL, xPosition, yPosition);
                yPosition -= LINE_HEIGHT;

                escribirTexto(contentStream, "IVA:", FONT_SIZE_NORMAL, MARGIN, yPosition);
                escribirTexto(contentStream, String.format("$%.2f", orden.getIva()), FONT_SIZE_NORMAL, xPosition, yPosition);
                yPosition -= LINE_HEIGHT;

                escribirTexto(contentStream, "Total:", FONT_SIZE_SUBTITLE, MARGIN, yPosition);
                escribirTexto(contentStream, String.format("$%.2f", orden.getTotal()), FONT_SIZE_SUBTITLE, xPosition, yPosition);
                yPosition -= LINE_HEIGHT * 1.5;

                // Información de pago
                escribirTexto(contentStream, "FORMA DE PAGO", FONT_SIZE_NORMAL, MARGIN, yPosition);
                yPosition -= LINE_HEIGHT;

                escribirTexto(contentStream, "Método:", FONT_SIZE_NORMAL, MARGIN, yPosition);
                escribirTexto(contentStream, pago.getMetodoPago().getNombre(), FONT_SIZE_NORMAL, xPosition, yPosition);
                yPosition -= LINE_HEIGHT;

                escribirTexto(contentStream, "Monto pagado:", FONT_SIZE_NORMAL, MARGIN, yPosition);
                escribirTexto(contentStream, String.format("$%.2f", pago.getMonto()), FONT_SIZE_NORMAL, xPosition, yPosition);
                yPosition -= LINE_HEIGHT;

                if (pago.getCambio() != null && pago.getCambio() > 0) {
                    escribirTexto(contentStream, "Cambio:", FONT_SIZE_NORMAL, MARGIN, yPosition);
                    escribirTexto(contentStream, String.format("$%.2f", pago.getCambio()), FONT_SIZE_NORMAL, xPosition, yPosition);
                    yPosition -= LINE_HEIGHT;
                }

                // Referencia de pago si existe
                if (pago.getReferencia() != null && !pago.getReferencia().isEmpty()) {
                    escribirTexto(contentStream, "Referencia:", FONT_SIZE_NORMAL, MARGIN, yPosition);
                    escribirTexto(contentStream, pago.getReferencia(), FONT_SIZE_NORMAL, xPosition, yPosition);
                    yPosition -= LINE_HEIGHT;
                }

                // Línea separadora
                yPosition -= LINE_HEIGHT;
                contentStream.moveTo(MARGIN, yPosition);
                contentStream.lineTo(page.getMediaBox().getWidth() - MARGIN, yPosition);
                contentStream.stroke();
                yPosition -= LINE_HEIGHT;

                // Notas de la orden si existen
                if (orden.getNotas() != null && !orden.getNotas().isEmpty()) {
                    escribirTexto(contentStream, "Notas:", FONT_SIZE_SMALL, MARGIN, yPosition);
                    yPosition -= LINE_HEIGHT;

                    // Dividir notas largas en múltiples líneas
                    String[] notasLines = wrapText(orden.getNotas(), 30);
                    for (String line : notasLines) {
                        escribirTexto(contentStream, line, FONT_SIZE_SMALL, MARGIN, yPosition);
                        yPosition -= LINE_HEIGHT;
                    }

                    yPosition -= LINE_HEIGHT;
                }

                // Pie de página
                centrarTexto(contentStream, page, "¡GRACIAS POR SU COMPRA!", FONT_SIZE_NORMAL, yPosition);
                yPosition -= LINE_HEIGHT;

                centrarTexto(contentStream, page, "Lo esperamos pronto", FONT_SIZE_SMALL, yPosition);
            } finally {
                // Aseguramos que el content stream se cierre correctamente
                if (contentStream != null) {
                    contentStream.close();
                }
            }

            document.save(rutaArchivo);
            return rutaArchivo;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Divide un texto largo en múltiples líneas según un ancho máximo
     * @param texto Texto a dividir
     * @param maxChars Número máximo de caracteres por línea
     * @return Array de strings con el texto dividido
     */
    private String[] wrapText(String texto, int maxChars) {
        List<String> lines = new ArrayList<>();

        if (texto == null || texto.isEmpty()) {
            return new String[0];
        }

        // Dividir primero por saltos de línea existentes
        String[] paragraphs = texto.split("\n");

        for (String paragraph : paragraphs) {
            if (paragraph.length() <= maxChars) {
                lines.add(paragraph);
            } else {
                // Dividir párrafos largos en múltiples líneas
                int startIndex = 0;
                while (startIndex < paragraph.length()) {
                    int endIndex = Math.min(startIndex + maxChars, paragraph.length());

                    // Intentar cortar en un espacio si es posible
                    if (endIndex < paragraph.length()) {
                        int lastSpace = paragraph.substring(startIndex, endIndex).lastIndexOf(' ');
                        if (lastSpace != -1) {
                            endIndex = startIndex + lastSpace;
                        }
                    }

                    lines.add(paragraph.substring(startIndex, endIndex).trim());
                    startIndex = endIndex;
                }
            }
        }

        return lines.toArray(new String[0]);
    }

    private void centrarTexto(PDPageContentStream contentStream, PDPage page, String texto, float fontSize, float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
        float anchoPagina = page.getMediaBox().getWidth();
        float anchoTexto = PDType1Font.HELVETICA_BOLD.getStringWidth(texto) / 1000 * fontSize;
        float x = (anchoPagina - anchoTexto) / 2;
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(texto);
        contentStream.endText();
    }

    private void escribirTexto(PDPageContentStream contentStream, String texto, float fontSize, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(texto);
        contentStream.endText();
    }
}