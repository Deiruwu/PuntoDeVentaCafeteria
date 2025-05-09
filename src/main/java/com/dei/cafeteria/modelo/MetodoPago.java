package com.dei.cafeteria.modelo;

import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetodoPago {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Boolean requiereReferencia;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}