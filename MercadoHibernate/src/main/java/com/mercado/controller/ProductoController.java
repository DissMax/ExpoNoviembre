package com.mercado.controller;

import com.mercado.model.Producto;
import com.mercado.service.IProductoService;
import com.mercado.service.ProductoServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * Capa de Controlador (Controller Layer).
 * Orquesta la interacción entre la interfaz (UI / Vista) y la Capa de Servicio.
 */
public class ProductoController {

    private final IProductoService productoService;

    public ProductoController() {
        this.productoService = new ProductoServiceImpl();
    }

    public ProductoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Acción: Listar todo el catálogo de productos.
     */
    public List<Producto> listarProductos() {
        return productoService.obtenerTodos();
    }

    /**
     * Acción: Buscar producto por ID.
     */
    public Producto buscarPorId(Long id) {
        return productoService.obtenerProductoPorId(id);
    }

    /**
     * Acción: Buscar productos por nombre o término clave.
     */
    public List<Producto> buscarPorNombre(String termino) {
        return productoService.buscarPorNombre(termino);
    }

    /**
     * Acción: Filtrar productos por categoría.
     */
    public List<Producto> filtrarPorCategoria(String categoria) {
        return productoService.filtrarPorCategoria(categoria);
    }

    /**
     * Acción: Filtro combinado avanzado para la barra de búsqueda y filtros laterales.
     */
    public List<Producto> filtrarAvanzado(String nombre, String categoria, Double minPrecio, Double maxPrecio, Boolean soloEnStock) {
        return productoService.filtrarProductos(nombre, categoria, minPrecio, maxPrecio, soloEnStock);
    }

    /**
     * Acción: Registrar nuevo producto con manejo de errores de validación.
     */
    public String registrarProducto(Producto producto) {
        try {
            productoService.registrarProducto(producto);
            return "SUCCESS: Producto '" + producto.getNombre() + "' registrado correctamente con código " + producto.getCodigo();
        } catch (IllegalArgumentException e) {
            return "VALIDATION_ERROR: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR: Fallo al guardar en la base de datos: " + e.getMessage();
        }
    }

    /**
     * Acción: Modificar producto existente.
     */
    public String actualizarProducto(Producto producto) {
        try {
            productoService.modificarProducto(producto);
            return "SUCCESS: Producto '" + producto.getNombre() + "' actualizado exitosamente.";
        } catch (IllegalArgumentException e) {
            return "VALIDATION_ERROR: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR: Fallo al actualizar producto: " + e.getMessage();
        }
    }

    /**
     * Acción: Eliminar producto.
     */
    public String eliminarProducto(Long id) {
        try {
            productoService.eliminarProducto(id);
            return "SUCCESS: Producto con ID " + id + " eliminado satisfactoriamente.";
        } catch (Exception e) {
            return "ERROR: No se pudo eliminar el producto: " + e.getMessage();
        }
    }

    /**
     * Acción: Obtener resumen estadístico del inventario.
     */
    public Map<String, Object> obtenerEstadisticas() {
        return productoService.obtenerEstadisticas();
    }
}
