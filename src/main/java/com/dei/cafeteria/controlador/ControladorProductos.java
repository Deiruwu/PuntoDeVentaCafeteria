package com.dei.cafeteria.controlador;

import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.dao.ProductoDAO;
import com.dei.cafeteria.modelo.Producto;

import java.util.List;

/**
 * Controlador para la gestión de productos
 */
public class ControladorProductos {

    private ProductoDAO productoDAO;

    public ControladorProductos() {
        this.productoDAO = new ProductoDAO();
    }

    /**
     * Obtiene todos los productos disponibles
     * @return Lista de productos
     * @throws DAOException Si ocurre un error en la base de datos
     */
    public List<Producto> obtenerTodosLosProductos() throws DAOException {
        return productoDAO.listarTodos();
    }

    /**
     * Busca productos por el ID de categoría
     * @param categoriaId ID de la categoría
     * @return Lista de productos que pertenecen a la categoría
     * @throws DAOException Si ocurre un error en la base de datos
     */
    public List<Producto> buscarPorCategoriaId(Integer categoriaId) throws DAOException {
        return productoDAO.buscarPorCategoriaId(categoriaId);
    }

    /**
     * Busca productos por nombre (contiene)
     * @param nombre Texto a buscar en el nombre del producto
     * @return Lista de productos que contienen el texto en su nombre
     * @throws DAOException Si ocurre un error en la base de datos
     */
    public List<Producto> buscarPorNombre(String nombre) throws DAOException {
        return productoDAO.buscarPorNombre(nombre);
    }

    /**
     * Obtiene un producto por su ID
     * @param id ID del producto
     * @return Producto encontrado o null si no existe
     * @throws DAOException Si ocurre un error en la base de datos
     */
    public Producto obtenerPorId(Integer id) throws DAOException {
        return productoDAO.buscarPorId(id);
    }

    /**
     * Guarda un nuevo producto o actualiza uno existente
     * @param producto Producto a guardar o actualizar
     * @throws DAOException Si ocurre un error en la base de datos
     */
    public void guardarProducto(Producto producto) throws DAOException {
        if (producto.getId() == null) {
            productoDAO.guardar(producto);
        } else {
            productoDAO.actualizar(producto);
        }
    }

    /**
     * Elimina un producto por su ID
     * @param id ID del producto a eliminar
     * @throws DAOException Si ocurre un error en la base de datos
     */
    public void eliminarProducto(Integer id) throws DAOException {
        productoDAO.eliminar(id);
    }
}