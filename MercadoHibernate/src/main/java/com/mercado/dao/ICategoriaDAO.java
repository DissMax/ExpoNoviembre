package com.mercado.dao;

import com.mercado.model.Categoria;
import java.util.List;

/**
 * Interfaz de Acceso a Datos (DAO) para la entidad Categoria.
 * Define las operaciones CRUD y consultas HQL gestionadas con Hibernate ORM.
 */
public interface ICategoriaDAO {

    /**
     * Guarda una nueva categoría en la base de datos mediante session.persist().
     */
    void guardar(Categoria categoria);

    /**
     * Actualiza una categoría existente mediante session.merge().
     */
    void actualizar(Categoria categoria);

    /**
     * Elimina una categoría por su ID mediante session.remove().
     */
    void eliminar(Long id);

    /**
     * Busca una categoría por su clave primaria.
     */
    Categoria buscarPorId(Long id);

    /**
     * Busca una categoría por su nombre exacto (case-insensitive).
     */
    Categoria buscarPorNombre(String nombre);

    /**
     * Busca una categoría por su código único (ej: "CAT-001").
     */
    Categoria buscarPorCodigo(String codigo);

    /**
     * Obtiene el listado completo de categorías ordenadas alfabéticamente.
     */
    List<Categoria> listarTodas();

    /**
     * Cuenta cuántos productos están asociados a una categoría dada.
     * Útil para validar restricciones de integridad referencial antes de eliminar.
     */
    long contarProductosPorCategoria(Long categoriaId);
}
