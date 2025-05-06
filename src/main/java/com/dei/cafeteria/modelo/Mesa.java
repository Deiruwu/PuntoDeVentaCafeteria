package com.dei.cafeteria.modelo;

public class Mesa {
    private int id;
    private int numero;
    private boolean disponible;

    public Mesa(int id, int numero, boolean disponible) {
        this.id = id;
        this.numero = numero;
        this.disponible = disponible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
