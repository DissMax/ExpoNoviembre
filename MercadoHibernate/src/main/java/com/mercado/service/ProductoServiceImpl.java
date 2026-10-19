package com.mercado.service;

import com.mercado.dao.IProductoDAO;
import com.mercado.dao.ProductoDAOImpl;
import com.mercado.model.Producto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación de la Capa de Servicio (Service Layer).
 * Contiene reglas de validación comercial y encapsula las llamadas al DAO.
 */
public class ProductoServiceImpl implements IProductoService {

    private final IProductoDAO productoDAO;

    // Constructor por defecto instanciando el DAO
    public ProductoServiceImpl() {
        this.productoDAO = new ProductoDAOImpl();
    }

    // Constructor para inyección de dependencias
    public ProductoServiceImpl(IProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    @Override
    public void registrarProducto(Producto producto) {
        // Validaciones de negocio
        validarDatosProducto(producto);

        // Validar unicidad de código
        Producto existente = productoDAO.buscarPorCodigo(producto.getCodigo());
        if (existente != null) {
            throw new IllegalArgumentException("Ya existe un producto registrado con el código: " + producto.getCodigo());
        }

        productoDAO.guardar(producto);
    }

    @Override
    public void modificarProducto(Producto producto) {
        if (producto.getId() == null) {
            throw new IllegalArgumentException("No se puede actualizar un producto sin ID.");
        }
        validarDatosProducto(producto);

        // Validar que el código no colisione con otro producto distinto
        Producto existente = productoDAO.buscarPorCodigo(producto.getCodigo());
        if (existente != null && !existente.getId().equals(producto.getId())) {
            throw new IllegalArgumentException("El código " + producto.getCodigo() + " ya está asignado a otro producto.");
        }

        productoDAO.actualizar(producto);
    }

    @Override
    public void eliminarProducto(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de producto inválido para eliminación.");
        }
        productoDAO.eliminar(id);
    }

    @Override
    public void darDeBajaProducto(Long id) {
        eliminarProducto(id);
    }

    @Override
    public Producto obtenerProductoPorId(Long id) {
        return productoDAO.buscarPorId(id);
    }

    @Override
    public List<Producto> obtenerTodos() {
        return productoDAO.listarTodos();
    }

    @Override
    public List<Producto> buscarPorNombre(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return productoDAO.listarTodos();
        }
        return productoDAO.buscarPorNombre(termino);
    }

    @Override
    public List<Producto> filtrarPorCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty() || categoria.equalsIgnoreCase("TODAS")) {
            return productoDAO.listarTodos();
        }
        return productoDAO.filtrarPorCategoria(categoria);
    }

    @Override
    public List<Producto> filtrarProductos(String nombre, String categoria, Double minPrecio, Double maxPrecio, Boolean soloEnStock) {
        return productoDAO.filtrarAvanzado(nombre, categoria, minPrecio, maxPrecio, soloEnStock);
    }

    @Override
    public Map<String, Object> obtenerEstadisticas() {
        List<Producto> todos = productoDAO.listarTodos();
        int totalProductos = todos.size();
        int totalStock = 0;
        double valorTotal = 0.0;
        int bajoStock = 0;

        for (Producto p : todos) {
            totalStock += (p.getStock() != null ? p.getStock() : 0);
            valorTotal += (p.getPrecio() != null && p.getStock() != null ? p.getPrecio() * p.getStock() : 0);
            if (p.getStock() != null && p.getStock() <= 10) {
                bajoStock++;
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProductos", totalProductos);
        stats.put("totalStock", totalStock);
        stats.put("valorTotalInventario", valorTotal);
        stats.put("productosBajoStock", bajoStock);
        return stats;
    }

    private void validarDatosProducto(Producto p) {
        if (p.getCodigo() == null || p.getCodigo().trim().isEmpty()) {
            throw new IllegalArgumentException("El código del producto es obligatorio.");
        }
        if (p.getNombre() == null || p.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        }
        if (p.getPrecio() == null || p.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio del producto debe ser mayor a 0.");
        }
        if (p.getStock() == null || p.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        if (p.getCategoria() == null || p.getCategoria().getNombre() == null || p.getCategoria().getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar una categoría.");
        }
    }
}
