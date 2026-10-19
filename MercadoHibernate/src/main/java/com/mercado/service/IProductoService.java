package com.mercado.service;

import com.mercado.model.Producto;
import java.util.List;
import java.util.Map;

/**
 * Interfaz de la Capa de Lógica de Negocio (Service).
 * Define las operaciones que orquestan validaciones y reglas del mercado.
 */
public interface IProductoService {

    /**
     * Valida y registra un nuevo producto en el catálogo.
     */
    void registrarProducto(Producto producto);

    /**
     * Valida y actualiza los datos de un producto.
     */
    void modificarProducto(Producto producto);

    /**
     * Elimina un producto por su identificador.
     */
    void eliminarProducto(Long id);

    /**
     * @deprecated Usar eliminarProducto(Long id)
     */
    @Deprecated
    default void darDeBajaProducto(Long id) {
        eliminarProducto(id);
    }

    /**
     * Obtiene un producto por su ID.
     */
    Producto obtenerProductoPorId(Long id);

    /**
     * Obtiene todos los productos registrados.
     */
    List<Producto> obtenerTodos();

    /**
     * Búsqueda en tiempo real por nombre de producto.
     */
    List<Producto> buscarPorNombre(String termino);

    /**
     * Filtro por categoría comercial.
     */
    List<Producto> filtrarPorCategoria(String categoria);

    /**
     * Filtro combinado avanzado para la interfaz de usuario.
     */
    List<Producto> filtrarProductos(String nombre, String categoria, Double minPrecio, Double maxPrecio, Boolean soloEnStock);

    /**
     * Calcula estadísticas generales del inventario del mercado (total productos, total stock, valor inventario).
     */
    Map<String, Object> obtenerEstadisticas();
}
