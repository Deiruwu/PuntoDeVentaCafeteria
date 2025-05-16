package com.dei.cafeteria.modelo;

import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Empleado {
    private Integer id;
    private String nombre;
    private String apellido;
    private Rol rol;
    @Builder.Default
    private String imagenUrl = "/imagenes/empleados/default.png";
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    @Override
    public String toString() {
        return nombre + " " + apellido;
    }
}