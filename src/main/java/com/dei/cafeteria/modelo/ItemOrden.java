package com.dei.cafeteria.modelo;

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
}