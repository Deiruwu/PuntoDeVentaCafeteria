package com.dei.cafeteria.modelo;

import java.sql.Array;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public class Orden {
    private int id;
    private Date fechaHora;
    private Empleado mesero;
    private Mesa mesa;
    private List<ItemOrden> items;

    public Orden(int id, Date fechaHora, Empleado mesero, Mesa mesa, Array items) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.mesero = mesero;
        this.mesa = mesa;
        this.items = (List<ItemOrden> ) items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Empleado getMesero() {
        return mesero;
    }

    public void setMesero(Empleado mesero) {
        this.mesero = mesero;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public List<ItemOrden> getItems() {
        return items;
    }

    public void setItems(List<ItemOrden> items) {
        this.items = items;
    }
}
