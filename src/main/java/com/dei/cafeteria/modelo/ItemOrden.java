package com.dei.cafeteria.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "item_orden")
public class ItemOrden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "tamaño_id", nullable = false)
    private TamañoProducto tamaño;

    @Column(nullable = false)
    private double cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private double precioUnitario;

    @Column(name = "precio_con_iva", nullable = false)
    private double precioConIva;

    @Column(nullable = false)
    private double subtotal;

    @Column(nullable = false)
    private double iva;

    @Column(nullable = false)
    private double total;

    private String notas;

    @Column(name = "fecha_creacion", nullable = false)
    private Timestamp fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private Timestamp fechaActualizacion;
}
