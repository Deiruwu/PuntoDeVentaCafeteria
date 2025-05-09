package com.dei.cafeteria.dao;

import java.util.List;

/**
 * Interfaz genérica para el patrón DAO.
 * @param <T> Tipo de la entidad
 * @param <K> Tipo del identificador de la entidad
 */
public interface DAO<T, K> {

    /**
     * Guarda una entidad en la base de datos.
     * @param entidad Entidad a guardar
     * @return La entidad guardada (con ID asignado si es nuevo)
     * @throws DAOException Si ocurre un error al guardar
     */
    T guardar(T entidad) throws DAOException;

    /**
     * Actualiza una entidad existente en la base de datos.
     * @param entidad Entidad a actualizar
     * @return La entidad actualizada
     * @throws DAOException Si ocurre un error al actualizar
     */
    T actualizar(T entidad) throws DAOException;

    /**
     * Elimina una entidad de la base de datos por su ID.
     * @param id Identificador de la entidad a eliminar
     * @throws DAOException Si ocurre un error al eliminar
     */
    void eliminar(K id) throws DAOException;

    /**
     * Busca una entidad por su ID.
     * @param id Identificador de la entidad a buscar
     * @return La entidad encontrada o null si no existe
     * @throws DAOException Si ocurre un error al buscar
     */
    T buscarPorId(K id) throws DAOException;

    /**
     * Lista todas las entidades del tipo especificado.
     * @return Lista de entidades
     * @throws DAOException Si ocurre un error al listar
     */
    List<T> listarTodos() throws DAOException;
}