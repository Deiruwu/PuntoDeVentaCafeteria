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
    private String imagenUrl;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public Empleado(int i, String mesero, String ejemplo, Rol rol) {
    }
}