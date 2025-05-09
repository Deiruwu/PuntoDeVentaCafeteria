package com.dei.cafeteria.modelo;
// Representa la relación muchos a muchos entre productos y tamaños
import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductoTamaño {
    private ProductoTamañoId id;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
