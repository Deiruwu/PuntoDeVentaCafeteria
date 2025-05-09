package com.dei.cafeteria.modelo;

import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pago {
    private Integer id;
    private Orden orden;
    private Empleado cajero;
    private LocalDateTime fechaHora;
    private Double monto;
    private MetodoPago metodoPago;
    private String referencia;
    private Double cambio;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}