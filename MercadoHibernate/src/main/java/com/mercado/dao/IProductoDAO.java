package com.mercado.dao;

import com.mercado.model.Producto;
import java.util.List;

/**
 * Interfaz de Acceso a Datos (DAO) para la entidad Producto.
 * Define el contrato de operaciones CRUD y consultas avanzadas con ORM.
 */
public interface IProductoDAO {

    /**
     * Guarda un nuevo producto en la base de datos mediante session.persist().
     */
    void guardar(Producto producto);

    /**
     * Actualiza un producto existente mediante session.merge().
     */
    void actualizar(Producto producto);

    /**
     * Elimina un producto por su clave primaria mediante session.remove().
     */
    void eliminar(Long id);

    /**
     * Busca un producto por su clave primaria.
     */
    Producto buscarPorId(Long id);

    /**
     * Busca un producto por su código único de mercado.
     */
    Producto buscarPorCodigo(String codigo);

    /**
     * Lista todos los productos registrados usando HQL.
     */
    List<Producto> listarTodos();

    /**
     * Búsqueda por coincidencia de nombre (LIKE insensible a mayúsculas/minúsculas).
     */
    List<Producto> buscarPorNombre(String termino);

    /**
     * Filtra los productos pertenecientes a una categoría específica.
     */
    List<Producto> filtrarPorCategoria(String categoria);

    /**
     * Filtro avanzado combinando texto de búsqueda, categoría, rangos de precio y disponibilidad.
     */
    List<Producto> filtrarAvanzado(String nombre, String categoria, Double minPrecio, Double maxPrecio, Boolean soloEnStock);
}
