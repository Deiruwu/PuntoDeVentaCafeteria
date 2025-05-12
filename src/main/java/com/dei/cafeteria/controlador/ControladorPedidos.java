package com.dei.cafeteria.controlador;

import com.dei.cafeteria.dao.*;
import com.dei.cafeteria.modelo.*;

import java.util.List;

public class ControladorPedidos {
    private final OrdenDAO ordenDAO;
    private final ItemOrdenDAO itemOrdenDAO;
    private final ProductoDAO productoDAO;
    private final TamañoProductoDAO tamañoDAO;
    private final MesaDAO mesaDAO;
    private final EstadoMesaDAO estadoMesaDAO;

    public ControladorPedidos() {
        this.ordenDAO = new OrdenDAO();
        this.itemOrdenDAO = new ItemOrdenDAO();
        this.productoDAO = new ProductoDAO();
        this.tamañoDAO = new TamañoProductoDAO();
        this.estadoMesaDAO = new EstadoMesaDAO();
        this.mesaDAO = new MesaDAO(estadoMesaDAO);
    }
}