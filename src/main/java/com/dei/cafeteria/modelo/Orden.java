package com.dei.cafeteria.modelo;

import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Orden {
    private Integer id;
    private LocalDateTime fechaHora;
    private Mesa mesa;
    private Empleado mesero;
    private EstadoOrden estado;
    private Double subtotal;
    private Double iva;
    private Double total;
    private String notas;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}