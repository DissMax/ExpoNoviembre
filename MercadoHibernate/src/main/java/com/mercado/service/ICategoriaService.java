package com.mercado.service;

import com.mercado.model.Categoria;
import java.util.List;

/**
 * Interfaz de la Capa de Lógica de Negocio (Service) para Categoria.
 * Define las reglas de negocio, validaciones y orquestación con la capa DAO.
 */
public interface ICategoriaService {

    /**
     * Valida y registra una nueva categoría comercial.
     */
    void registrarCategoria(Categoria categoria);

    /**
     * Valida y actualiza los datos de una categoría existente.
     */
    void modificarCategoria(Categoria categoria);

    /**
     * Elimina una categoría por su ID, validando que no tenga productos asociados.
     */
    void eliminarCategoria(Long id);

    /**
     * Obtiene una categoría por su ID.
     */
    Categoria obtenerPorId(Long id);

    /**
     * Obtiene una categoría por su nombre exacto.
     */
    Categoria obtenerPorNombre(String nombre);

    /**
     * Obtiene una categoría por su código.
     */
    Categoria obtenerPorCodigo(String codigo);

    /**
     * Obtiene el listado de todas las categorías disponibles.
     */
    List<Categoria> obtenerTodas();

    /**
     * Inicializa las categorías base del supermercado si la tabla se encuentra vacía.
     */
    void inicializarCategoriasPorDefecto();

    /**
     * Retorna cuántos productos están asociados a una categoría dada.
     */
    long contarProductosPorCategoria(Long categoriaId);
}
