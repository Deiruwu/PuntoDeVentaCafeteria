package com.dei.cafeteria.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase abstracta para implementar la funcionalidad común de los DAOs.
 * @param <T> Tipo de la entidad
 * @param <K> Tipo del identificador de la entidad
 */
public abstract class AbstractDAO<T, K> implements DAO<T, K> {

    // Ruta de la base de datos SQLite
    private static final String URL_CONEXION = "jdbc:sqlite:data/puntoventa.db";

    /**
     * Obtiene una conexión a la base de datos.
     * @return Conexión a la base de datos
     * @throws SQLException Si ocurre un error al obtener la conexión
     */
    protected Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL_CONEXION);
    }

    /**
     * Cierra recursos de base de datos de manera segura.
     * @param conexion Conexión a cerrar
     * @param statement Statement a cerrar
     * @param resultSet ResultSet a cerrar
     */
    protected void cerrarRecursos(Connection conexion, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                // Loguear el error
                System.err.println("Error al cerrar ResultSet: " + e.getMessage());
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                // Loguear el error
                System.err.println("Error al cerrar Statement: " + e.getMessage());
            }
        }

        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                // Loguear el error
                System.err.println("Error al cerrar Connection: " + e.getMessage());
            }
        }
    }

    /**
     * Ejecuta una operación que retorna un ID generado.
     * @param sql Consulta SQL de inserción
     * @param parametros Parámetros para la consulta SQL
     * @return ID generado por la operación
     * @throws SQLException Si ocurre un error al ejecutar la consulta
     */
    protected int ejecutarInsert(String sql, Object... parametros) throws SQLException {
        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            conexion = obtenerConexion();
            statement = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Asignar parámetros
            for (int i = 0; i < parametros.length; i++) {
                statement.setObject(i + 1, parametros[i]);
            }

            int filasAfectadas = statement.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("La inserción falló, no se modificaron filas.");
            }

            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                throw new SQLException("La inserción falló, no se obtuvo el ID generado.");
            }
        } finally {
            cerrarRecursos(conexion, statement, resultSet);
        }
    }

    /**
     * Ejecuta una operación de actualización o eliminación.
     * @param sql Consulta SQL de actualización o eliminación
     * @param parametros Parámetros para la consulta SQL
     * @return Número de filas afectadas
     * @throws SQLException Si ocurre un error al ejecutar la consulta
     */
    protected int ejecutarUpdate(String sql, Object... parametros) throws SQLException {
        Connection conexion = null;
        PreparedStatement statement = null;

        try {
            conexion = obtenerConexion();
            statement = conexion.prepareStatement(sql);

            // Asignar parámetros
            for (int i = 0; i < parametros.length; i++) {
                statement.setObject(i + 1, parametros[i]);
            }

            return statement.executeUpdate();
        } finally {
            cerrarRecursos(conexion, statement, null);
        }
    }

    /**
     * Ejecuta una consulta SELECT.
     * @param <E> Tipo del resultado
     * @param sql Consulta SQL SELECT
     * @param mapper Función para mapear el ResultSet a objetos
     * @param parametros Parámetros para la consulta SQL
     * @return Lista de objetos resultantes
     * @throws SQLException Si ocurre un error al ejecutar la consulta
     */
    protected <E> List<E> ejecutarQuery(String sql, ResultSetMapper<E> mapper, Object... parametros) throws SQLException {
        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<E> resultados = new ArrayList<>();

        try {
            conexion = obtenerConexion();
            statement = conexion.prepareStatement(sql);

            // Asignar parámetros
            for (int i = 0; i < parametros.length; i++) {
                statement.setObject(i + 1, parametros[i]);
            }

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                resultados.add(mapper.mapRow(resultSet));
            }

            return resultados;
        } finally {
            cerrarRecursos(conexion, statement, resultSet);
        }
    }

    /**
     * Ejecuta una consulta SELECT que devuelve un único resultado.
     * @param <E> Tipo del resultado
     * @param sql Consulta SQL SELECT
     * @param mapper Función para mapear el ResultSet a objetos
     * @param parametros Parámetros para la consulta SQL
     * @return Objeto resultante o null si no se encuentra
     * @throws SQLException Si ocurre un error al ejecutar la consulta
     */
    protected <E> E ejecutarQueryUnico(String sql, ResultSetMapper<E> mapper, Object... parametros) throws SQLException {
        List<E> resultados = ejecutarQuery(sql, mapper, parametros);
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    /**
     * Interfaz funcional para mapear resultados de consultas.
     * @param <E> Tipo del objeto resultante
     */
    protected interface ResultSetMapper<E> {
        /**
         * Mapea una fila del ResultSet a un objeto.
         * @param rs ResultSet posicionado en la fila actual
         * @return Objeto mapeado
         * @throws SQLException Si ocurre un error al acceder a los datos
         */
        E mapRow(ResultSet rs) throws SQLException;
    }
}