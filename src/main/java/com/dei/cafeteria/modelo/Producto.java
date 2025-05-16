package com.dei.cafeteria.modelo;

import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Producto {
    private Integer id;
    private String nombre;
    private Double precioBase;
    private Boolean aplicaIva;
    private String descripcion;
    private CategoriaProducto categoria;
    private Boolean disponible;
    private Double stockActual;
    private Double stockMinimo;
    @Builder.Default
    private String imagenUrl = "/imagenes/productos/default.png";
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    @Override
    public String toString() {
        return nombre;
    }
}