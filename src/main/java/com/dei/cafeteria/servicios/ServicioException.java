package com.dei.cafeteria.servicios;

/**
 * Excepción específica para los servicios de la aplicación.
 * Se utiliza para encapsular errores de los servicios de negocio.
 *
 * @author DeyCafeteria
 */
public class ServicioException extends Exception {

    /**
     * Constructor con mensaje de error.
     *
     * @param mensaje Descripción del error
     */
    public ServicioException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa del error.
     *
     * @param mensaje Descripción del error
     * @param causa Excepción que causó el error
     */
    public ServicioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}