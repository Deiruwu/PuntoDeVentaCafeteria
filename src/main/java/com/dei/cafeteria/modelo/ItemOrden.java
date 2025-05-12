package com.dei.cafeteria.modelo;

import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.dao.TamañoProductoDAO;
import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemOrden {
    private Integer id;
    private Orden orden;
    private Producto producto;
    private TamañoProducto tamaño;
    private Double cantidad;
    private Double precioUnitario;
    private Double precioConIva;
    private Double subtotal;
    private Double iva;
    private Double total;
    private String notas;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public ItemOrden(Producto producto, int cantidad, int tamañoProductoId) {
        TamañoProductoDAO tamañoProductoDAO = new TamañoProductoDAO();
        this.producto = producto;
        this.cantidad = Double.valueOf(cantidad);
        try {
            this.tamaño = tamañoProductoDAO.buscarPorId(tamañoProductoId);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }
}