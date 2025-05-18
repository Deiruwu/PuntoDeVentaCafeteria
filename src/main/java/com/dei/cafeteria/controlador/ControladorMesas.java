package com.dei.cafeteria.controlador;

import com.dei.cafeteria.dao.MesaDAO;
import com.dei.cafeteria.dao.EstadoMesaDAO;
import com.dei.cafeteria.modelo.Mesa;
import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.util.ColorPaleta;
import com.dei.cafeteria.vista.VistaMesero;

import java.awt.*;
import java.util.List;

public class ControladorMesas {
    private MesaDAO mesaDAO;

    public ControladorMesas() {
        EstadoMesaDAO estadoMesaDAO = new EstadoMesaDAO();
        this.mesaDAO = new MesaDAO(estadoMesaDAO);
    }

    public List<Mesa> obtenerTodasLasMesas() throws DAOException {
        return mesaDAO.listarTodos();
    }

    public Color getColor(int estadoMesa) {
        return switch (estadoMesa) {
            case 1 -> ColorPaleta.VERDE.getColor();
            case 2 -> ColorPaleta.TERRACOTA.getColor();
            case 3 -> ColorPaleta.AMBAR.getColor();
            default -> Color.GRAY;
        };
    }

    public String getEstadoStr(int estadoMesa) {
        return switch (estadoMesa) {
            case 1 -> "Disponible";
            case 2 -> "Ocupada";
            case 3 -> "Reservada";
            default -> "Desconocido";
        };
    }
}
