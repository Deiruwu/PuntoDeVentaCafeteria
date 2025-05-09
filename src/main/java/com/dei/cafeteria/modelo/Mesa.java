package com.dei.cafeteria.modelo;

import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Mesa {
    private Integer id;
    private Integer numero;
    private Integer capacidad;
    private EstadoMesa estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
