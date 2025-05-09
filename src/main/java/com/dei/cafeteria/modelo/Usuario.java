package com.dei.cafeteria.modelo;

import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    private Integer id;
    private Empleado empleado;
    private String nombreUsuario;
    private String hashContraseña;
    private LocalDateTime ultimoLogin;
    private EstadoUsuario estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
