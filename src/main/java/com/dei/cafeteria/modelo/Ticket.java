package com.dei.cafeteria.modelo;

import java.time.LocalDateTime;
import java.util.Date;

public class Ticket {
    private int id;
    private Orden orden;
    private Date fechaHora;
    private double total;

    public Ticket(int id, Orden orden, Date fechaHora, double total) {
        this.id = id;
        this.orden = orden;
        this.fechaHora = fechaHora;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
