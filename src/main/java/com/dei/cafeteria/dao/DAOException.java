package com.dei.cafeteria.dao;

/**
 * Excepción personalizada para manejar errores en la capa DAO.
 */
public class DAOException extends Exception {

    /**
     * Constructor con mensaje de error.
     * @param mensaje Mensaje que describe el error
     */
    public DAOException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa del error.
     * @param mensaje Mensaje que describe el error
     * @param causa Excepción que originó el error
     */
    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}