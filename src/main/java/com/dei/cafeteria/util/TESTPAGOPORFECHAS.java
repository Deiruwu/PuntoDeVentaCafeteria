package com.dei.cafeteria.util;

import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.dao.PagoDAO;
import com.dei.cafeteria.modelo.Pago;

import java.util.ArrayList;
import java.util.List;

public class TESTPAGOPORFECHAS {

    public static void main(String[] args) {
        PagoDAO pagoDAO = new PagoDAO();
        List<Pago> pagos = new ArrayList<>();
        try {
            pagos = pagoDAO.buscarPorRangoFechas("2025-05-16 00:00:00","2025-05-17 00:00:00");
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        for (Pago items : pagos){
            System.out.println(items.getId());
        }
    }
}
