package com.dei.cafeteria.modelo;

import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoInventario {
    private Integer id;
    private Producto producto;
    private TipoMovimiento tipoMovimiento;
    private Double cantidad;
    private Double stockPrevio;
    private Double stockNuevo;
    private Empleado empleado;
    private String referencia;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}