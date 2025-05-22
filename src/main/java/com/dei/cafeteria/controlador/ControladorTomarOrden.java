package com.dei.cafeteria.controlador;

import com.dei.cafeteria.dao.OrdenDAO;

public class ControladorTomarOrden {
    OrdenDAO ordenDAO;

    public ControladorTomarOrden() {
        ordenDAO = new OrdenDAO();
    }
}
