package com.dei.cafeteria.modelo;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductoTamañoId {
    private int idProducto;
    private int idTamaño;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductoTamañoId)) return false;
        ProductoTamañoId that = (ProductoTamañoId) o;
        return idProducto == that.idProducto && idTamaño == that.idTamaño;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProducto, idTamaño);
    }
}
