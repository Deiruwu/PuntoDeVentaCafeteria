package com.dei.cafeteria.util;

import java.awt.Color;

/**
 * Enumeración que centraliza los colores de la aplicación
 */
public enum ColorPaleta {
    TERRACOTA(new Color(140, 94, 88)),   // #8C5E58
    AZUL(new Color(44, 59, 71)),         // #2C3B47
    CREMA(new Color(232, 218, 203)),     // #E8DACB
    VERDE(new Color(97, 112, 91)),       // #61705B
    AMBAR(new Color(212, 146, 93));      // #D4925D

    private final Color color;

    ColorPaleta(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}