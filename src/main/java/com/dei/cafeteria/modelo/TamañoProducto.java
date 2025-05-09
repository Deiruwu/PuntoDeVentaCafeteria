package com.dei.cafeteria.modelo;
//
import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TamañoProducto {
    private Integer id;
    private String nombre;
    private Double factorPrecio;
    private Boolean esPorcion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}