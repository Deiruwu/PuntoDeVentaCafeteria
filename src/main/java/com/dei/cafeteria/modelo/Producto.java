package com.dei.cafeteria.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false)
    private double precio;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private com.cafeteria.modelo.CategoriaProducto categoria;

    @Column(nullable = false)
    private Timestamp fechaCreacion;

    @Column(nullable = false)
    private Timestamp fechaActualizacion;
}
